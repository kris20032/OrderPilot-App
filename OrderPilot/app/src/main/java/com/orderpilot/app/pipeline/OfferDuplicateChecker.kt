package com.orderpilot.app.pipeline

import com.orderpilot.app.di.AppLog
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import kotlin.math.abs

/**
 * Sprawdza cross-platform duplikaty ofert.
 * Używane przez OrderPilotAccessibilityService i PipelineOrchestrator
 * żeby nie wyświetlać dwóch belek z tymi samymi danymi.
 */
object OfferDuplicateChecker {

    /**
     * Czy inna platforma już wyświetla belkę z podobnymi danymi?
     * Duplikat = 2 z 3 wymiarów się zgadzają:
     * - kwota ±0.5 zł
     * - czas ±5 min (parsery OCR mogą wyciągnąć różne wartości z tego samego tekstu)
     * - dystans ±0.5 km
     */
    fun isCrossPlatformDuplicate(offer: Offer, activeOffers: Map<Platform, Offer>): Boolean {
        for ((otherPlatform, otherOffer) in activeOffers) {
            if (otherPlatform == offer.platform) continue

            // M9: gdy któraś strona nie ma czasu (Glovo daje min=0), wymiar czasu jest
            // NIEZNANY, a nie "różny" — decydują wtedy tylko kwota+dystans, więc zawężamy
            // ich okna (0.3 zamiast 0.5), by przypadkowo zbliżona oferta nie tłumiła
            // realnie innego zlecenia.
            val timeKnown = offer.estimatedMinutes > 0 && otherOffer.estimatedMinutes > 0
            val tolerance = if (timeKnown) 0.5 else 0.3

            val amountClose = abs(offer.amount - otherOffer.amount) < tolerance
            val minutesClose = timeKnown &&
                abs(offer.estimatedMinutes - otherOffer.estimatedMinutes) <= 5
            val distanceClose = if (offer.distanceKm != null && otherOffer.distanceKm != null) {
                abs(offer.distanceKm - otherOffer.distanceKm) <= tolerance
            } else {
                false
            }

            // 2 z 3 wymiarów muszą się zgadzać
            val matchCount = listOf(amountClose, minutesClose, distanceClose).count { it }
            if (matchCount >= 2) {
                AppLog.d(AppLog.TAG_PIPELINE, "Cross-platform duplicate: ${offer.platform} ≈ ${otherPlatform} (amount=${offer.amount}/${otherOffer.amount}, min=${offer.estimatedMinutes}/${otherOffer.estimatedMinutes}, km=${offer.distanceKm}/${otherOffer.distanceKm}) — skipping")
                return true
            }
        }
        return false
    }
}
