package com.orderpilot.app.settings

interface SettingsRepository {
    fun load(): AppSettings
    fun save(settings: AppSettings)
    fun addListener(listener: (AppSettings) -> Unit)
    fun removeListener(listener: (AppSettings) -> Unit)
}