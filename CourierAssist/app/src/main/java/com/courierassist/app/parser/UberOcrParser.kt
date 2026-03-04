package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class UberOcrParser : OcrOfferParser {

    override val platform = Platform.UBER
    override val supportedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")

    private data class RegexSet(val amount: Regex, val time: Regex, val distance: Regex)

    private val regexSets = mapOf(
        AppLanguage.PL to RegexSet(
            amount = Regex("""(\d+[.,]\d+)\s*zł""", RegexOption.IGNORE_CASE),
            time = Regex("""(\d+)\s*min""", RegexOption.IGNORE_CASE),
            distance = Regex("""\((\d+[.,]\d+)\s*km\)""", RegexOption.IGNORE_CASE)
        ),
        AppLanguage.UK to RegexSet(
            amount = Regex("""(\d+[.,]\d+)\s*грн"""),
            time = Regex("""(\d+)\s*хв"""),
            distance = Regex("""\((\d+[.,]\d+)\s*км\)""")
        ),
        AppLanguage.EN to RegexSet(
            amount = Regex("""(\d+[.,]\d+)\s*(?:zł|PLN)""", RegexOption.IGNORE_CASE),
            time = Regex("""(\d+)\s*min""", RegexOption.IGNORE_CASE),
            distance = Regex("""\((\d+[.,]\d+)\s*km\)""", RegexOption.IGNORE_CASE)
        )
    )

    override fun parse(ocrLines: List<String>, language: AppLanguage): Offer? {
        val regex = regexSets[language] ?: regexSets[AppLanguage.PL]!!
        val text = ocrLines.joinToString(" ")
        val amount = regex.amount.find(text)?.groupValues?.get(1)?.toDoubleLocale() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "No amount found")
            return null
        }
        val minutes = regex.time.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "No time found")
            return null
        }
        val distance = regex.distance.find(text)?.groupValues?.get(1)?.toDoubleLocale()
        val offer = Offer(Platform.UBER, amount, minutes, distance)
        AppLog.d(AppLog.TAG_PARSER, "Parsed offer: $offer")
        return offer
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}