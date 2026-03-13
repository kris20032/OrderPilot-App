package com.courierassist.app.parser

import com.courierassist.app.domain.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GlovoOcrParserTest {

    private val parser = GlovoOcrParser()

    // Ekran pierwszy (bez scrollu): kwota + dystans do restauracji
    @Test
    fun `parses partial offer - one distance visible`() {
        val lines = listOf("11,50 zł", "Pizzeria 105", "1,4 km", "Stanisława Wyspiańskiego 2")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.50, offer!!.amount, 0.01)
        assertEquals(0, offer.estimatedMinutes)
        assertEquals(1.4, offer.distanceKm!!, 0.01)
        assertEquals(1.4, offer.pickupDistanceKm!!, 0.01)
        assertTrue(offer.isPartial)
        assertEquals(Platform.GLOVO, offer.platform)
    }

    // Ekran po scrollu: kwota + oba dystanse
    @Test
    fun `parses full offer - two distances visible`() {
        val lines = listOf("11,50 zł", "Pizzeria 105", "1,4 km", "Dostawa", "1,6 km")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.50, offer!!.amount, 0.01)
        assertEquals(0, offer.estimatedMinutes)
        assertEquals(3.0, offer.distanceKm!!, 0.01)   // 1.4 + 1.6
        assertEquals(1.4, offer.pickupDistanceKm!!, 0.01)
        assertFalse(offer.isPartial)
    }

    // OCR gubił kropkę → "1,4 km" może być "14 km" — zostawiamy jak jest, regexem matchujemy co OCR da
    @Test
    fun `parses amount with dot separator`() {
        val lines = listOf("11.50 zł", "1.4 km", "1.6 km")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.5, offer!!.amount, 0.01)
        assertEquals(3.0, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `parses OCR variant - zl instead of zł`() {
        val lines = listOf("11,50 zl", "1,4 km", "1,6 km")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.50, offer!!.amount, 0.01)
    }

    @Test
    fun `parses OCR variant - zt instead of zł`() {
        val lines = listOf("11,50 zt", "1,4 km")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.50, offer!!.amount, 0.01)
        assertTrue(offer.isPartial)
    }

    @Test
    fun `returns null when no amount`() {
        val lines = listOf("Pizzeria 105", "1,4 km", "1,6 km")
        assertNull(parser.parse(lines))
    }

    @Test
    fun `returns null when no distances`() {
        val lines = listOf("11,50 zł", "Pizzeria 105 Stopiątka")
        assertNull(parser.parse(lines))
    }

    @Test
    fun `returns null for empty lines`() {
        assertNull(parser.parse(emptyList()))
    }

    @Test
    fun `ParserRegistry returns GlovoOcrParser for com_glovo_courier`() {
        val registry = ParserRegistry(listOf(GlovoOcrParser()))
        assertNotNull(registry.getParser("com.glovo.courier"))
    }

    @Test
    fun `ParserRegistry returns GlovoOcrParser for com_logistics_rider_glovo`() {
        val registry = ParserRegistry(listOf(GlovoOcrParser()))
        assertNotNull(registry.getParser("com.logistics.rider.glovo"))
    }

    @Test
    fun `does not match uber package`() {
        val registry = ParserRegistry(listOf(GlovoOcrParser()))
        assertNull(registry.getParser("com.ubercab.eats"))
    }
}
