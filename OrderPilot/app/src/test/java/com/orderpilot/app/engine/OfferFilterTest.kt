package com.orderpilot.app.engine

import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.settings.FilterConfig
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfferFilterTest {

    private val filter = OfferFilter()

    private fun offer(distanceKm: Double?) =
        Offer(platform = Platform.UBER, amount = 20.0, estimatedMinutes = 30, distanceKm = distanceKm)

    // --- brak filtrów ---

    @Test
    fun `passes when no filters set`() {
        val config = FilterConfig()
        assertTrue(filter.passes(offer(5.0), config))
    }

    @Test
    fun `passes when distance is null and no filters set`() {
        val config = FilterConfig()
        assertTrue(filter.passes(offer(null), config))
    }

    // --- minDistanceKm ---

    @Test
    fun `passes when distance equals minDistanceKm`() {
        val config = FilterConfig(minDistanceKm = 3.0)
        assertTrue(filter.passes(offer(3.0), config))
    }

    @Test
    fun `passes when distance exceeds minDistanceKm`() {
        val config = FilterConfig(minDistanceKm = 3.0)
        assertTrue(filter.passes(offer(5.0), config))
    }

    @Test
    fun `fails when distance is below minDistanceKm`() {
        val config = FilterConfig(minDistanceKm = 3.0)
        assertFalse(filter.passes(offer(2.0), config))
    }

    // --- maxDistanceKm ---

    @Test
    fun `passes when distance equals maxDistanceKm`() {
        val config = FilterConfig(maxDistanceKm = 10.0)
        assertTrue(filter.passes(offer(10.0), config))
    }

    @Test
    fun `passes when distance is below maxDistanceKm`() {
        val config = FilterConfig(maxDistanceKm = 10.0)
        assertTrue(filter.passes(offer(7.0), config))
    }

    @Test
    fun `fails when distance exceeds maxDistanceKm`() {
        val config = FilterConfig(maxDistanceKm = 10.0)
        assertFalse(filter.passes(offer(15.0), config))
    }

    // --- oba filtry ---

    @Test
    fun `passes when distance is within range`() {
        val config = FilterConfig(minDistanceKm = 2.0, maxDistanceKm = 8.0)
        assertTrue(filter.passes(offer(5.0), config))
    }

    @Test
    fun `fails when distance is below range`() {
        val config = FilterConfig(minDistanceKm = 2.0, maxDistanceKm = 8.0)
        assertFalse(filter.passes(offer(1.0), config))
    }

    @Test
    fun `fails when distance is above range`() {
        val config = FilterConfig(minDistanceKm = 2.0, maxDistanceKm = 8.0)
        assertFalse(filter.passes(offer(9.0), config))
    }

    // --- distanceKm = null z filtrami ---

    @Test
    fun `passes when distance is null regardless of filters`() {
        val config = FilterConfig(minDistanceKm = 2.0, maxDistanceKm = 8.0)
        assertTrue(filter.passes(offer(null), config))
    }
}