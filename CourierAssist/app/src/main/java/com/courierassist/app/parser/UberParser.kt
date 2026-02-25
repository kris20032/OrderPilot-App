package com.courierassist.app.parser

import android.view.accessibility.AccessibilityNodeInfo
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

class UberParser : OfferParser {

    companion object {
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

        var amount: Double? = null
        var minutes: Int? = null
        var hasAcceptButton = false

        for (text in texts) {
            val lower = text.lowercase()

            // Check for accept button
            if (!hasAcceptButton) {
                for (acceptText in ACCEPT_TEXTS) {
                    if (lower.contains(acceptText)) {
                        hasAcceptButton = true
                        break
                    }
                }
            }

            // Extract amount
            if (amount == null) {
                amount = extractAmount(text)
            }

            // Extract time
            if (minutes == null) {
                minutes = extractMinutes(text)
            }
        }

        // Only return Offer if we have all required data
        if (amount == null || minutes == null || !hasAcceptButton) {
            return null
        }

        if (amount <= 0 || minutes <= 0) {
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
