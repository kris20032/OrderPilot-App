package com.orderpilot.app.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.orderpilot.app.R
import com.orderpilot.app.domain.AppLanguage
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
        syncAppLocaleFromSettings()
    }

    /**
     * Migracja v1.0.2 → v1.0.3: stary kod używał LocaleHelper.wrap przez attachBaseContext
     * i nie wołał AppCompatDelegate.setApplicationLocales. Po update SharedPrefs trzyma
     * np. AppLanguage.UK, ale system-managed locale jest pusty → UI fallbackuje do
     * system locale zamiast wyboru usera. Sync ustawia AppCompatDelegate na wartość z prefs
     * jeśli system jeszcze go nie zna.
     */
    private fun syncAppLocaleFromSettings() {
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            val pref = try {
                ServiceLocator.settingsRepository.load().language
            } catch (_: Exception) {
                AppLanguage.PL
            }
            val tag = when (pref) {
                AppLanguage.PL -> "pl"
                AppLanguage.UK -> "uk"
                AppLanguage.EN -> "en"
                AppLanguage.RU -> "ru"
            }
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
    }

    private fun setupCrashLogger() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                // Zapis do katalogu prywatnego apki (Android/data/.../files) — NIE wymaga
                // uprawnień storage i działa na całym zakresie API (scoped storage, targetSdk 35).
                // Wcześniej pisaliśmy do publicznego Downloads → wyjątek połykany → 0 logów crashy.
                val dir = getExternalFilesDir(null) ?: filesDir
                val file = File(dir, "OrderPilot_crash_${System.currentTimeMillis()}.txt")
                file.writeText("${Date()}\nThread: ${thread.name}\n${throwable.stackTraceToString()}")
            } catch (_: Exception) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notif_monitoring_title),
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
