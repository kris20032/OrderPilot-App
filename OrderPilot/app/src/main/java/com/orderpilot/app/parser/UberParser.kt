package com.orderpilot.app.parser

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import java.util.Locale

class UberParser : OfferParser {

    companion object {
        private const val TAG = "OrderPilot"
        const val UBER_DRIVER_PACKAGE = "com.ubercab.driver"

        // Regex for amount: "35 zł", "35.00 PLN", "35,50 zł", "₴150"
        private val AMOUNT_REGEX = Regex(
            """(\d+[.,]?\d*)\s*(?:zł|PLN|грн|₴|UAH)""",
            RegexOption.IGNORE_CASE
        )
        private val AMOUNT_REGEX_REVERSED = Regex(
            """(?:zł|PLN|грн|₴|UAH)\s*(\d+[.,]?\d*)""",
            RegexOption.IGNORE_CASE
        )

        // Regex for time: "45 min", "45 хв"
        private val TIME_REGEX = Regex(
            """(\d+)\s*(?:min|minut|хв)""",
            RegexOption.IGNORE_CASE
        )

        // Accept button texts PL/EN/UKR
        private val ACCEPT_TEXTS = listOf(
            "accept", "akceptuj", "прийняти"
        )
    }

    override fun canHandle(packageName: String): Boolean {
        return packageName == UBER_DRIVER_PACKAGE
    }

    override fun parse(rootNode: AccessibilityNodeInfo): Offer? {
        val texts = mutableListOf<String>()
        collectTexts(rootNode, texts)

        Log.d(TAG, "UberParser: collected ${texts.size} texts")
        texts.forEach { Log.d(TAG, "  text: '$it'") }

        var amount: Double? = null
        var minutes: Int? = null
        var hasAcceptButton = false

        for (text in texts) {
            val lower = text.lowercase(Locale.ROOT)

            if (!hasAcceptButton) {
                for (acceptText in ACCEPT_TEXTS) {
                    if (lower.contains(acceptText)) {
                        hasAcceptButton = true
                        Log.d(TAG, "UberParser: Accept button found: '$text'")
                        break
                    }
                }
            }

            if (amount == null) {
                val found = extractAmount(text)
                if (found != null) {
                    amount = found
                    Log.d(TAG, "UberParser: Amount found: $amount from '$text'")
                }
            }

            if (minutes == null) {
                val found = extractMinutes(text)
                if (found != null) {
                    minutes = found
                    Log.d(TAG, "UberParser: Minutes found: $minutes from '$text'")
                }
            }
        }

        Log.d(TAG, "UberParser result: amount=$amount minutes=$minutes hasAccept=$hasAcceptButton")

        if (amount == null || minutes == null || !hasAcceptButton) {
            Log.d(TAG, "UberParser: returning null — missing data")
            return null
        }

        if (amount <= 0 || minutes <= 0) {
            Log.d(TAG, "UberParser: returning null — invalid values")
            return null
        }

        return Offer(
            platform = Platform.UBER,
            amount = amount,
            estimatedMinutes = minutes
        )
    }

    private fun extractAmount(text: String): Double? {
        val match = AMOUNT_REGEX.find(text) ?: AMOUNT_REGEX_REVERSED.find(text) ?: return null
        val raw = match.groupValues[1].replace(",", ".")
        return raw.toDoubleOrNull()
    }

    private fun extractMinutes(text: String): Int? {
        val match = TIME_REGEX.find(text) ?: return null
        return match.groupValues[1].toIntOrNull()
    }

    private fun collectTexts(node: AccessibilityNodeInfo, texts: MutableList<String>) {
        node.text?.toString()?.let { texts.add(it) }
        node.contentDescription?.toString()?.let { texts.add(it) }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectTexts(child, texts)
        }
    }
}
