package com.orderpilot.app.settings

import com.orderpilot.app.domain.AppLanguage
import com.orderpilot.app.domain.MetricType
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.domain.ThemeMode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Wspólny format JSON dla (de)serializacji ustawień w SharedPreferences.
 *
 * - encodeDefaults = true: pole [AppSettings.language] ma DYNAMICZNY default
 *   (fromSystemLocale()), więc bez tego klucz bywał pomijany przy zapisie i wybór
 *   języka nie przeżywał „Zapisz" (#28 — połowa persystencji). Z true jest zawsze zapisany.
 * - ignoreUnknownKeys + coerceInputValues: forward-compat — nieznany klucz albo zły enum
 *   z przyszłej/uszkodzonej wersji nie wywala dekodowania i NIE kasuje ustawień do defaultów.
 */
val SettingsJson: Json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    coerceInputValues = true
}

@Serializable
data class ThresholdConfig(
    val greenMinZlPerHour: Double = 40.0,
    val yellowMinZlPerHour: Double = 32.0,
    val greenMinZlPerKm: Double = 4.0,   // dla Glovo (brak czasu)
    val yellowMinZlPerKm: Double = 3.0
)

@Serializable
data class DisplayConfig(
    val visibleMetrics: Set<MetricType> = MetricType.entries.toSet(),
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val overlayOpacity: Int = 80,
    val displayTimeSeconds: Int = 30
)

@Serializable
data class FilterConfig(
    val minDistanceKm: Double? = null,
    val maxDistanceKm: Double? = null
)

@Serializable
data class PlatformSettings(
    val thresholds: ThresholdConfig? = null,
    val filters: FilterConfig? = null,
    val displayTimeSeconds: Int? = null
)

@Serializable
data class AppSettings(
    val language: AppLanguage = AppLanguage.fromSystemLocale(),
    val display: DisplayConfig = DisplayConfig(),
    val globalThresholds: ThresholdConfig = ThresholdConfig(),
    val globalFilters: FilterConfig = FilterConfig(),
    val platformOverrides: Map<Platform, PlatformSettings> = emptyMap()
) {
    fun thresholdsFor(platform: Platform): ThresholdConfig =
        platformOverrides[platform]?.thresholds ?: globalThresholds

    fun filtersFor(platform: Platform): FilterConfig =
        platformOverrides[platform]?.filters ?: globalFilters

    fun displayTimeFor(platform: Platform): Int =
        platformOverrides[platform]?.displayTimeSeconds ?: display.displayTimeSeconds
}