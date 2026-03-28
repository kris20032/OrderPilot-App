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

    // Ekran pierwszy (bez scrollu): kwota + tylko 1 dystans → null (czekamy na pełne dane)
    @Test
    fun `returns null for partial offer - one distance visible`() {
        val lines = listOf("11,50 zł", "Pizzeria 105", "1,4 km", "Stanisława Wyspiańskiego 2")
        val offer = parser.parse(lines)
        assertNull(offer)
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
        val lines = listOf("11,50 zt", "1,4 km", "1,6 km")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.50, offer!!.amount, 0.01)
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

    // --- EN prefix currency ---

    @Test
    fun `EN - PLN before amount`() {
        val lines = listOf("PLN11.50", "Pizzeria 105", "1,4 km", "Dostawa", "1,6 km")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.50, offer!!.amount, 0.01)
        assertEquals(3.0, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `fallback - amount without currency`() {
        val lines = listOf("11.50", "Pizzeria 105", "1,4 km", "Dostawa", "1,6 km")
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.50, offer!!.amount, 0.01)
    }

    @Test
    fun `does not match uber package`() {
        val registry = ParserRegistry(listOf(GlovoOcrParser()))
        assertNull(registry.getParser("com.ubercab.eats"))
    }

    // --- Testy filtra gotówkowego ---

    @Test
    fun `returns null for cash payment screen - Zapłać gotówką partnerowi`() {
        // Prawdziwy tekst z logów — ekran "zapłać gotówką w restauracji"
        val lines = listOf(
            "Status Online direction Kebab Lamh 1 zamówienie Jaśkowa Dolina 101",
            "80-287 Gdańsk Poland , GDN Firma: Kebab Lamh Gotowe za ok. 5 min",
            "#935 101599661750 - #935 - Marcin 1 pozycja",
            "Płatność Zapłać gotówką partnerowi 31,50 zł",
            "Łączna płatność Zapłać gotówką u partnera zł 31.50",
            "(Klient poprosił o resztę za 40,00 zł) Potwierdź odbiór"
        )
        assertNull(parser.parse(lines))
    }

    @Test
    fun `filters cash amount when courier earnings also present`() {
        // Oferta z kwotą wynagrodzenia (10,74 zł) + gotówka partnerowi (31,50 zł)
        val lines = listOf(
            "10,74 zł", "Kebab Lamh", "1,4 km", "Dostawa", "1,6 km",
            "Płatność Zapłać gotówką partnerowi 31,50 zł"
        )
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(10.74, offer!!.amount, 0.01)
        assertEquals(3.0, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `filters reszta za amount`() {
        // "Klient poprosił o resztę za 40,00 zł" — 40 zł to nie zarobek
        val lines = listOf(
            "10,74 zł", "1,4 km", "1,6 km",
            "(Klient poprosił o resztę za 40,00 zł)"
        )
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(10.74, offer!!.amount, 0.01)
    }

    @Test
    fun `returns null for Potwierdz odbior screen`() {
        val lines = listOf(
            "11,50 zł", "1,4 km", "1,6 km", "Potwierdź odbiór"
        )
        assertNull(parser.parse(lines))
    }

    @Test
    fun `filters ODBIERZ cash amount - existing behavior`() {
        val lines = listOf(
            "10,74 zł", "1,4 km", "1,6 km", "ODBIERZ 65,41 zł"
        )
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(10.74, offer!!.amount, 0.01)
    }

    // --- Testy "ZAPŁAĆ" na ekranie oferty ---

    @Test
    fun `filters ZAPŁAĆ cash amount on offer screen`() {
        // PL: Ekran oferty z "POTRZEBNA GOTÓWKA" — przycisk "ZAPŁAĆ 43,99 zł"
        val lines = listOf(
            "11,32 zł", "Apteczka Zdrowia", "0,36 km",
            "Dostawa", "1,38 km",
            "POTRZEBNA GOTÓWKA", "ZAPŁAĆ 43,99 zł"
        )
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.32, offer!!.amount, 0.01)
        assertEquals(1.74, offer.distanceKm!!, 0.01)
    }

    @Test
    fun `filters PAY cash amount on offer screen - EN`() {
        val lines = listOf(
            "11,32 zł", "Apteczka Zdrowia", "0,36 km",
            "Dostawa", "1,38 km",
            "CASH NEEDED", "PAY 43,99 PLN"
        )
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.32, offer!!.amount, 0.01)
    }

    @Test
    fun `filters СПЛАТИТИ cash amount on offer screen - UK`() {
        val lines = listOf(
            "11,32 грн", "Аптека Здоров'я", "0,36 km",
            "Доставка", "1,38 km",
            "ПОТРІБНА ГОТІВКА", "СПЛАТИТИ 43,99 грн"
        )
        val offer = parser.parse(lines)
        assertNotNull(offer)
        assertEquals(11.32, offer!!.amount, 0.01)
    }

    @Test
    fun `returns null for Confirm pickup screen - EN`() {
        val lines = listOf(
            "11,50 zł", "1,4 km", "1,6 km", "Confirm pickup"
        )
        assertNull(parser.parse(lines))
    }

    @Test
    fun `returns null for Ukrainian confirm screen`() {
        val lines = listOf(
            "11,50 грн", "1,4 km", "1,6 km", "Підтвердити отримання"
        )
        assertNull(parser.parse(lines))
    }
}
