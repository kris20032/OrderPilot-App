package com.orderpilot.app.engine

import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.domain.ProfitLevel
import com.orderpilot.app.settings.ThresholdConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class OfferAnalyzerTest {

    private val analyzer = OfferAnalyzer()
    private val defaultThresholds = ThresholdConfig(greenMinZlPerHour = 40.0, yellowMinZlPerHour = 32.0)

    private fun offer(amount: Double, minutes: Int, distanceKm: Double? = null) =
        Offer(platform = Platform.UBER, amount = amount, estimatedMinutes = minutes, distanceKm = distanceKm)

    // --- zlPerHour ---

    @Test
    fun `zlPerHour is calculated correctly`() {
        // 30 zł w 30 min = 60 zł/h
        val result = analyzer.analyze(offer(30.0, 30), defaultThresholds)
        assertEquals(60.0, result.zlPerHour!!, 0.01)
    }

    @Test
    fun `zlPerHour for 15 min offer`() {
        // 10 zł w 15 min = 40 zł/h
        val result = analyzer.analyze(offer(10.0, 15), defaultThresholds)
        assertEquals(40.0, result.zlPerHour!!, 0.01)
    }

    // --- ProfitLevel ---

    @Test
    fun `level is GREEN when zlPerHour meets green threshold`() {
        val result = analyzer.analyze(offer(40.0, 60), defaultThresholds)
        assertEquals(ProfitLevel.GREEN, result.level)
    }

    @Test
    fun `level is GREEN when zlPerHour exceeds green threshold`() {
        val result = analyzer.analyze(offer(50.0, 60), defaultThresholds)
        assertEquals(ProfitLevel.GREEN, result.level)
    }

    @Test
    fun `level is YELLOW when zlPerHour is between thresholds`() {
        // 35 zł/h — powyżej 32, poniżej 40
        val result = analyzer.analyze(offer(35.0, 60), defaultThresholds)
        assertEquals(ProfitLevel.YELLOW, result.level)
    }

    @Test
    fun `level is YELLOW when zlPerHour meets yellow threshold exactly`() {
        val result = analyzer.analyze(offer(32.0, 60), defaultThresholds)
        assertEquals(ProfitLevel.YELLOW, result.level)
    }

    @Test
    fun `level is RED when zlPerHour is below yellow threshold`() {
        val result = analyzer.analyze(offer(20.0, 60), defaultThresholds)
        assertEquals(ProfitLevel.RED, result.level)
    }

    // --- zlPerKm ---

    @Test
    fun `zlPerKm is null when distanceKm is null`() {
        val result = analyzer.analyze(offer(30.0, 30, distanceKm = null), defaultThresholds)
        assertNull(result.zlPerKm)
    }

    @Test
    fun `zlPerKm is calculated correctly`() {
        // 20 zł / 5 km = 4.0 zł/km
        val result = analyzer.analyze(offer(20.0, 30, distanceKm = 5.0), defaultThresholds)
        assertNotNull(result.zlPerKm)
        assertEquals(4.0, result.zlPerKm!!, 0.01)
    }

    // --- edge cases ---

    @Test
    fun `zero minutes returns RED with zlPerHour = null`() {
        val result = analyzer.analyze(offer(30.0, 0), defaultThresholds)
        assertNull(result.zlPerHour)
        assertEquals(ProfitLevel.RED, result.level)
    }

    @Test
    fun `offer is preserved in result`() {
        val o = offer(15.0, 20, distanceKm = 3.0)
        val result = analyzer.analyze(o, defaultThresholds)
        assertEquals(o, result.offer)
    }

    @Test
    fun `custom thresholds are respected`() {
        val thresholds = ThresholdConfig(greenMinZlPerHour = 50.0, yellowMinZlPerHour = 30.0)
        // 45 zł/h — poniżej custom green (50), powyżej yellow (30) → YELLOW
        val result = analyzer.analyze(offer(45.0, 60), thresholds)
        assertEquals(ProfitLevel.YELLOW, result.level)
    }

    // --- combined thresholds (AND-semantics, fix #38, Marcin 2026-05-11) ---
    // PLN/h i PLN/km muszą oba spełnić swój próg, żeby kolor był GREEN/YELLOW.
    // Gorszy z dwóch wygrywa (np. YELLOW po godzinie + RED po km → RED).

    private val combinedThresholds = ThresholdConfig(
        greenMinZlPerHour = 40.0,
        yellowMinZlPerHour = 34.0,
        greenMinZlPerKm = 3.0,
        yellowMinZlPerKm = 2.0
    )

    @Test
    fun `combined - marcin repro - yellow per hour but red per km gives RED`() {
        // Marcin's screen 2026-05-11: 25.61 zł / 45 min / 20.0 km = 34 PLN/h + 1.3 PLN/km
        // Progi: yellow_h=34, yellow_km=2. Hour spełnia yellow, km nie spełnia nawet yellow → RED.
        val result = analyzer.analyze(offer(25.61, 45, distanceKm = 20.0), combinedThresholds)
        assertEquals(34.0, result.zlPerHour!!, 0.5)
        assertEquals(1.28, result.zlPerKm!!, 0.05)
        assertEquals(ProfitLevel.RED, result.level)
    }

    @Test
    fun `combined - both green gives GREEN`() {
        // 40 zł, 60 min, 8 km → 40 zł/h (green) + 5 zł/km (green) → GREEN
        val result = analyzer.analyze(offer(40.0, 60, distanceKm = 8.0), combinedThresholds)
        assertEquals(ProfitLevel.GREEN, result.level)
    }

    @Test
    fun `combined - green per hour but yellow per km gives YELLOW`() {
        // 50 zł, 60 min, 20 km → 50 zł/h (green) + 2.5 zł/km (yellow) → YELLOW
        val result = analyzer.analyze(offer(50.0, 60, distanceKm = 20.0), combinedThresholds)
        assertEquals(ProfitLevel.YELLOW, result.level)
    }

    @Test
    fun `combined - green per hour but red per km gives RED`() {
        // 50 zł, 60 min, 50 km → 50 zł/h (green) + 1 zł/km (red) → RED
        val result = analyzer.analyze(offer(50.0, 60, distanceKm = 50.0), combinedThresholds)
        assertEquals(ProfitLevel.RED, result.level)
    }

    @Test
    fun `combined - missing distance falls back to per-hour only`() {
        // Bez dystansu — combined logic nie ma drugiego wymiaru, decyduje sam zł/h.
        // 50 zł/h (green) bez km → GREEN (nie spada do RED).
        val result = analyzer.analyze(offer(50.0, 60, distanceKm = null), combinedThresholds)
        assertEquals(ProfitLevel.GREEN, result.level)
    }

    @Test
    fun `combined - zero distance falls back to per-hour only`() {
        // distanceKm = 0 też powinno fallback do tylko zł/h (nie dzielimy przez zero).
        val result = analyzer.analyze(offer(50.0, 60, distanceKm = 0.0), combinedThresholds)
        assertEquals(ProfitLevel.GREEN, result.level)
        assertNull(result.zlPerKm)
    }

    @Test
    fun `combined - glovo path (zero minutes) uses only zlPerKm as before`() {
        // Regresja: dla Glovo (estimatedMinutes = 0) zachowanie bez zmian — sam zł/km decyduje.
        // 30 zł, 0 min, 6 km → 5 zł/km → GREEN (>= 3.0)
        val result = analyzer.analyze(offer(30.0, 0, distanceKm = 6.0), combinedThresholds)
        assertNull(result.zlPerHour)
        assertEquals(ProfitLevel.GREEN, result.level)
    }
}