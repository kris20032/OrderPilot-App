package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class UberOcrParser : OcrOfferParser {

    override val platform = Platform.UBER
    override val supportedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")

    // Uniwersalne regexy — łapią PL (zł), UK (грн), EN (PLN) jednocześnie
    private val amountRegex = Regex("""(\d+[.,]\d+)\s*(?:zł|грн|PLN)""", RegexOption.IGNORE_CASE)
    private val timeRegex = Regex("""(\d+)\s*(?:min|хв)""", RegexOption.IGNORE_CASE)
    private val distanceRegex = Regex("""\((\d+[.,]\d+)\s*(?:km|км)\)""", RegexOption.IGNORE_CASE)

    // Mapowanie waluty z tekstu OCR
    private val currencyRegex = Regex("""\d+[.,]\d+\s*(zł|грн|PLN)""", RegexOption.IGNORE_CASE)

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")
        val amount = amountRegex.find(text)?.groupValues?.get(1)?.toDoubleLocale() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "No amount found")
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