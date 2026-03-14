package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class GlovoOcrParser : OcrOfferParser {

    override val platform = Platform.GLOVO
    override val supportedPackages = setOf("com.glovo.courier", "com.logistics.rider.glovo")

    // Kwota: "11,50 zł", "11.50 zł", "17 zł" — OCR może gubić "ł" → "zl", "zt", "z"
    private val amountRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*(?:zł|zl|zt|z\b|PLN)""", RegexOption.IGNORE_CASE)

    // Dystans: "1,4 km", "1.4 km", "3 km" — szukamy WSZYSTKICH wystąpień
    private val distanceRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*km""", RegexOption.IGNORE_CASE)

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")
        AppLog.d(AppLog.TAG_PARSER, "Glovo OCR: $text")

        val amountMatch = amountRegex.find(text)?.groupValues?.get(1) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Glovo: no amount found")
            return null
        }
        val amount = OcrOfferParser.sanitizeAmount(amountMatch, amountMatch.toDoubleLocale() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Glovo: no amount found")
            return null
        }) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Glovo: amount rejected by sanitize")
            return null
        }

        // Szukamy wszystkich dystansów w tekście
        val distances = distanceRegex.findAll(text)
            .mapNotNull { it.groupValues[1].toDoubleLocale() }
            .filter { it > 0 }
            .toList()

        AppLog.d(AppLog.TAG_PARSER, "Glovo: distances found = $distances")

        return when (distances.size) {
            0 -> {
                // Sama kwota — za mało danych
                AppLog.w(AppLog.TAG_PARSER, "Glovo: no distances found")
                null
            }
            1 -> {
                // Tylko dystans do restauracji — partial, czekamy na scroll
                val pickupKm = distances[0]
                AppLog.d(AppLog.TAG_PARSER, "Glovo partial: amount=$amount pickupKm=$pickupKm")
                Offer(
                    platform = Platform.GLOVO,
                    amount = amount,
                    estimatedMinutes = 0,
                    distanceKm = pickupKm,
                    currency = "zł",
                    pickupDistanceKm = pickupKm,
                    isPartial = true
                )
            }
            else -> {
                // Oba dystanse widoczne (po scrollu) — pełna oferta
                val pickupKm = distances[0]
                val deliveryKm = distances[1]
                val totalKm = pickupKm + deliveryKm
                AppLog.d(AppLog.TAG_PARSER, "Glovo full: amount=$amount pickupKm=$pickupKm deliveryKm=$deliveryKm totalKm=$totalKm")
                Offer(
                    platform = Platform.GLOVO,
                    amount = amount,
                    estimatedMinutes = 0,
                    distanceKm = totalKm,
                    currency = "zł",
                    pickupDistanceKm = pickupKm,
                    isPartial = false
                )
            }
        }
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}
