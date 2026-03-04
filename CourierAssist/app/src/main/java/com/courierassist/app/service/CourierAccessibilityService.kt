package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.courierassist.app.di.AppLog
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.pipeline.PipelineOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class CourierAccessibilityService : AccessibilityService() {

    private lateinit var pipeline: PipelineOrchestrator
    private lateinit var throttler: EventThrottler
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onServiceConnected() {
        pipeline = ServiceLocator.pipelineOrchestrator
        throttler = EventThrottler()
        AppLog.d(AppLog.TAG_SERVICE, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg !in watchedPackages) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        AppLog.d(AppLog.TAG_SERVICE, "Event from $pkg")
        throttler.onEvent(scope) { pipeline.process(pkg) }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private val watchedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")
    }
}