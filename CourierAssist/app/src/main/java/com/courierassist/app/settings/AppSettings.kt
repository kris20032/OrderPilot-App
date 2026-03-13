package com.courierassist.app.settings

import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.MetricType
import com.courierassist.app.domain.Platform
import com.courierassist.app.domain.ThemeMode
import kotlinx.serialization.Serializable

@Serializable
data class ThresholdConfig(
    val greenMinZlPerHour: Double = 40.0,
    val yellowMinZlPerHour: Double = 32.0,
    val greenMinZlPerKm: Double = 4.0,   // dla Glovo (brak czasu)
    val yellowMinZlPerKm: Double = 3.0
)

@Serializable
data class DisplayConfig(
    val visibleMetrics: Set<MetricType> = setOf(MetricType.ZL_PER_HOUR),
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val overlayOpacity: Int = 80,
    val displayTimeSeconds: Int = 40
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
    val language: AppLanguage = AppLanguage.PL,
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