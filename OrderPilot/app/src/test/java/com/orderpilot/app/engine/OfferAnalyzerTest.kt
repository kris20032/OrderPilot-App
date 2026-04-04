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
}