package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class WoltOcrParser : OcrOfferParser {

    override val platform = Platform.WOLT
    override val supportedPackages = setOf("com.wolt.courierapp")

    // Kwota: "11,73 zł" lub "14,50 zł"
    // [\s\u00A0]* — obsługuje zwykłą spację i non-breaking space
    private val amountRegex = Regex("""(\d+[.,]\d+)[\s\u00A0]*(?:zł|PLN)""", RegexOption.IGNORE_CASE)

    // Czas jako zakres: "16 - 19 min", "16 – 19 min", "16—19 min"
    // Bierzemy MAX (konserwatywne — niższe zł/h)
    private val timeRangeRegex = Regex("""(\d+)[\s\u00A0]*[-\u2013\u2014][\s\u00A0]*(\d+)[\s\u00A0]*min""", RegexOption.IGNORE_CASE)

    // Fallback: czas jako pojedyncza wartość "19 min"
    private val timeSingleRegex = Regex("""(\d+)[\s\u00A0]*min""", RegexOption.IGNORE_CASE)

    // Dystans: "1.7 km" lub "4.1 km" (Wolt bez nawiasów, w odróżnieniu od Ubera)
    private val distanceRegex = Regex("""(\d+[.,]\d+)[\s\u00A0]*km""", RegexOption.IGNORE_CASE)

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")

        AppLog.d(AppLog.TAG_PARSER, "Wolt OCR: $text")
        val amount = amountRegex.find(text)?.groupValues?.get(1)?.toDoubleLocale() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Wolt: no amount found")
            return null
        }

        val minutes = timeRangeRegex.find(text)?.let { match ->
            // Zakres: bierzemy większą wartość (MAX = konserwatywne)
            val max = maxOf(
                match.groupValues[1].toIntOrNull() ?: 0,
                match.groupValues[2].toIntOrNull() ?: 0
            )
            if (max > 0) max else null
        } ?: timeSingleRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Wolt: no time found")
            return null
        }

        val distance = distanceRegex.find(text)?.groupValues?.get(1)?.toDoubleLocale()

        val offer = Offer(Platform.WOLT, amount, minutes, distance, "zł")
        AppLog.d(AppLog.TAG_PARSER, "Wolt parsed offer: $offer")
        return offer
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}