package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
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

class CourierAccessibilityService : AccessibilityService() {

    private lateinit var pipeline: PipelineOrchestrator
    private lateinit var throttler: EventThrottler
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    @Volatile private var lastResult: AnalysisResult? = null
    @Volatile private var lastResultTime = 0L
    private val resultExpiryMs = 60_000L

    override fun onServiceConnected() {
        pipeline = ServiceLocator.pipelineOrchestrator
        throttler = EventThrottler()
        isConnected = true
        AppLog.d(AppLog.TAG_SERVICE, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg !in watchedPackages) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        if (isUserStopped) return

        AppLog.d(AppLog.TAG_SERVICE, "Event from $pkg")

        // Primary: MediaProjection pipeline (jeśli aktywna)
        if (isMediaProjectionAvailable()) {
            throttler.onEvent(scope) { pipeline.process(pkg) }
            return
        }

        // Fallback: Accessibility text parsing (gdy MediaProjection niedostępna)
        throttler.onEvent(scope) { processViaAccessibility(pkg) }
    }

    private fun isMediaProjectionAvailable(): Boolean {
        val service = ScreenCaptureService.instance ?: return false
        return service.isReady() && !ScreenCaptureService.isProjectionLost
    }

    private fun processViaAccessibility(packageName: String) {
        val root = rootInActiveWindow ?: run {
            AppLog.w(AppLog.TAG_SERVICE, "rootInActiveWindow null")
            return
        }
        try {
            val text = AccessibilityTextCollector.collectText(root)
            root.recycle()

            if (text.isBlank()) return
            AppLog.d(AppLog.TAG_SERVICE, "Accessibility text (${text.length} chars)")

            val lines = text.split("\n").filter { it.isNotBlank() }
            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: run {
                AppLog.d(AppLog.TAG_SERVICE, "Accessibility: no parser for $packageName")
                return
            }
            val offer = parser.parse(lines) ?: run {
                AppLog.d(AppLog.TAG_SERVICE, "Accessibility: parser returned null")
                return
            }

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Accessibility result: ${result.zlPerHour} zł/h → ${result.level}")

            // Deduplikacja — nie pokazuj belki jeśli ten sam wynik
            val now = System.currentTimeMillis()
            if (now - lastResultTime > resultExpiryMs) lastResult = null
            if (result == lastResult) {
                AppLog.d(AppLog.TAG_SERVICE, "Accessibility: same result, skipping")
                return
            }
            lastResult = result
            lastResultTime = now

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.display.displayTimeSeconds * 1000L)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Accessibility fallback error: ${e.message}")
        }
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

        private val watchedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")
    }
}
