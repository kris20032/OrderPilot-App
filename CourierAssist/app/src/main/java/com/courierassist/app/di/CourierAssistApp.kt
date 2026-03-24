package com.courierassist.app.di

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.courierassist.app.service.CourierAccessibilityService
import java.io.File
import java.util.Date

class CourierAssistApp : Application() {

    private var activityCount = 0

    override fun onCreate() {
        super.onCreate()
        setupCrashLogger()
        createNotificationChannel()
        ServiceLocator.init(applicationContext)
        registerActivityLifecycleCallbacks(taskRemovedDetector)
    }

    /**
     * Wykrywa usunięcie apki z "ostatnich aplikacji".
     * Gdy ostatnia Activity jest zniszczona (nie przez rotację ekranu) →
     * zatrzymuje monitoring i chowa belki.
     */
    private val taskRemovedDetector = object : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { activityCount++ }
        override fun onActivityDestroyed(activity: Activity) {
            activityCount--
            if (activityCount <= 0 && !activity.isChangingConfigurations) {
                activityCount = 0
                CourierAccessibilityService.isUserStopped = true
                Handler(Looper.getMainLooper()).post {
                    try { ServiceLocator.overlayManager.hide() } catch (_: Exception) {}
                }
                AppLog.d(AppLog.TAG_SERVICE, "App removed from recents — stopping monitoring")
            }
        }
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }

    private fun setupCrashLogger() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "CourierAssist_crash_${System.currentTimeMillis()}.txt"
                )
                file.writeText("${Date()}\nThread: ${thread.name}\n${throwable.stackTraceToString()}")
            } catch (_: Exception) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "CourierAssist nasłuchuje",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "courier_assist_service"
    }
}
