package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

interface OcrOfferParser {
    val platform: Platform
    val supportedPackages: Set<String>
    fun parse(ocrLines: List<String>): Offer?

    companion object {
        private const val MIN_REALISTIC_AMOUNT = 3.0
        private const val MAX_REALISTIC_AMOUNT = 150.0

        /**
         * Koryguje kwotę gdy OCR zgubił separator dziesiętny.
         * @param rawMatch dopasowany ciąg znaków z regex (np. "1720", "17,20")
         * @param parsed sparsowana wartość (np. 1720.0, 17.20)
         */
        fun sanitizeAmount(rawMatch: String, parsed: Double): Double? {
            val hasSeparator = rawMatch.contains(',') || rawMatch.contains('.')
            val corrected = when {
                hasSeparator -> parsed  // OCR widział separator — ufamy
                parsed >= 1000 -> {
                    // 4+ cyfry bez separatora: 1720 → 17.20
                    val c = parsed / 100.0
                    AppLog.w(AppLog.TAG_PARSER, "Amount corrected: $rawMatch → $c (div/100)")
                    c
                }
                parsed >= 100 -> {
                    // 3 cyfry bez separatora — nie wiemy czy np. 850=8.50 czy 85.0
                    // Zostawiamy i logujemy, zbieramy dane z produkcji
                    AppLog.w(AppLog.TAG_PARSER, "Amount 3-digit no separator: $rawMatch — leaving as-is")
                    parsed
                }
                else -> parsed  // < 100 — prawidłowa wartość
            }
            return when {
                corrected < MIN_REALISTIC_AMOUNT -> {
                    AppLog.w(AppLog.TAG_PARSER, "Amount $corrected below minimum — rejecting")
                    null
                }
                corrected > MAX_REALISTIC_AMOUNT ->  {
                    AppLog.w(AppLog.TAG_PARSER, "Amount $corrected above maximum — keeping (rare large order)")
                    corrected
                }
                else -> corrected
            }
        }
    }
}