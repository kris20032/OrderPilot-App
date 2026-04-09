package com.orderpilot.app.settings

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArrayList

class SharedPrefsSettingsRepository(context: Context) : SettingsRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val listeners = CopyOnWriteArrayList<(AppSettings) -> Unit>()

    override fun load(): AppSettings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return AppSettings()
        return try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            AppSettings()
        }
    }

    override fun save(settings: AppSettings) {
        prefs.edit().putString(KEY_SETTINGS, Json.encodeToString(settings)).apply()
        listeners.forEach { it(settings) }
    }

    override fun addListener(listener: (AppSettings) -> Unit) {
        listeners.add(listener)
    }

    override fun removeListener(listener: (AppSettings) -> Unit) {
        listeners.remove(listener)
    }

    companion object {
        private const val PREFS_NAME = "order_pilot_settings"
        private const val KEY_SETTINGS = "app_settings"
    }
}