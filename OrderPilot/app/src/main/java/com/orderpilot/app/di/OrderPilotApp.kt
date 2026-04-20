package com.orderpilot.app.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Environment
import com.orderpilot.app.service.ServiceWatchdog
import java.io.File
import java.util.Date

class OrderPilotApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupCrashLogger()
        createNotificationChannel()
        ServiceWatchdog.createNotificationChannel(this)
        ServiceLocator.init(applicationContext)
        MonitoringController.initialize(applicationContext)
    }

    private fun setupCrashLogger() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "OrderPilot_crash_${System.currentTimeMillis()}.txt"
                )
                file.writeText("${Date()}\nThread: ${thread.name}\n${throwable.stackTraceToString()}")
            } catch (_: Exception) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "OrderPilot nasłuchuje",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "order_pilot_service"

        /**
         * Privacy Policy URL — pokazywany w DisclosureActivity i SettingsActivity (M4, KD5).
         *
         * TODO(Phase 4, Task 4.3): zastąpić docelowym URL po wgraniu privacy-policy.html
         * na GitHub Pages. Obecnie placeholder — jeśli user kliknie link przed Phase 4
         * uploadem, zobaczy 404, ale DisclosureActivity nadal działa.
         */
        const val PRIVACY_POLICY_URL = "https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html"
    }
}
