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

        // Szukamy WSZYSTKICH kwot, odfiltruj "ODBIERZ X zł" (gotówka klienta, nie wynagrodzenie)
        val amounts = amountRegex.findAll(text)
            .mapNotNull { match ->
                val prefix = text.substring(maxOf(0, match.range.first - 12), match.range.first)
                if (prefix.contains("ODBIERZ", ignoreCase = true)) {
                    AppLog.d(AppLog.TAG_PARSER, "Glovo: skipping cash amount ${match.groupValues[1]} (ODBIERZ)")
                    return@mapNotNull null
                }
                val raw = match.groupValues[1]
                OcrOfferParser.sanitizeAmount(raw, raw.toDoubleLocale() ?: return@mapNotNull null)
            }
            .toList()

        AppLog.d(AppLog.TAG_PARSER, "Glovo: amounts found = $amounts")

        val amount = amounts.maxOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Glovo: no amount found")
            return null
        }

        // Szukamy wszystkich dystansów, bierzemy dwa NAJMNIEJSZE (< 20 km)
        // Pickup i delivery to krótkie trasy, dystanse z mapy bywają większe
        val distances = distanceRegex.findAll(text)
            .mapNotNull { it.groupValues[1].toDoubleLocale() }
            .filter { it > 0 && it < 20 }
            .sorted()
            .toList()

        AppLog.d(AppLog.TAG_PARSER, "Glovo: distances found (sorted) = $distances")

        return when {
            distances.isEmpty() -> {
                AppLog.w(AppLog.TAG_PARSER, "Glovo: no distances found")
                null
            }
            distances.size == 1 -> {
                // Tylko jeden dystans (pickup) — czekamy na pełne dane (pickup + delivery)
                AppLog.d(AppLog.TAG_PARSER, "Glovo partial: only pickup=${distances[0]}, waiting for delivery distance")
                null
            }
            else -> {
                // Dwa najmniejsze dystanse = pickup + delivery
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
