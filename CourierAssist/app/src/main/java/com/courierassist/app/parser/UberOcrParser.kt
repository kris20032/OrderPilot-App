package com.courierassist.app.parser

import android.util.Log
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class UberOcrParser {

    companion object {
        private const val TAG = "CourierAssist"

        // "14,64 zł" or "14.64 zł"
        private val AMOUNT_REGEX = Regex("""(\d+[.,]\d+)\s*zł""", RegexOption.IGNORE_CASE)

        // "26 min" — usually inside "Łącznie 26 min (5.6 km)"
        private val TIME_REGEX = Regex("""(\d+)\s*min""", RegexOption.IGNORE_CASE)

        // "(5.6 km)" or "(5,6 km)"
        private val DISTANCE_REGEX = Regex("""\((\d+[.,]\d+)\s*km\)""", RegexOption.IGNORE_CASE)

        // Accept button in PL / EN / UK
        private val ACCEPT_TEXTS = listOf("akceptuj", "accept", "прийняти")
    }

    fun parse(ocrLines: List<String>): Offer? {
        if (ocrLines.isEmpty()) return null

        var amount: Double? = null
        var minutes: Int? = null
        var distanceKm: Double? = null
        var hasAccept = false

        for (line in ocrLines) {
            if (amount == null) {
                AMOUNT_REGEX.find(line)?.let {
                    amount = it.groupValues[1].replace(",", ".").toDoubleOrNull()
                }
            }
            if (minutes == null) {
                TIME_REGEX.find(line)?.let {
                    minutes = it.groupValues[1].toIntOrNull()
                }
            }
            if (distanceKm == null) {
                DISTANCE_REGEX.find(line)?.let {
                    distanceKm = it.groupValues[1].replace(",", ".").toDoubleOrNull()
                }
            }
            if (!hasAccept && ACCEPT_TEXTS.any { line.lowercase().contains(it) }) {
                hasAccept = true
            }
        }

        if (amount == null || minutes == null || !hasAccept) {
            Log.d(TAG, "UberOcrParser: incomplete — amount=$amount minutes=$minutes hasAccept=$hasAccept")
            return null
        }

        val offer = Offer(
            platform = Platform.UBER,
            amount = amount!!,
            estimatedMinutes = minutes!!,
            distanceKm = distanceKm
        )
        Log.d(TAG, "UberOcrParser: parsed offer=$offer")
        return offer
    }
}
