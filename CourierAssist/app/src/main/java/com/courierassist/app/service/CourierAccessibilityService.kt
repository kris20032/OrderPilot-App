package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.courierassist.app.engine.OfferAnalyzer
import com.courierassist.app.overlay.OverlayManager
import com.courierassist.app.overlay.SystemOverlayManager
import com.courierassist.app.parser.ParserRegistry
import com.courierassist.app.parser.UberParser

class CourierAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "CourierAssist"
        private const val PREFS_NAME = "courierassist_prefs"
        const val KEY_ENABLED = "service_enabled"
        private const val DEBOUNCE_MS = 300L
    }

    private lateinit var prefs: SharedPreferences
    private lateinit var parserRegistry: ParserRegistry
    private lateinit var offerAnalyzer: OfferAnalyzer
    private lateinit var overlayManager: OverlayManager

    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null
    private var isShowingOverlay = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        parserRegistry = ParserRegistry(listOf(UberParser()))
        offerAnalyzer = OfferAnalyzer()
        overlayManager = SystemOverlayManager(this)
        Log.d(TAG, "AccessibilityService connected — pipeline ready")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!isEnabled()) return

        val packageName = event.packageName?.toString() ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return

        // Debounce
        pendingRunnable?.let { handler.removeCallbacks(it) }

        val runnable = Runnable {
            processEvent(packageName)
        }
        pendingRunnable = runnable
        handler.postDelayed(runnable, DEBOUNCE_MS)
    }

    private fun processEvent(packageName: String) {
        val parser = parserRegistry.getParser(packageName)
        if (parser == null) {
            hideOverlayIfShowing()
            return
        }

        val rootNode = rootInActiveWindow
        if (rootNode == null) {
            hideOverlayIfShowing()
            return
        }

        val offer = parser.parse(rootNode)
        if (offer == null) {
            hideOverlayIfShowing()
            return
        }

        val result = offerAnalyzer.analyze(offer)
        overlayManager.show(result, offer)
        isShowingOverlay = true
        Log.d(TAG, "Overlay shown: ${result.zlPerHour} zł/h → ${result.level}")
    }

    private fun hideOverlayIfShowing() {
        if (isShowingOverlay) {
            overlayManager.hide()
            isShowingOverlay = false
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted")
        hideOverlayIfShowing()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlayIfShowing()
        pendingRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun isEnabled(): Boolean {
        return prefs.getBoolean(KEY_ENABLED, false)
    }
}
