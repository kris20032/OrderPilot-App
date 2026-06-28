package com.orderpilot.app.parser

import com.orderpilot.app.domain.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class WoltOcrParserTest {

    private val parser = WoltOcrParser()

    // Layer-4 gate (#36): parse() wymaga markera popupu Wolta ("Spodziewany zarobek",
    // "Odbiór za", "Dostawa od", "Wolt" ...). Fixtury realnych ofert zawierają taki marker
    // (tu zwykle "Wolt"). Commit 15c131d dodał bramkę bez aktualizacji testów → były czerwone.

    @Test
    fun `parses full offer with time range - takes max`() {
        val lines = listOf("11,73 zł", "Spodziewany zarobek za pełną dostawę", "1.7 km", "16 - 19 min")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.73, offer!!.amount, 0.01)
        assertEquals(19, offer.estimatedMinutes) // MAX z zakresu
        assertEquals(1.7, offer.distanceKm!!, 0.01)
        assertEquals(Platform.WOLT, offer.platform)
        assertEquals("zł", offer.currency)
    }

    @Test
    fun `parses offer dark mode with promo banner`() {
        // Baner "800 zł" nie może nadpisać kwoty zlecenia "14,50 zł"
        val lines = listOf(
            "14,50 zł",
            "Spodziewany zarobek za pełną dostawę",
            "Ukończ 100 zadań, aby otrzymać 800 zł!",
            "Dostawa od",
            "SHMartel 2.0",
            "4.1 km",
            "26 - 29 min"
        )
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(14.50, offer!!.amount, 0.01) // pierwsza kwota, nie 800
        assertEquals(29, offer.estimatedMinutes) // MAX
        assertEquals(4.1, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `parses amount with dot separator`() {
        val lines = listOf("14.50 zł", "26 - 29 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(14.5, offer!!.amount, 0.01)
    }

    @Test
    fun `parses amount with comma separator`() {
        val lines = listOf("11,73 zł", "16 - 19 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.73, offer!!.amount, 0.01)
    }

    @Test
    fun `parses distance with comma`() {
        val lines = listOf("11,73 zł", "1,7 km", "16 - 19 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(1.7, offer!!.distanceKm!!, 0.01)
    }

    @Test
    fun `parses offer without distance`() {
        val lines = listOf("11,73 zł", "16 - 19 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertNull(offer!!.distanceKm)
    }

    @Test
    fun `parses single time without range`() {
        val lines = listOf("11,73 zł", "19 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(19, offer!!.estimatedMinutes)
    }

    @Test
    fun `time range with en-dash`() {
        val lines = listOf("11,73 zł", "16 \u2013 19 min", "Wolt") // en-dash
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(19, offer!!.estimatedMinutes)
    }

    @Test
    fun `time range with em-dash`() {
        val lines = listOf("11,73 zł", "16\u201419 min", "Wolt") // em-dash
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(19, offer!!.estimatedMinutes)
    }

    @Test
    fun `returns null when no amount`() {
        val lines = listOf("Spodziewany zarobek", "1.7 km", "16 - 19 min")
        val offer = parser.parse(lines)
        assertNull(offer)
    }

    @Test
    fun `returns null when no time`() {
        // Marker obecny → bramka przepuszcza; null wynika z BRAKU czasu (nie z bramki).
        val lines = listOf("11,73 zł", "1.7 km", "Wolt")
        val offer = parser.parse(lines)
        assertNull(offer)
    }

    @Test
    fun `returns null for empty lines`() {
        val offer = parser.parse(emptyList())
        assertNull(offer)
    }

    @Test
    fun `PLN currency variant`() {
        val lines = listOf("11,73 PLN", "16 - 19 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.73, offer!!.amount, 0.01)
    }

    // --- EN prefix currency ---

    @Test
    fun `EN - PLN before amount no space`() {
        val lines = listOf("PLN14.50", "26 - 29 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(14.50, offer!!.amount, 0.01)
        assertEquals(29, offer.estimatedMinutes)
    }

    @Test
    fun `EN - PLN before amount with space`() {
        val lines = listOf("PLN 14.50", "26 - 29 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(14.50, offer!!.amount, 0.01)
    }

    @Test
    fun `fallback - amount without currency`() {
        val lines = listOf("14.50", "26 - 29 min", "Wolt")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(14.50, offer!!.amount, 0.01)
    }

    // --- Layer-4 gate (#36) ---

    @Test
    fun `gate - rejects offer-like text without Wolt marker`() {
        // Treść jak oferta ale BEZ markera popupu Wolta → odrzucone (obrona z #36).
        val lines = listOf("14.50 zł", "26 - 29 min", "1.7 km")
        assertNull(parser.parse(lines))
    }

    // --- ParserRegistry ---

    @Test
    fun `ParserRegistry returns WoltOcrParser for wolt package`() {
        val registry = ParserRegistry(listOf(WoltOcrParser()))
        assertNotNull(registry.getParser("com.wolt.courierapp"))
    }

    @Test
    fun `ParserRegistry getAllWatchedPackages includes wolt`() {
        val registry = ParserRegistry(listOf(UberOcrParser(), WoltOcrParser()))
        val packages = registry.getAllWatchedPackages()
        assert("com.wolt.courierapp" in packages)
        assert("com.ubercab.driver" in packages)
    }
}