package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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
        Log.d(TAG, "=== AccessibilityService connected — pipeline ready ===")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // Log ALL packages to see what's coming through
        Log.d(TAG, "Event: pkg=$packageName type=${event.eventType} enabled=${isEnabled()}")

        if (!isEnabled()) return
        val eventType = event.eventType
        if (eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        // Debounce
        pendingRunnable?.let { handler.removeCallbacks(it) }
        val runnable = Runnable { processEvent(packageName) }
        pendingRunnable = runnable
        handler.postDelayed(runnable, DEBOUNCE_MS)
    }

    private fun processEvent(packageName: String) {
        Log.d(TAG, "--- processEvent: $packageName ---")

        val parser = parserRegistry.getParser(packageName)
        if (parser == null) {
            Log.d(TAG, "No parser for package: $packageName")
            hideOverlayIfShowing()
            return
        }

        Log.d(TAG, "Parser found for: $packageName")

        // Scan ALL windows — popup may be in a separate window
        val rootNodes = getAllRootNodes()
        Log.d(TAG, "Found ${rootNodes.size} windows to scan")

        if (rootNodes.isEmpty()) {
            Log.d(TAG, "No root nodes found in any window!")
            hideOverlayIfShowing()
            return
        }

        for ((index, rootNode) in rootNodes.withIndex()) {
            Log.d(TAG, "--- Window $index ---")
            logNodeTree(rootNode, 0)

            val offer = parser.parse(rootNode)
            if (offer != null) {
                Log.d(TAG, "Offer detected in window $index: amount=${offer.amount} minutes=${offer.estimatedMinutes}")
                val result = offerAnalyzer.analyze(offer)
                overlayManager.show(result, offer)
                isShowingOverlay = true
                Log.d(TAG, "Overlay shown: ${result.zlPerHour} zł/h → ${result.level}")
                return
            }
        }

        Log.d(TAG, "Parser returned null for all windows — offer not detected")
        hideOverlayIfShowing()
    }

    private fun getAllRootNodes(): List<AccessibilityNodeInfo> {
        val nodes = mutableListOf<AccessibilityNodeInfo>()

        // Try getWindows() first — returns all windows on screen
        try {
            val allWindows = windows
            Log.d(TAG, "getWindows() returned ${allWindows.size} windows")
            for (window in allWindows) {
                val root = window.root
                if (root != null) {
                    nodes.add(root)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "getWindows() failed: ${e.message}")
        }

        // Fallback: if no windows found, try rootInActiveWindow
        if (nodes.isEmpty()) {
            val root = rootInActiveWindow
            if (root != null) {
                nodes.add(root)
            }
        }

        return nodes
    }

    private fun logNodeTree(node: AccessibilityNodeInfo, depth: Int) {
        val indent = "  ".repeat(depth)
        val text = node.text?.toString()
        val desc = node.contentDescription?.toString()
        if (!text.isNullOrBlank() || !desc.isNullOrBlank()) {
            Log.d(TAG, "$indent[node] text='$text' desc='$desc'")
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            logNodeTree(child, depth + 1)
        }
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
