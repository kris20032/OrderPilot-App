package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
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
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
            if (isMediaProjectionAvailable()) {
                pipeline.process(pkg)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                processViaScreenshot(pkg)
            }

            // Retry tylko dla Ubera — Uber generuje mało eventów (~15s przerwy),
            // więc jeśli pierwszy screenshot fail (Glovo dialog, lock screen itp.),
            // popup może zniknąć zanim przyjdzie następny event.
            val plat = ServiceLocator.parserRegistry.getParser(pkg)?.platform
            if (plat == Platform.UBER) {
                kotlinx.coroutines.delay(3000)
                if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] == null) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber: no overlay after first attempt, retrying")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        processViaScreenshot(pkg)
                    }
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
    private suspend fun processViaScreenshot(packageName: String) {
        try {
            AppLog.d(AppLog.TAG_SERVICE, "Taking screenshot via AccessibilityService API")
            val bitmap = withTimeoutOrNull(3000L) { takeScreenshotSuspend() } ?: run {
                AppLog.w(AppLog.TAG_SERVICE, "Screenshot returned null or timed out")
                return
            }

            // Crop dolne 60% — belka jest na górze, popup zlecenia na dole
            val croppedBitmap = run {
                val startY = (bitmap.height * 0.4f).toInt()
                val cropped = Bitmap.createBitmap(bitmap, 0, startY, bitmap.width, bitmap.height - startY)
                bitmap.recycle()
                cropped
            }
            val lines = ServiceLocator.ocrEngine.recognize(croppedBitmap)
            croppedBitmap.recycle()
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot OCR: ${lines.size} lines → ${lines.take(3)}")

            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: return
            val offer = parser.parse(lines) ?: run {
                AppLog.d(AppLog.TAG_SERVICE, "Screenshot: parser returned null")
                return
            }

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            // Cross-platform duplicate check
            if (isCrossPlatformDuplicate(offer)) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot result: ${result.zlPerHour} zł/h → ${result.level}")

            if (isSameAsPrevious(offer.platform, result)) return

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L, offer.platform)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Screenshot fallback error: ${e.message}")
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
