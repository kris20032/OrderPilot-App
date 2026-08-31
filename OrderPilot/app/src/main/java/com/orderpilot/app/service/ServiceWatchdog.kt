package com.orderpilot.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orderpilot.app.R
import com.orderpilot.app.di.AppLog
import com.orderpilot.app.di.MonitoringController
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * WorkManager watchdog — sprawdza co 15 min czy AccessibilityService zyje.
 *
 * Scenariusze:
 * - MonitoringController.isActive() + AccessibilityService.isConnected → OK, cisza.
 * - MonitoringController.isActive() + !isConnected → serwis padl. Notification z recovery.
 * - !isActive() → user celowo wylaczyI. Watchdog sie sam anuluje.
 *
 * WorkManager przezywa process kill (jest czescia systemu).
 */
class ServiceWatchdog(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Guard: upewnij się że MonitoringController jest zainicjalizowany (race po process kill)
        MonitoringController.initialize(applicationContext)

        // Jesli user celowo zatrzymal monitoring — nie bedziemy go meczyc
        if (!MonitoringController.isActive()) {
            AppLog.d(TAG, "Watchdog: monitoring stopped by user, cancelling self")
            cancel(applicationContext)
            return Result.success()
        }

        // Grace period: po świeżym start() AccessibilityService potrzebuje kilku sekund na bind.
        // Jeśli monitoring aktywny od mniej niż 30s → za wcześnie na diagnozę, skip.
        // M10: -1 (brak timestampu) traktujemy TEŻ jak grace — lepiej przemilczeć jeden cykl
        // niż wysłać fałszywy alert "wykrywanie zatrzymane" w trakcie rebindu po reboocie.
        val activeMs = MonitoringController.msSinceLastStart()
        if (activeMs < 0L || activeMs <= 30_000L) {
            AppLog.d(TAG, "Watchdog: grace period (${activeMs}ms since start), skipping check")
            return Result.success()
        }

        // Sprawdz 2 rzeczy: czy serwis jest ENABLED w ustawieniach I czy jest CONNECTED (bound)
        val isEnabled = isAccessibilityEnabled(applicationContext)
        val isConnected = OrderPilotAccessibilityService.isConnected

        if (isEnabled && isConnected) {
            AppLog.d(TAG, "Watchdog: service enabled + connected, all good")
            return Result.success()
        }

        // Serwis martwy lub wylaczony ale monitoring ma byc aktywny — powiadom usera
        AppLog.w(TAG, "Watchdog: service problem (enabled=$isEnabled, connected=$isConnected) — sending recovery notification")
        sendRecoveryNotification()

        return Result.success()
    }

    private fun sendRecoveryNotification() {
        // Intent do ustawien accessibility — jak najblizej OrderPilot
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pending = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, WATCHDOG_CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.notif_watchdog_title))
            .setContentText(applicationContext.getString(R.string.notif_watchdog_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pending)
            .setAutoCancel(true) // znika po kliknieciu
            .setPriority(NotificationCompat.PRIORITY_HIGH) // heads-up zeby user zauwazyl
            .build()

        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(WATCHDOG_NOTIFICATION_ID, notification)
    }

    private fun isAccessibilityEnabled(context: Context): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        return enabledServices.lowercase(Locale.ROOT).contains("orderpilot")
    }

    companion object {
        private const val TAG = "OP_Watchdog"
        private const val WORK_NAME = "service_watchdog"
        const val WATCHDOG_CHANNEL_ID = "order_pilot_watchdog"
        private const val WATCHDOG_NOTIFICATION_ID = 2

        /** Tworzy kanaly notyfikacji watchdoga. Wolane z OrderPilotApp.onCreate(). */
        fun createNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                WATCHDOG_CHANNEL_ID,
                context.getString(R.string.notif_watchdog_title),
                NotificationManager.IMPORTANCE_HIGH // heads-up
            ).apply {
                description = context.getString(R.string.notif_watchdog_text)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        /** Planuje watchdoga co 15 min. Idempotent (KEEP = nie nadpisuje istniejacego). */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<ServiceWatchdog>(15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            AppLog.d(TAG, "Watchdog scheduled (15 min interval)")
        }

        /** Anuluje watchdoga. */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            // Usun ewentualna recovery notification
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(WATCHDOG_NOTIFICATION_ID)
            AppLog.d(TAG, "Watchdog cancelled")
        }
    }
}
