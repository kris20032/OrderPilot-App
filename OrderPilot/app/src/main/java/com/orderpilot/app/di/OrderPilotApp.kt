package com.orderpilot.app.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Environment
import java.io.File
import java.util.Date

class OrderPilotApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupCrashLogger()
        createNotificationChannel()
        ServiceLocator.init(applicationContext)
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
    }
}
