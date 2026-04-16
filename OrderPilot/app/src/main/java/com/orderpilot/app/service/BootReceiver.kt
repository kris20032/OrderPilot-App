package com.orderpilot.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.orderpilot.app.di.AppLog
import com.orderpilot.app.di.MonitoringController

/**
 * Wznawia monitoring po reboot urządzenia.
 * MonitoringController.initialize() jest idempotentne — jeśli stan = ACTIVE,
 * uruchomi foreground service + watchdog.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            AppLog.d(TAG, "Boot completed — reinitializing monitoring")
            MonitoringController.initialize(context)
        }
    }

    companion object {
        private const val TAG = "OP_BootReceiver"
    }
}
