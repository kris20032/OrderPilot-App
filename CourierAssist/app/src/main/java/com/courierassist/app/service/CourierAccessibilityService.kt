package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.SharedPreferences
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class CourierAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "CourierAssist"
        private const val PREFS_NAME = "courierassist_prefs"
        private const val KEY_ENABLED = "service_enabled"

        // Uber Driver package name
        const val UBER_DRIVER_PACKAGE = "com.ubercab.driver"
    }

    private lateinit var prefs: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        Log.d(TAG, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Check if user enabled the service via START/STOP toggle
        if (!isEnabled()) return

        // Filter only Uber Driver package
        val packageName = event.packageName?.toString() ?: return
        if (packageName != UBER_DRIVER_PACKAGE) return

        // Only process content change events
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return

        val rootNode = rootInActiveWindow ?: return

        Log.d(TAG, "Uber Driver event detected, root node available")

        // TODO: TASK 6.1.1 — pass rootNode to parser pipeline
    }

    override fun onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted")
    }

    private fun isEnabled(): Boolean {
        return prefs.getBoolean(KEY_ENABLED, false)
    }
}
