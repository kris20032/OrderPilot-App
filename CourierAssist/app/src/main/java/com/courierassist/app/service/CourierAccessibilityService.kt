package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.PowerManager
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import androidx.annotation.RequiresApi
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.di.AppLog
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform
import com.courierassist.app.pipeline.OfferDuplicateChecker
import com.courierassist.app.pipeline.PipelineOrchestrator
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

class CourierAccessibilityService : AccessibilityService() {

    private lateinit var pipeline: PipelineOrchestrator
    private val throttlers = mutableMapOf<String, EventThrottler>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val lastResults = ConcurrentHashMap<Platform, AnalysisResult>()
    private val lastResultTimes = ConcurrentHashMap<Platform, Long>()
    private val resultExpiryMs = 60_000L
    private var lastWindowDiagTime = 0L
    @Volatile private var lastUberEventTime = 0L
    @Volatile private var isRetrying = false
    @Volatile private var uberWatchJob: Job? = null

    override fun onServiceConnected() {
        pipeline = ServiceLocator.pipelineOrchestrator
        isConnected = true
        AppLog.d(AppLog.TAG_SERVICE, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (!::pipeline.isInitialized) return // event przed onServiceConnected()
        if (isUserStopped) return

        // TYPE_WINDOWS_CHANGED = event SYSTEMOWY (WindowManager) — nowe okno pojawiło się/zniknęło.
        // packageName jest null bo pochodzi z systemu, nie z aplikacji.
        // Sprawdzamy getWindows() czy jest overlay Ubera → łapie popupy nad Samsung launcher
        // gdzie TYPE_WINDOW_STATE_CHANGED nie przychodzi.
        if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            handleWindowsChanged()
            return
        }

        val pkg = event.packageName?.toString() ?: return
        AppLog.d(AppLog.TAG_SERVICE, "RAW event: pkg=$pkg type=${event.eventType}")
        if (pkg !in watchedPackages) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

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

        // Uber false trigger reduction: jeśli event to CONTENT_CHANGED (mapa/UI scroll)
        // i Uber NIE jest foreground i nie ma overlay popupu → skip screenshot.
        // Gdy Uber JEST foreground: popup jest WEWNĄTRZ okna apki (type=1, nie overlay),
        // więc hasUberOverlayWindow() zwraca false — musimy przepuścić event do throttlera.
        // Throttler ogranicza do 1 screenshot/1.6s, parser odfiltruje mapę (zwróci null).
        if (pkg == "com.ubercab.driver" && event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (!isUberForeground() && !hasUberOverlayWindow()) {
                AppLog.d(AppLog.TAG_SERVICE, "Uber: CONTENT_CHANGED but no overlay window and not foreground — skipping")
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

            // Spaced retries dla platform screenshot-owych (Uber, Wolt).
            // Popup może nie być wyrenderowany w momencie pierwszego screenshota.
            // Uber (React Native): 50-2500ms na rendering → 4 retries.
            // Wolt (natywne UI): szybszy rendering → 2 retries (safety net).
            // Retry co 600ms unika errorCode=3 (Android rate-limit na takeScreenshot).
            // Early exit: jeśli belka się pokazała → stop (bez marnowania zasobów).
            val plat = ServiceLocator.parserRegistry.getParser(pkg)?.platform
            if ((plat == Platform.UBER || plat == Platform.WOLT) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val maxRetries = if (plat == Platform.UBER) UBER_MAX_RETRIES else WOLT_MAX_RETRIES
                isRetrying = true
                try {
                    for (i in 1..maxRetries) {
                        delay(RETRY_DELAY_MS)
                        if (ServiceLocator.overlayManager.getActiveOffers()[plat] != null) {
                            AppLog.d(AppLog.TAG_SERVICE, "${plat.name}: overlay shown after $i retries — stopping")
                            break
                        }
                        // Uber: sprawdź czy overlay okno nadal istnieje (user mógł przełączyć apkę)
                        if (plat == Platform.UBER && !hasUberOverlayWindow()) {
                            AppLog.d(AppLog.TAG_SERVICE, "UBER: overlay window gone during retries — stopping")
                            break
                        }
                        AppLog.d(AppLog.TAG_SERVICE, "${plat.name}: spaced retry $i/$maxRetries (T+${i * RETRY_DELAY_MS}ms)")
                        processViaScreenshot(pkg, retryIndex = i)
                    }
                } finally {
                    isRetrying = false
                }
            }
        }
    }

    /**
     * TYPE_WINDOWS_CHANGED handler — event systemowy (WindowManager).
     * Sprawdza getWindows() czy pojawiło się okno overlay Ubera.
     * Łapie popupy nad Samsung launcher, gdzie TYPE_WINDOW_STATE_CHANGED nie przychodzi.
     */
    private fun handleWindowsChanged() {
        if (isRetrying) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        if (!hasUberOverlayWindow()) return // brak overlay okna Ubera

        // Jeśli belka Ubera już widoczna — nie robimy nic
        if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) return

        AppLog.d(AppLog.TAG_SERVICE, "WINDOWS_CHANGED: detected Uber overlay window — triggering screenshot")
        lastUberEventTime = System.currentTimeMillis()
        startUberWatchIfNeeded()

        val throttler = throttlers.getOrPut("com.ubercab.driver") { EventThrottler() }
        throttler.onEvent(scope) {
            logUberWindowDiagnostics()
            if (isMediaProjectionAvailable()) {
                pipeline.process("com.ubercab.driver")
            } else {
                processViaScreenshot("com.ubercab.driver")
            }

            // Spaced retries — popup React Native może potrzebować czasu na rendering
            isRetrying = true
            try {
                for (i in 1..UBER_MAX_RETRIES) {
                    delay(RETRY_DELAY_MS)
                    if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) {
                        AppLog.d(AppLog.TAG_SERVICE, "UBER: overlay shown after $i retries (WINDOWS_CHANGED) — stopping")
                        break
                    }
                    if (!hasUberOverlayWindow()) {
                        AppLog.d(AppLog.TAG_SERVICE, "UBER: overlay window gone during retries (WINDOWS_CHANGED) — stopping")
                        break
                    }
                    AppLog.d(AppLog.TAG_SERVICE, "UBER: spaced retry $i/$UBER_MAX_RETRIES via WINDOWS_CHANGED (T+${i * RETRY_DELAY_MS}ms)")
                    processViaScreenshot("com.ubercab.driver", retryIndex = i)
                }
            } finally {
                isRetrying = false
            }
        }
    }

    /**
     * Sprawdza czy na ekranie jest overlay okno Ubera (popup zlecenia).
     * Z logów wiemy:
     *   - Mapa (bez popupu): Window[1] type=1 pkg=com.ubercab.driver (type=1 = APPLICATION)
     *   - Popup nad WhatsApp: Window[1] type=3 pkg=com.ubercab.driver (type=3 = overlay)
     * Popup zlecenia = okno Ubera z type != TYPE_APPLICATION (1).
     * Gdy popup jest nad inną apką, główne okno Ubera nie jest widoczne,
     * więc liczyć okna >= 2 nie działa — trzeba sprawdzać typ.
     */
    /**
     * Sprawdza czy Uber jest aktywną (foreground) apką na ekranie.
     * Gdy Uber jest foreground, popup zlecenia jest renderowany WEWNĄTRZ okna apki (type=1),
     * nie jako osobne overlay okno (type=3) — więc hasUberOverlayWindow() zwraca false.
     */
    private fun isUberForeground(): Boolean {
        return try {
            rootInActiveWindow?.packageName?.toString() == "com.ubercab.driver"
        } catch (e: Exception) {
            false
        }
    }

    private fun hasUberOverlayWindow(): Boolean {
        return try {
            windows?.any { w ->
                val root = w.root
                val pkg = try { root?.packageName?.toString() } finally { root?.recycle() }
                pkg == "com.ubercab.driver" && w.type != AccessibilityWindowInfo.TYPE_APPLICATION
            } ?: false
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "hasUberOverlayWindow error: ${e.message}")
            false
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

    private suspend fun processViaAccessibilityTree(packageName: String) {
        try {
            val root = rootInActiveWindow ?: run {
                AppLog.w(AppLog.TAG_SERVICE, "Tree: rootInActiveWindow null, falling back to screenshot")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) processViaScreenshot(packageName)
                return
            }
            val text: String
            try {
                text = AccessibilityTextCollector.collectText(root)
            } finally {
                root.recycle()
            }
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
            if (OfferDuplicateChecker.isCrossPlatformDuplicate(offer, ServiceLocator.overlayManager.getActiveOffers())) return

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
        var bitmap: Bitmap? = null
        var croppedBitmap: Bitmap? = null
        try {
            val screenOn = (getSystemService(POWER_SERVICE) as? PowerManager)?.isInteractive ?: true
            AppLog.d(AppLog.TAG_SERVICE, "Taking screenshot via AccessibilityService API (retry=$retryIndex, screenOn=$screenOn)")
            bitmap = withTimeoutOrNull(SCREENSHOT_TIMEOUT_MS) { takeScreenshotSuspend() } ?: run {
                AppLog.w(AppLog.TAG_SERVICE, "Screenshot returned null or timed out (retry=$retryIndex)")
                return
            }

            AppLog.d(AppLog.TAG_SERVICE, "Screenshot bitmap: ${bitmap.width}x${bitmap.height} (retry=$retryIndex)")

            // Crop dolne 60% — belka jest na górze, popup zlecenia na dole
            val startY = (bitmap.height * CROP_TOP_RATIO).toInt()
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, startY, bitmap.width, bitmap.height - startY)

            val lines = ServiceLocator.ocrEngine.recognize(croppedBitmap)
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot OCR: ${lines.size} lines → ${lines.take(3)} (retry=$retryIndex)")

            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: return
            val offer = parser.parse(lines)

            if (offer == null) {
                AppLog.d(AppLog.TAG_SERVICE, "Screenshot: parser returned null (retry=$retryIndex)")
                return
            }

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            // Cross-platform duplicate check
            if (OfferDuplicateChecker.isCrossPlatformDuplicate(offer, ServiceLocator.overlayManager.getActiveOffers())) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot result: ${result.zlPerHour} zł/h → ${result.level} (retry=$retryIndex)")

            if (isSameAsPrevious(offer.platform, result)) return

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L, offer.platform)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Screenshot fallback error (retry=$retryIndex): ${e.message}")
        } finally {
            bitmap?.recycle()
            croppedBitmap?.recycle()
        }
    }

    /**
     * Uber watch mode — safety net na wypadek opóźnionych/brakujących eventów.
     * Gdy Uber generuje eventy, uruchamiamy periodic check co 2.5s.
     * Jeśli belka NIE jest widoczna → robimy screenshot.
     * Zatrzymuje się po 60s bez eventów Ubera.
     */
    private fun startUberWatchIfNeeded() {
        if (uberWatchJob?.isActive == true) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        uberWatchJob = scope.launch {
            AppLog.d(AppLog.TAG_SERVICE, "Uber watch: started")
            while (isActive) {
                delay(WATCH_INTERVAL_MS)
                val sinceLastEvent = System.currentTimeMillis() - lastUberEventTime
                if (sinceLastEvent > WATCH_TIMEOUT_MS) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: no events for ${sinceLastEvent / 1000}s, stopping")
                    break
                }
                // Jeśli retries właśnie lecą — nie robimy dodatkowego screenshota (unika kolizji)
                if (isRetrying) continue
                // Jeśli belka Ubera jest już widoczna — nie robimy screenshota
                if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) continue
                // Jeśli Uber nie jest foreground i nie ma overlay okna — nie ma co screenshotować.
                // Gdy Uber jest foreground, popup jest wewnątrz okna apki (nie overlay) — screenshotuj.
                if (!isUberForeground() && !hasUberOverlayWindow()) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: no overlay window and not foreground — skipping screenshot")
                    continue
                }
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
        if (now - lastWindowDiagTime < DIAG_THROTTLE_MS) return
        lastWindowDiagTime = now

        try {
            val allWindows = windows ?: return
            AppLog.d(AppLog.TAG_SERVICE, "=== Uber Window Diagnostics (${allWindows.size} windows) ===")
            for ((i, w) in allWindows.withIndex()) {
                val root = w.root
                try {
                    val pkg = root?.packageName?.toString() ?: "null"
                    AppLog.d(AppLog.TAG_SERVICE, "  Window[$i]: type=${w.type}, layer=${w.layer}, pkg=$pkg")
                    if (root != null && pkg == "com.ubercab.driver") {
                        val text = AccessibilityTextCollector.collectText(root)
                        val preview = text.take(300).replace("\n", " | ")
                        AppLog.d(AppLog.TAG_SERVICE, "  Window[$i] Uber text: $preview")
                    }
                } finally {
                    root?.recycle()
                }
            }
            AppLog.d(AppLog.TAG_SERVICE, "=== End Window Diagnostics ===")
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Window diagnostics error: ${e.message}")
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

        // Timing constants
        private const val RETRY_DELAY_MS = 600L        // odstęp między retryami — unika errorCode=3 (Android rate-limit)
        private const val WATCH_INTERVAL_MS = 2500L    // periodic check co 2.5s
        private const val WATCH_TIMEOUT_MS = 60_000L   // watch mode wyłącza się po 60s bez eventów
        private const val SCREENSHOT_TIMEOUT_MS = 3000L // timeout na takeScreenshot()
        private const val DIAG_THROTTLE_MS = 10_000L   // diagnostyka okien max raz na 10s
        private const val UBER_MAX_RETRIES = 4         // Uber React Native: wolny rendering
        private const val WOLT_MAX_RETRIES = 2          // Wolt natywne UI: szybki rendering
        private const val CROP_TOP_RATIO = 0.4f         // crop dolne 60% screenshota

        /** Paczki kurierskie — blokujemy screenshot tylko gdy foreground to INNA z tych apek */
        private val courierPackages = setOf(
            "com.ubercab.driver",
            "com.wolt.courierapp",
            "com.logistics.rider.glovo",
            "com.bolt.deliverycourier"
        )
    }
}
