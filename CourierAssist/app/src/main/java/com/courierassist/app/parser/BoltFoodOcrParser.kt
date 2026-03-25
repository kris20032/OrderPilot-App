package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class BoltFoodOcrParser : OcrOfferParser {

    override val platform = Platform.BOLT
    override val supportedPackages = setOf(
        "com.bolt.deliverycourier",
        "com.bolt.courier",
        "com.bolt.food.courier",
        "ee.mtakso.courier"
    )

    // Przycisk akceptuj: "2.2 km, 27 min, 8,22 zł"
    // Szukamy kwoty, czasu i dystansu niezależnie — mogą być w różnej kolejności
    // rpH = "грн" czytane przez Latin OCR, XB = "хв"
    private val amountRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*(?:zł|zl|zt|z\b|PLN|грн|rpH)""", RegexOption.IGNORE_CASE)
    private val timeRegex = Regex("""(\d+)[\s\u00A0]*(?:min|хв|XB)""", RegexOption.IGNORE_CASE)
    private val hourRegex = Regex("""(\d+)[\s\u00A0]*(?:godz|год|hr|hour)""", RegexOption.IGNORE_CASE)
    private val distanceRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*(?:km|км)""", RegexOption.IGNORE_CASE)

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")
        AppLog.d(AppLog.TAG_PARSER, "Bolt OCR: $text")

        // Szukamy WSZYSTKICH kwot i bierzemy NAJWIĘKSZĄ (przycisk ma kwotę zlecenia)
        val amounts = amountRegex.findAll(text)
            .mapNotNull { match ->
                val raw = match.groupValues[1]
                OcrOfferParser.sanitizeAmount(raw, raw.toDoubleLocale() ?: return@mapNotNull null)
            }
            .toList()

        AppLog.d(AppLog.TAG_PARSER, "Bolt: amounts found = $amounts")

        val amount = amounts.maxOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Bolt: no amount found")
            return null
        }

        // Czas: szukamy wszystkich, bierzemy NAJWIĘKSZY (suma na przycisku)
        val allMinutes = timeRegex.findAll(text)
            .mapNotNull { it.groupValues[1].toIntOrNull() }
            .toList()
        val hours = hourRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val minutes = (allMinutes.maxOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Bolt: no time found")
            return null
        }) + hours * 60

        // Dystans: szukamy wszystkich, bierzemy NAJWIĘKSZY (suma na przycisku)
        val allDistances = distanceRegex.findAll(text)
            .mapNotNull { it.groupValues[1].toDoubleLocale() }
            .filter { it > 0 }
            .toList()

        AppLog.d(AppLog.TAG_PARSER, "Bolt: times=$allMinutes hours=$hours distances=$allDistances")

        val distance = allDistances.maxOrNull()

        val offer = Offer(Platform.BOLT, amount, minutes, distance, "zł")
        AppLog.d(AppLog.TAG_PARSER, "Bolt parsed offer: $offer")
        return offer
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}
