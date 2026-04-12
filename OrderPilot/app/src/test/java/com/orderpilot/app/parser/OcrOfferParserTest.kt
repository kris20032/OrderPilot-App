package com.orderpilot.app.parser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OcrOfferParserTest {

    // --- containsCashMarkers ---

    @Test
    fun `PL - detects gotówka`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Płatność gotówką partnerowi 31,50 zł"))
    }

    @Test
    fun `PL - detects gotowka without diacritics`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Zaplac gotowka"))
    }

    @Test
    fun `EN - detects cash payment`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Cash payment required"))
    }

    @Test
    fun `EN - detects pay in cash`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Please pay in cash at delivery"))
    }

    @Test
    fun `EN - detects cash on delivery`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Cash on delivery"))
    }

    @Test
    fun `UK - detects готівкою`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Оплата готівкою 120 грн"))
    }

    @Test
    fun `RU - detects наличными`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Оплата наличными"))
    }

    @Test
    fun `RU - detects наличн prefix`() {
        assertTrue(OcrOfferParser.containsCashMarkers("Наличный расчёт"))
    }

    @Test
    fun `returns false for regular text without cash markers`() {
        assertFalse(OcrOfferParser.containsCashMarkers("11,50 zł Pizzeria 105 1,4 km Dostawa 1,6 km"))
    }

    @Test
    fun `returns false for empty text`() {
        assertFalse(OcrOfferParser.containsCashMarkers(""))
    }

    @Test
    fun `case insensitive detection`() {
        assertTrue(OcrOfferParser.containsCashMarkers("GOTÓWKA"))
        assertTrue(OcrOfferParser.containsCashMarkers("Cash Payment"))
        assertTrue(OcrOfferParser.containsCashMarkers("CASH ON DELIVERY"))
    }
}
