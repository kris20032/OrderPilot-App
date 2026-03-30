package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.di.AppLog
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform
import com.courierassist.app.pipeline.PipelineOrchestrator
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.math.abs

class CourierAccessibilityService : AccessibilityService() {

    private lateinit var pipeline: PipelineOrchestrator
    private val throttlers = mutableMapOf<String, EventThrottler>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val lastResults = ConcurrentHashMap<Platform, AnalysisResult>()
    private val lastResultTimes = ConcurrentHashMap<Platform, Long>()
    private val resultExpiryMs = 60_000L
    private var lastWindowDiagTime = 0L
    @Volatile private var lastUberEventTime = 0L
    private var uberWatchJob: Job? = null

    override fun onServiceConnected() {
        pipeline = ServiceLocator.pipelineOrchestrator
        isConnected = true
        AppLog.d(AppLog.TAG_SERVICE, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        AppLog.d(AppLog.TAG_SERVICE, "RAW event: pkg=$pkg type=${event.eventType}")
        if (pkg !in watchedPackages) {
            return
        }
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        if (isUserStopped) return

        AppLog.d(AppLog.TAG_SERVICE, "Event from $pkg")

        // Uber watch mode: aktualizuj timestamp i uruchom periodic check
        if (pkg == "com.ubercab.driver") {
            lastUberEventTime = System.currentTimeMillis()
            startUberWatchIfNeeded()
        }

        val throttler = throttlers.getOrPut(pkg) { EventThrottler() }

        // Glovo/Bolt: accessibility tree first (natywne UI — tekst widoczny w drzewie)
        val platform = ServiceLocator.parserRegistry.getParser(pkg)?.platform
        if (platform == com.courierassist.app.domain.Platform.GLOVO || platform == com.courierassist.app.domain.Platform.BOLT) {
            throttler.onEvent(scope) { processViaAccessibilityTree(pkg) }
            return
        }

        // Screenshot pipeline (Uber/Wolt) — ochrona przed cross-contamination.
        // takeScreenshot() robi zrzut CAŁEGO ekranu, więc jeśli np. Wolt generuje eventy
        // w tle a na ekranie jest popup Ubera — WoltOcrParser sparsuje go jako ofertę Wolt.
        //
        // Uber pokazuje popupy jako overlay NAD KAŻDĄ apką (launcher, Wolt, bank itp.)
        // więc eventów z Ubera NIGDY nie skipujemy — popup jest zawsze widoczny.
        // Skipujemy TYLKO eventy z Wolta gdy foreground to inna apka kurierska,
        // bo Wolt generuje "szum" (eventy 2048) bez widocznego popupu.
        if (pkg != "com.ubercab.driver") {
            val activePackage = rootInActiveWindow?.packageName?.toString()
            if (activePackage != null && activePackage != pkg && activePackage in courierPackages) {
                AppLog.d(AppLog.TAG_SERVICE, "Skipping screenshot for $pkg — foreground is rival platform $activePackage")
                return
            }
        }

        // Primary: MediaProjection pipeline (jeśli aktywna) + Fallback: takeScreenshot (API 30+)
        throttler.onEvent(scope) {
            // Diagnostyka: loguj okna Ubera gdy user jest w apce (throttle 10s)
            if (pkg == "com.ubercab.driver") {
                logUberWindowDiagnostics()
            }

            if (isMediaProjectionAvailable()) {
                pipeline.process(pkg)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                processViaScreenshot(pkg)
            }

            // Spaced retries dla Ubera.
            // React Native popup potrzebuje 50-2500ms na rendering po WINDOW_STATE_CHANGED.
            // Pierwszy screenshot (powyżej) łapie natychmiastowy scenariusz.
            // Retry co 600ms pokrywa okno 600-2400ms bez errorCode=3 (Android rate-limit).
            // Early exit: jeśli belka się pokazała → stop.
            val plat = ServiceLocator.parserRegistry.getParser(pkg)?.platform
            if (plat == Platform.UBER && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val maxRetries = 4
                for (i in 1..maxRetries) {
                    delay(600) // 600ms odstęp — unika errorCode=3 od Androida
                    if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) {
                        AppLog.d(AppLog.TAG_SERVICE, "Uber: overlay shown after $i retries — stopping")
                        break
                    }
                    AppLog.d(AppLog.TAG_SERVICE, "Uber: spaced retry $i/$maxRetries (T+${i * 600}ms)")
                    processViaScreenshot(pkg, retryIndex = i)
                }
            }
        }
    }

    /**
     * Per-platform duplicate check: porównuje nowy wynik z ostatnim wynikiem
     * DLA TEJ SAMEJ platformy. Zapobiega niepotrzebnym aktualizacjom belki.
     */
    private fun isSameAsPrevious(platform: Platform, result: AnalysisResult): Boolean {
        val now = System.currentTimeMillis()
        val lastTime = lastResultTimes[platform] ?: 0L
        if (now - lastTime > resultExpiryMs) lastResults.remove(platform)

        val prev = lastResults[platform]
        if (prev != null &&
            result.level == prev.level &&
            result.offer.amount == prev.offer.amount &&
            result.offer.estimatedMinutes == prev.offer.estimatedMinutes &&
            result.offer.distanceKm == prev.offer.distanceKm) return true

        lastResults[platform] = result
        lastResultTimes[platform] = now
        return false
    }

    /**
     * Cross-platform duplicate check z tolerancją.
     * Jeśli inna platforma już wyświetla belkę z podobnymi danymi (±1 min, ±0.5 km),
     * to prawdopodobnie OCR przeczytał cudzą belkę/ekran → skip.
     * Dotyczy TYLKO aktualizacji (gdy belka dla tej platformy już istnieje).
     */
    private fun isCrossPlatformDuplicate(offer: Offer): Boolean {
        val activeOffers = ServiceLocator.overlayManager.getActiveOffers()

        // Sprawdź czy INNA platforma ma już belkę z takimi samymi danymi (contamination)
        for ((otherPlatform, otherOffer) in activeOffers) {
            if (otherPlatform == offer.platform) continue

            val minutesClose = abs(offer.estimatedMinutes - otherOffer.estimatedMinutes) <= 1
            val distanceClose = if (offer.distanceKm != null && otherOffer.distanceKm != null) {
                abs(offer.distanceKm - otherOffer.distanceKm) <= 0.5
            } else {
                // Jeśli brak dystansu — porównaj kwotę z tolerancją
                abs(offer.amount - otherOffer.amount) < 0.5
            }

            if (minutesClose && distanceClose) {
                AppLog.d(AppLog.TAG_SERVICE, "Cross-platform duplicate: ${offer.platform} data matches ${otherPlatform} bar (${offer.estimatedMinutes}min/${offer.distanceKm}km ≈ ${otherOffer.estimatedMinutes}min/${otherOffer.distanceKm}km) — skipping")
                return true
            }
        }
        return false
    }

    private suspend fun processViaAccessibilityTree(packageName: String) {
        try {
            val root = rootInActiveWindow ?: run {
                AppLog.w(AppLog.TAG_SERVICE, "Tree: rootInActiveWindow null, falling back to screenshot")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) processViaScreenshot(packageName)
                return
            }
            val text = AccessibilityTextCollector.collectText(root)
            root.recycle()
            AppLog.d(AppLog.TAG_SERVICE, "Tree text ($packageName): ${text.take(200)}")

            val lines = text.lines().filter { it.isNotBlank() }
            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: return
            val offer = parser.parse(lines)

            if (offer == null) {
                AppLog.d(AppLog.TAG_SERVICE, "Tree: parse failed for $packageName, falling back to screenshot")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) processViaScreenshot(packageName)
                return
            }

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            // Cross-platform duplicate check
            if (isCrossPlatformDuplicate(offer)) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Tree result ($packageName): zlPerHour=${result.zlPerHour} zlPerKm=${result.zlPerKm} → ${result.level}")

            if (isSameAsPrevious(offer.platform, result)) return

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L, offer.platform)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Tree error ($packageName): ${e.message}")
        }
    }

    private fun isMediaProjectionAvailable(): Boolean {
        val service = ScreenCaptureService.instance ?: return false
        return service.isReady() && !ScreenCaptureService.isProjectionLost
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun processViaScreenshot(packageName: String, retryIndex: Int = 0) {
        try {
            val screenOn = (getSystemService(POWER_SERVICE) as? PowerManager)?.isInteractive ?: true
            AppLog.d(AppLog.TAG_SERVICE, "Taking screenshot via AccessibilityService API (retry=$retryIndex, screenOn=$screenOn)")
            val bitmap = withTimeoutOrNull(3000L) { takeScreenshotSuspend() } ?: run {
                AppLog.w(AppLog.TAG_SERVICE, "Screenshot returned null or timed out (retry=$retryIndex)")
                return
            }

            AppLog.d(AppLog.TAG_SERVICE, "Screenshot bitmap: ${bitmap.width}x${bitmap.height} (retry=$retryIndex)")

            // Crop dolne 60% — belka jest na górze, popup zlecenia na dole
            val startY = (bitmap.height * 0.4f).toInt()
            val croppedBitmap = Bitmap.createBitmap(bitmap, 0, startY, bitmap.width, bitmap.height - startY)

            val lines = ServiceLocator.ocrEngine.recognize(croppedBitmap)
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot OCR: ${lines.size} lines → ${lines.take(3)} (retry=$retryIndex)")

            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: run {
                bitmap.recycle()
                croppedBitmap.recycle()
                return
            }
            val offer = parser.parse(lines)

            if (offer == null) {
                AppLog.d(AppLog.TAG_SERVICE, "Screenshot: parser returned null (retry=$retryIndex)")
                // Debug: zapisz screenshot gdy parser nie rozpoznał oferty (tylko retry 0 — pierwszy w batchu)
                if (retryIndex == 0) {
                    val ts = SimpleDateFormat("HHmmss", Locale.US).format(Date())
                    saveDebugScreenshot(bitmap, "debug_full_r${retryIndex}_$ts")
                    saveDebugScreenshot(croppedBitmap, "debug_crop_r${retryIndex}_$ts")
                    AppLog.d(AppLog.TAG_SERVICE, "Debug screenshots saved: debug_full_r${retryIndex}_$ts.png")
                }
                bitmap.recycle()
                croppedBitmap.recycle()
                return
            }

            bitmap.recycle()
            croppedBitmap.recycle()

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            // Cross-platform duplicate check
            if (isCrossPlatformDuplicate(offer)) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot result: ${result.zlPerHour} zł/h → ${result.level} (retry=$retryIndex)")

            if (isSameAsPrevious(offer.platform, result)) return

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L, offer.platform)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Screenshot fallback error (retry=$retryIndex): ${e.message}")
        }
    }

    /**
     * Uber watch mode — safety net na wypadek opóźnionych/brakujących eventów.
     * Gdy Uber generuje eventy, uruchamiamy periodic check co 2.5s.
     * Jeśli belka NIE jest widoczna → robimy screenshot.
     * Zatrzymuje się po 15s bez eventów Ubera.
     */
    private fun startUberWatchIfNeeded() {
        if (uberWatchJob?.isActive == true) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        uberWatchJob = scope.launch {
            AppLog.d(AppLog.TAG_SERVICE, "Uber watch: started")
            while (isActive) {
                delay(2500)
                val sinceLastEvent = System.currentTimeMillis() - lastUberEventTime
                if (sinceLastEvent > 15_000) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: no events for ${sinceLastEvent / 1000}s, stopping")
                    break
                }
                // Jeśli belka Ubera jest już widoczna — nie robimy screenshota
                if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) continue
                // Jeśli MediaProjection aktywna — użyj pipeline zamiast screenshot
                if (isMediaProjectionAvailable()) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: periodic check via pipeline")
                    pipeline.process("com.ubercab.driver")
                } else {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: periodic check via screenshot")
                    processViaScreenshot("com.ubercab.driver", retryIndex = -1)
                }
            }
            AppLog.d(AppLog.TAG_SERVICE, "Uber watch: stopped")
        }
    }

    /**
     * Diagnostyka okien Ubera — loguje wszystkie okna na ekranie i ich tekst.
     * Throttle: max raz na 10 sekund żeby nie zalewać logów.
     * Cel: sprawdzić czy popup zlecenia Ubera jest osobnym oknem
     * i czy accessibility tree eksponuje tekst popupu.
     */
    private fun logUberWindowDiagnostics() {
        val now = System.currentTimeMillis()
        if (now - lastWindowDiagTime < 10_000L) return
        lastWindowDiagTime = now

        try {
            val allWindows = windows ?: return
            AppLog.d(AppLog.TAG_SERVICE, "=== Uber Window Diagnostics (${allWindows.size} windows) ===")
            for ((i, w) in allWindows.withIndex()) {
                val root = w.root
                val pkg = root?.packageName?.toString() ?: "null"
                AppLog.d(AppLog.TAG_SERVICE, "  Window[$i]: type=${w.type}, layer=${w.layer}, pkg=$pkg")
                if (root != null && pkg == "com.ubercab.driver") {
                    val text = AccessibilityTextCollector.collectText(root)
                    val preview = text.take(300).replace("\n", " | ")
                    AppLog.d(AppLog.TAG_SERVICE, "  Window[$i] Uber text: $preview")
                    root.recycle()
                } else {
                    root?.recycle()
                }
            }
            AppLog.d(AppLog.TAG_SERVICE, "=== End Window Diagnostics ===")
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Window diagnostics error: ${e.message}")
        }
    }

    private fun saveDebugScreenshot(bitmap: Bitmap, name: String) {
        try {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, "${name}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Debug screenshot save failed: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun takeScreenshotSuspend(): Bitmap? = suspendCancellableCoroutine { cont ->
        takeScreenshot(
            Display.DEFAULT_DISPLAY,
            mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                        ?.copy(Bitmap.Config.ARGB_8888, false)
                    screenshot.hardwareBuffer.close()
                    cont.resume(bitmap)
                }
                override fun onFailure(errorCode: Int) {
                    AppLog.w(AppLog.TAG_SERVICE, "takeScreenshot failed: errorCode=$errorCode")
                    cont.resume(null)
                }
            }
        )
    }

    override fun onInterrupt() {}

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        isUserStopped = true
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            try { ServiceLocator.overlayManager.hide() } catch (_: Exception) {}
        }
        AppLog.d(AppLog.TAG_SERVICE, "App removed from recents — stopping monitoring")
    }

    override fun onDestroy() {
        isConnected = false
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        @Volatile
        var isConnected = false
            private set

        @Volatile
        var isUserStopped = false

        private val watchedPackages: Set<String>
            get() = ServiceLocator.parserRegistry.getAllWatchedPackages()

        /** Paczki kurierskie — blokujemy screenshot tylko gdy foreground to INNA z tych apek */
        private val courierPackages = setOf(
            "com.ubercab.driver",
            "com.wolt.courierapp",
            "com.logistics.rider.glovo",
            "com.bolt.deliverycourier"
        )
    }
}
