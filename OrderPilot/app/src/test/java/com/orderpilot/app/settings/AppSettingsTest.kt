package com.orderpilot.app.settings

import com.orderpilot.app.domain.AppLanguage
import com.orderpilot.app.domain.Platform
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        val json = SettingsJson.encodeToString(original)
        val decoded = SettingsJson.decodeFromString<AppSettings>(json)
        assertEquals(original, decoded)
    }

    // --- #28: wybór języka MUSI przetrwać zapis, niezależnie od locale systemu ---

    @Test
    fun `language is always written to json (encodeDefaults)`() {
        // Sedno #28: przy domyślnym Json (encodeDefaults=false) klucz language bywał pomijany
        // gdy == fromSystemLocale() → po „Zapisz" wracał do systemowego (objaw „reset do PL").
        val original = AppSettings(language = AppLanguage.RU)
        val json = SettingsJson.encodeToString(original)
        assertTrue("Brak klucza language w JSON: $json", json.contains("\"language\""))
        assertTrue("Brak wartości RU w JSON: $json", json.contains("RU"))
        val decoded = SettingsJson.decodeFromString<AppSettings>(json)
        assertEquals(AppLanguage.RU, decoded.language)
    }

    @Test
    fun `unknown json keys do not wipe settings`() {
        // Forward-compat (M2): nieznany klucz z przyszłej/uszkodzonej wersji nie może wywalić
        // dekodowania — inaczej load() łapie wyjątek i resetuje WSZYSTKIE ustawienia do defaultów.
        val json = """{"language":"EN","someFutureKey":123}"""
        val decoded = SettingsJson.decodeFromString<AppSettings>(json)
        assertEquals(AppLanguage.EN, decoded.language)
    }
}