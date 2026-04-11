package com.orderpilot.app.parser

import com.orderpilot.app.di.AppLog
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform

class WoltOcrParser : OcrOfferParser {

    override val platform = Platform.WOLT
    override val supportedPackages = setOf("com.wolt.courierapp")

    // Czas jako zakres: "16 - 19 min", "16 – 19 min", "16—19 min"
    // Bierzemy MAX (konserwatywne — niższe zł/h)
    private val timeRangeRegex = Regex("""(\d+)[\s\u00A0]*[-\u2013\u2014][\s\u00A0]*(\d+)[\s\u00A0]*(?:min|хв|XB|мин)""", RegexOption.IGNORE_CASE)

    // Fallback: czas jako pojedyncza wartość "19 min"
    private val timeSingleRegex = Regex("""(\d+)[\s\u00A0]*(?:min|хв|XB|мін|мин)""", RegexOption.IGNORE_CASE)

    // Godziny: "1 godz.", "2 год", "1 hr"
    private val hourRegex = Regex("""(\d+)[\s\u00A0]*(?:godz|год|hr|hour)""", RegexOption.IGNORE_CASE)

    // Dystans: "1.7 km", "4.1 km" lub "5 km" (OCR czasem gubi część dziesiętną)
    private val distanceRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*(?:km|км)""", RegexOption.IGNORE_CASE)

    // Frazy specyficzne dla Ubera/Bolta — Wolt nigdy ich nie używa.
    private val rivalPlatformMarkers = listOf(
        // Uber PL/EN/UK
        "Łącznie", "Lacznie", "Загалом",
        "Dostawa ·", "Delivery ·",
        "Jesteś w trybie online", "You're online", "Ви онлайн",
        "Includes expected tip",
        // Uber RU
        "Итого", "Всего",
        "Вы онлайн", "Вы в сети",
        "Включая чаевые",
        // Bolt offer popup / idle
        "Decline", "Show map", "Looking for orders", "Go offline"
    )

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")

        AppLog.d(AppLog.TAG_PARSER, "Wolt OCR: $text")

        // Guard: odrzuć tekst z UI innej platformy kurierskiej
        rivalPlatformMarkers.firstOrNull { text.contains(it, ignoreCase = true) }?.let { marker ->
            AppLog.d(AppLog.TAG_PARSER, "Wolt: skipping — rival platform text detected ('$marker')")
            return null
        }

        // Kwota — wspólna logika z fallbackiem
        val (rawAmount, parsedAmount) = OcrOfferParser.extractAmount(text) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Wolt: no amount found | text=${text.take(200)}")
            return null
        }
        val amount = OcrOfferParser.sanitizeAmount(rawAmount, parsedAmount) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Wolt: amount rejected by sanitize")
            return null
        }

        val hours = hourRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val mins = timeRangeRegex.find(text)?.let { match ->
            // Zakres: bierzemy większą wartość (MAX = konserwatywne)
            val max = maxOf(
                match.groupValues[1].toIntOrNull() ?: 0,
                match.groupValues[2].toIntOrNull() ?: 0
            )
            if (max > 0) max else null
        } ?: timeSingleRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Wolt: no time found | text=${text.take(200)}")
            return null
        }
        val minutes = hours * 60 + mins

        val distance = distanceRegex.find(text)?.groupValues?.get(1)?.toDoubleLocale()

        val offer = Offer(Platform.WOLT, amount, minutes, distance, OcrOfferParser.detectCurrency(text))
        AppLog.d(AppLog.TAG_PARSER, "Wolt parsed offer: $offer")
        return offer
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}
