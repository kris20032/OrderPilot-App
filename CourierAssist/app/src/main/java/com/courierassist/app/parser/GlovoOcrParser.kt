package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class GlovoOcrParser : OcrOfferParser {

    override val platform = Platform.GLOVO
    override val supportedPackages = setOf("com.glovo.courier", "com.logistics.rider.glovo")

    // Dystans: "1,4 km", "1.4 km", "3 km" — szukamy WSZYSTKICH wystąpień
    private val distanceRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*(?:km|км)""", RegexOption.IGNORE_CASE)

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")
        AppLog.d(AppLog.TAG_PARSER, "Glovo OCR: $text")

        // Guard: Uber popup — "Łącznie X min" to format Ubera, Glovo tego nie używa.
        // Gdy Glovo event triggeruje screenshot a na ekranie jest popup Ubera,
        // GlovoOcrParser widzi 1 kwotę + 1 dystans → "partial" → null, blokując Ubera.
        val uberMarkers = listOf("Łącznie", "Lacznie", "Загалом")
        if (uberMarkers.any { text.contains(it, ignoreCase = true) }) {
            AppLog.d(AppLog.TAG_PARSER, "Glovo: skipping — Uber popup text detected")
            return null
        }

        // Guard: ekran szczegółów zamówienia (nie oferta) — "Potwierdź odbiór" / UK / EN
        if (text.contains("Potwierdź odbiór", ignoreCase = true) ||
            text.contains("Potwierdz odbior", ignoreCase = true) ||
            text.contains("Підтвердити отримання", ignoreCase = true) ||
            text.contains("Confirm pickup", ignoreCase = true)) {
            AppLog.d(AppLog.TAG_PARSER, "Glovo: skipping — order details screen (Potwierdź odbiór)")
            return null
        }

        // Szukamy WSZYSTKICH kwot, odfiltruj kwoty gotówkowe (nie wynagrodzenie kuriera)
        val amounts = OcrOfferParser.findAllAmounts(text)
            .mapNotNull { match ->
                val prefixStart = maxOf(0, match.range.first - 40)
                val prefix = text.substring(prefixStart, match.range.first)

                // Filtruj kwoty gotówkowe — PL / UK / EN
                val isCashAmount = prefix.contains("ODBIERZ", ignoreCase = true) ||
                    prefix.contains("gotówką partnerowi", ignoreCase = true) ||
                    prefix.contains("gotówką u partnera", ignoreCase = true) ||
                    prefix.contains("gotowk", ignoreCase = true) ||
                    prefix.contains("ZAPŁAĆ", ignoreCase = true) ||
                    prefix.contains("Zaplac", ignoreCase = true) ||
                    prefix.contains("resztę za", ignoreCase = true) ||
                    prefix.contains("reszt", ignoreCase = true) ||
                    // UK
                    prefix.contains("СПЛАТИТИ", ignoreCase = true) ||
                    prefix.contains("ОПЛАТИТИ", ignoreCase = true) ||
                    prefix.contains("готівкою", ignoreCase = true) ||
                    prefix.contains("решту", ignoreCase = true) ||
                    // EN
                    prefix.contains("PAY ", ignoreCase = true) ||
                    prefix.contains("cash to partner", ignoreCase = true) ||
                    prefix.contains("change for", ignoreCase = true) ||
                    prefix.contains("COLLECT", ignoreCase = true)

                if (isCashAmount) {
                    AppLog.d(AppLog.TAG_PARSER, "Glovo: skipping cash amount ${match.groupValues[1]} (cash/change)")
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
            .filter { it > 0 && it < 50 }
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
                // Sumujemy WSZYSTKIE dystanse (mogą być 2+ przy złożonych zleceniach: multi-pickup + multi-delivery)
                val totalKm = distances.sum()
                AppLog.d(AppLog.TAG_PARSER, "Glovo full: amount=$amount distances=$distances totalKm=$totalKm")
                Offer(
                    platform = Platform.GLOVO,
                    amount = amount,
                    estimatedMinutes = 0,
                    distanceKm = totalKm,
                    currency = "zł",
                    pickupDistanceKm = distances[0],
                    isPartial = false
                )
            }
        }
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}
