package com.orderpilot.app.settings

import com.orderpilot.app.domain.Platform
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class AppSettingsTest {

    // --- thresholdsFor ---

    @Test
    fun `thresholdsFor returns global thresholds when no platform override`() {
        val settings = AppSettings()
        val result = settings.thresholdsFor(Platform.UBER)
        assertEquals(settings.globalThresholds, result)
    }

    @Test
    fun `thresholdsFor returns platform override when set`() {
        val override = ThresholdConfig(greenMinZlPerHour = 50.0, yellowMinZlPerHour = 40.0)
        val settings = AppSettings(
            platformOverrides = mapOf(Platform.UBER to PlatformSettings(thresholds = override))
        )
        assertEquals(override, settings.thresholdsFor(Platform.UBER))
    }

    @Test
    fun `thresholdsFor returns global when platform override has null thresholds`() {
        val settings = AppSettings(
            platformOverrides = mapOf(Platform.UBER to PlatformSettings(thresholds = null))
        )
        assertEquals(settings.globalThresholds, settings.thresholdsFor(Platform.UBER))
    }

    // --- filtersFor ---

    @Test
    fun `filtersFor returns global filters when no platform override`() {
        val settings = AppSettings()
        assertEquals(settings.globalFilters, settings.filtersFor(Platform.UBER))
    }

    @Test
    fun `filtersFor returns platform override when set`() {
        val override = FilterConfig(minDistanceKm = 1.0, maxDistanceKm = 10.0)
        val settings = AppSettings(
            platformOverrides = mapOf(Platform.UBER to PlatformSettings(filters = override))
        )
        assertEquals(override, settings.filtersFor(Platform.UBER))
    }

    // --- serialization round-trip ---

    @Test
    fun `AppSettings serializes and deserializes correctly`() {
        val original = AppSettings(
            globalThresholds = ThresholdConfig(45.0, 35.0),
            globalFilters = FilterConfig(minDistanceKm = 0.5, maxDistanceKm = 15.0),
            platformOverrides = mapOf(
                Platform.UBER to PlatformSettings(
                    thresholds = ThresholdConfig(50.0, 40.0)
                )
            )
        )
        val json = Json.encodeToString(original)
        val decoded = Json.decodeFromString<AppSettings>(json)
        assertEquals(original, decoded)
    }
}