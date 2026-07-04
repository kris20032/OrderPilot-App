package com.orderpilot.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.orderpilot.app.R
import com.orderpilot.app.di.AppLog
import com.orderpilot.app.di.OrderPilotApp
import com.orderpilot.app.ui.MainActivity

/**
 * Foreground service utrzymujacy proces przy zyciu.
 *
 * - Persistent notification "Monitoring zamowien aktywny" — sygnalizuje userowi ze system dziala.
 * - 1px niewidoczny overlay — dodatkowy boost priorytetu procesu dla OEM (MIUI/Samsung).
 * - Startowany gdy user klika Start, zatrzymywany gdy klika Stop.
 */
class MonitoringForegroundService : Service() {

    private var keepAliveView: View? = null
    private var windowManager: WindowManager? = null

    override fun onCreate() {
        super.onCreate()
        AppLog.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                AppLog.d(TAG, "Stop action received")
                stopSelf()
                return START_NOT_STICKY
            }
        }

        startForeground(NOTIFICATION_ID, createNotification())
        showKeepAliveOverlay()
        AppLog.d(TAG, "Foreground service started with notification + keepAlive overlay")

        // START_STICKY = system restartuje serwis po procesowym killu (OEM)
        return START_STICKY
    }

    override fun onDestroy() {
        removeKeepAliveOverlay()
        AppLog.d(TAG, "onDestroy — overlay removed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openPending = PendingIntent.getActivity(
            this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, OrderPilotApp.CHANNEL_ID)
            .setContentTitle(getString(R.string.notif_monitoring_title))
            .setContentText(getString(R.string.notif_monitoring_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(openPending)
            .setOngoing(true) // nieusuwalna swipe'em
            .setSilent(true)  // bez dzwieku
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * 1px niewidoczny overlay — podnosi priorytet procesu.
     * FLAG_NOT_TOUCHABLE + FLAG_NOT_FOCUSABLE = nie przechwytuje dotyku.
     * Rozmiar 1x1 px, w pelni przezroczysty — niewidoczny dla usera.
     */
    private fun showKeepAliveOverlay() {
        if (keepAliveView != null) return // juz wyswietlony

        try {
            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            keepAliveView = View(this).apply {
                // 1x1 px, w pelni przezroczysty
                setBackgroundColor(0x00000000)
            }

            val params = WindowManager.LayoutParams(
                1, // width = 1px
                1, // height = 1px
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 0
                y = 0
            }

            windowManager?.addView(keepAliveView, params)
            AppLog.d(TAG, "KeepAlive overlay shown (1x1 px)")
        } catch (e: Exception) {
            AppLog.w(TAG, "Failed to show keepAlive overlay: ${e.message}")
        }
    }

    private fun removeKeepAliveOverlay() {
        try {
            keepAliveView?.let { windowManager?.removeView(it) }
        } catch (_: Exception) {}
        keepAliveView = null
    }

    companion object {
        private const val TAG = "OP_ForegroundService"
        private const val NOTIFICATION_ID = 1
        private const val START_BLOCKED_NOTIFICATION_ID = 3
        private const val ACTION_STOP = "com.orderpilot.app.STOP_MONITORING"

        fun start(context: Context) {
            val intent = Intent(context, MonitoringForegroundService::class.java)
            try {
                context.startForegroundService(intent)
            } catch (e: Exception) {
                // M11: na API 31+ start FGS z tła rzuca ForegroundServiceStartNotAllowedException,
                // gdy OEM zerwał wyjątek optymalizacji baterii (ścieżki: watchdog co 15 min,
                // BootReceiver, OrderPilotApp.onCreate). Zamiast crasha — powiadomienie
                // "otwórz aplikację" (start z foregroundu zawsze przejdzie).
                AppLog.w(TAG, "startForegroundService blocked: ${e.message}")
                notifyStartBlocked(context)
            }
        }

        private fun notifyStartBlocked(context: Context) {
            try {
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pending = PendingIntent.getActivity(
                    context, 0, openIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                val notification = NotificationCompat.Builder(context, ServiceWatchdog.WATCHDOG_CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.notif_start_blocked_title))
                    .setContentText(context.getString(R.string.notif_start_blocked_text))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pending)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                nm.notify(START_BLOCKED_NOTIFICATION_ID, notification)
            } catch (_: Exception) {
                // Brak POST_NOTIFICATIONS / inny błąd — nic więcej nie zrobimy z tła.
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, MonitoringForegroundService::class.java)
            context.stopService(intent)
        }
    }
}
