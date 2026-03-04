package com.courierassist.app.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class CourierAssistApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        ServiceLocator.init(applicationContext)
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
