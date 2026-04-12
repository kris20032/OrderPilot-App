package com.orderpilot.app.parser

import com.orderpilot.app.domain.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UberOcrParserTest {

    private val parser = UberOcrParser()

    // --- PL ---

    @Test
    fun `PL - parses full offer with distance`() {
        val lines = listOf("34,58 zł", "12 min", "(9,1 km)", "AKCEPTUJ")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(34.58, offer!!.amount, 0.01)
        assertEquals(12, offer.estimatedMinutes)
        assertEquals(9.1, offer.distanceKm!!, 0.01)
        assertEquals(Platform.UBER, offer.platform)
    }

    @Test
    fun `PL - parses offer without distance`() {
        val lines = listOf("20,00 zł", "8 min", "AKCEPTUJ")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(20.0, offer!!.amount, 0.01)
        assertEquals(8, offer.estimatedMinutes)
        assertNull(offer.distanceKm)
    }

    @Test
    fun `PL - amount with dot separator`() {
        val lines = listOf("15.50 zł", "10 min")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(15.5, offer!!.amount, 0.01)
    }

    @Test
    fun `PL - case insensitive zl`() {
        val lines = listOf("18,00 ZŁ", "5 min")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(18.0, offer!!.amount, 0.01)
    }

    @Test
    fun `PL - multiline OCR text joined`() {
        val lines = listOf("Nowe zlecenie", "34,58 zł", "12 min (9,1 km)", "AKCEPTUJ")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(34.58, offer!!.amount, 0.01)
        assertEquals(12, offer.estimatedMinutes)
        assertEquals(9.1, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `PL - returns null when no amount`() {
        val lines = listOf("12 min", "(5,0 km)")
        val offer = parser.parse(lines)
        assertNull(offer)
    }

    @Test
    fun `PL - returns null when no time`() {
        val lines = listOf("25,00 zł", "(5,0 km)")
        val offer = parser.parse(lines)
        assertNull(offer)
    }

    @Test
    fun `PL - returns null for empty lines`() {
        val offer = parser.parse(emptyList())
        assertNull(offer)
    }

    // --- UK ---

    @Test
    fun `UK - parses full offer with distance`() {
        val lines = listOf("250,00 грн", "15 хв", "(8,5 км)")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(250.0, offer!!.amount, 0.01)
        assertEquals(15, offer.estimatedMinutes)
        assertEquals(8.5, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `UK - parses offer without distance`() {
        val lines = listOf("180,50 грн", "10 хв")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(180.5, offer!!.amount, 0.01)
        assertEquals(10, offer.estimatedMinutes)
        assertNull(offer.distanceKm)
    }

    @Test
    fun `UK - returns null when no amount`() {
        val lines = listOf("10 хв", "(5,0 км)")
        val offer = parser.parse(lines)
        assertNull(offer)
    }

    // --- EN ---

    @Test
    fun `EN - parses offer with zl`() {
        val lines = listOf("22,50 zł", "14 min", "(6,3 km)")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(22.5, offer!!.amount, 0.01)
        assertEquals(14, offer.estimatedMinutes)
        assertEquals(6.3, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `EN - parses offer with PLN`() {
        val lines = listOf("30,00 PLN", "20 min")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(30.0, offer!!.amount, 0.01)
        assertEquals(20, offer.estimatedMinutes)
    }

    // --- EN prefix currency (Ivan's Pixel 5) ---

    @Test
    fun `EN - parses PLN before amount no space`() {
        // Ivan's real screenshot: "PLN7.86", "14 min (1.0 km) total"
        val lines = listOf("Delivery", "PLN7.86", "14 min (1.0 km) total", "Accept")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(7.86, offer!!.amount, 0.01)
        assertEquals(14, offer.estimatedMinutes)
        assertEquals(1.0, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `EN - parses PLN before amount with space`() {
        val lines = listOf("PLN 7.86", "14 min (1.0 km)")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(7.86, offer!!.amount, 0.01)
    }

    @Test
    fun `EN - parses PLN after amount`() {
        val lines = listOf("7.86 PLN", "14 min")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(7.86, offer!!.amount, 0.01)
    }

    @Test
    fun `EN - parses PLN lowercase`() {
        val lines = listOf("pln7.86", "14 min")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(7.86, offer!!.amount, 0.01)
    }

    @Test
    fun `fallback - parses amount without currency symbol`() {
        // OCR nie odczytał waluty, ale kwota i czas są widoczne
        val lines = listOf("7.86", "14 min")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(7.86, offer!!.amount, 0.01)
    }

    @Test
    fun `fallback - does not match time as amount`() {
        // "14 min" — 14 nie ma separatora, nie powinno być wzięte jako kwota
        val lines = listOf("14 min")
        val offer = parser.parse(lines)
        assertNull(offer) // brak kwoty
    }

    // --- Statistics screen guard ---

    @Test
    fun `PL - rejects statistics screen with Statystyki`() {
        val lines = listOf("Statystyki", "Online", "2 godz. 31 min", "Pkt",
            "Przejazdy", "5", "Opłata netto", "70,74 zł", "Podsumowanie")
        assertNull(parser.parse(lines))
    }

    @Test
    fun `PL - rejects statistics screen with Podsumowanie`() {
        val lines = listOf("Sposób obliczania statystyk", "Podsumowanie",
            "Opłata netto", "70,74 zł", "Podatki", "16,28 zł",
            "Całkowity przychód 87,02 zł", "Zobacz przychody")
        assertNull(parser.parse(lines))
    }

    @Test
    fun `EN - rejects statistics screen`() {
        val lines = listOf("Statistics", "Online", "2 hr 31 min", "Trips", "5",
            "Net fare", "70.74 zł", "Summary")
        assertNull(parser.parse(lines))
    }

    // --- ParserRegistry ---

    @Test
    fun `ParserRegistry returns UberOcrParser for uber package`() {
        val registry = ParserRegistry(listOf(UberOcrParser()))
        assertNotNull(registry.getParser("com.ubercab.driver"))
        assertNotNull(registry.getParser("com.ubercab.eats"))
    }

    @Test
    fun `ParserRegistry returns null for unknown package`() {
        val registry = ParserRegistry(listOf(UberOcrParser()))
        assertNull(registry.getParser("com.wolt.android"))
    }

    @Test
    fun `ParserRegistry getAllWatchedPackages returns uber packages`() {
        val registry = ParserRegistry(listOf(UberOcrParser()))
        val packages = registry.getAllWatchedPackages()
        assert("com.ubercab.driver" in packages)
        assert("com.ubercab.eats" in packages)
    }
}