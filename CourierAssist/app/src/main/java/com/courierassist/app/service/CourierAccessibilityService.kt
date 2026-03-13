package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Build
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.di.AppLog
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.pipeline.PipelineOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class CourierAccessibilityService : AccessibilityService() {

    private lateinit var pipeline: PipelineOrchestrator
    private val throttlers = mutableMapOf<String, EventThrottler>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    @Volatile private var lastResult: AnalysisResult? = null
    @Volatile private var lastResultTime = 0L
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

        // Glovo: accessibility tree first (może zawierać dane poza ekranem)
        val isGlovo = ServiceLocator.parserRegistry.getParser(pkg)?.platform == com.courierassist.app.domain.Platform.GLOVO
        if (isGlovo) {
            throttler.onEvent(scope) { processViaAccessibilityTree(pkg) }
            return
        }

        // Primary: MediaProjection pipeline (jeśli aktywna)
        if (isMediaProjectionAvailable()) {
            throttler.onEvent(scope) { pipeline.process(pkg) }
            return
        }

        // Fallback: takeScreenshot() (API 30+) — nie wymaga MediaProjection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            throttler.onEvent(scope) { processViaScreenshot(pkg) }
        }
    }

    private suspend fun processViaAccessibilityTree(packageName: String) {
        try {
            val root = rootInActiveWindow ?: run {
                AppLog.w(AppLog.TAG_SERVICE, "Glovo: rootInActiveWindow null, falling back to screenshot")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) processViaScreenshot(packageName)
                return
            }
            val text = AccessibilityTextCollector.collectText(root)
            root.recycle()
            AppLog.d(AppLog.TAG_SERVICE, "Glovo accessibility tree text: ${text.take(200)}")

            val lines = text.lines().filter { it.isNotBlank() }
            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: return
            val offer = parser.parse(lines)

            if (offer == null) {
                AppLog.d(AppLog.TAG_SERVICE, "Glovo: accessibility tree parse failed, falling back to screenshot")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) processViaScreenshot(packageName)
                return
            }

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Glovo tree result: zlPerKm=${result.zlPerKm} → ${result.level} partial=${offer.isPartial}")

            val now = System.currentTimeMillis()
            if (now - lastResultTime > resultExpiryMs) lastResult = null
            val prev = lastResult
            if (prev != null &&
                result.level == prev.level &&
                result.offer.amount == prev.offer.amount &&
                result.offer.distanceKm == prev.offer.distanceKm) return
            lastResult = result
            lastResultTime = now

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Glovo accessibility tree error: ${e.message}")
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

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot result: ${result.zlPerHour} zł/h → ${result.level}")

            val now = System.currentTimeMillis()
            if (now - lastResultTime > resultExpiryMs) lastResult = null
            val prev = lastResult
            if (prev != null &&
                result.level == prev.level &&
                result.offer.amount == prev.offer.amount &&
                result.offer.estimatedMinutes == prev.offer.estimatedMinutes) return
            lastResult = result
            lastResultTime = now

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L)
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
    }
}