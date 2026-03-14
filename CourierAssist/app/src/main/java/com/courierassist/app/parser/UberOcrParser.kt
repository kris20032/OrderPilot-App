package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class UberOcrParser : OcrOfferParser {

    override val platform = Platform.UBER
    override val supportedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")

    // Uniwersalne regexy — łapią PL (zł), UK (грн), EN (PLN) jednocześnie
    // [\s\u00A0]* — obsługuje zwykłą spację i non-breaking space (Uber używa \u00A0)
    private val amountRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*(?:zł|грн|PLN)""", RegexOption.IGNORE_CASE)
    private val timeRegex = Regex("""(\d+)[\s\u00A0]*(?:min|хв)""", RegexOption.IGNORE_CASE)
    private val distanceRegex = Regex("""[\s\u00A0(](\d+[.,]\d+)[\s\u00A0]*(?:km|км)[\s\u00A0)]""", RegexOption.IGNORE_CASE)

    // Mapowanie waluty z tekstu OCR
    private val currencyRegex = Regex("""\d+[.,]\d+[\s\u00A0]*(zł|грн|PLN)""", RegexOption.IGNORE_CASE)

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")
        val amountMatch = amountRegex.find(text)?.groupValues?.get(1) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "No amount found")
            return null
        }
        val amount = OcrOfferParser.sanitizeAmount(amountMatch, amountMatch.toDoubleLocale() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "No amount found")
            return null
        }) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Amount rejected by sanitize")
            return null
        }
        val minutes = timeRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "No time found")
            return null
        }
        val distance = distanceRegex.find(text)?.groupValues?.get(1)?.toDoubleLocale()

        val detectedCurrency = currencyRegex.find(text)?.groupValues?.get(1) ?: "zł"

        val offer = Offer(Platform.UBER, amount, minutes, distance, detectedCurrency)
        AppLog.d(AppLog.TAG_PARSER, "Parsed offer: $offer")
        return offer
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}