package com.courierassist.app.pipeline

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform
import kotlin.math.abs

/**
 * Sprawdza cross-platform duplikaty ofert.
 * Używane przez CourierAccessibilityService i PipelineOrchestrator
 * żeby nie wyświetlać dwóch belek z tymi samymi danymi.
 */
object OfferDuplicateChecker {

    /**
     * Czy inna platforma już wyświetla belkę z podobnymi danymi?
     * Tolerancja: ±1 min czasu, ±0.5 km dystansu (lub ±0.5 zł kwoty gdy brak dystansu).
     */
    fun isCrossPlatformDuplicate(offer: Offer, activeOffers: Map<Platform, Offer>): Boolean {
        for ((otherPlatform, otherOffer) in activeOffers) {
            if (otherPlatform == offer.platform) continue

            val minutesClose = abs(offer.estimatedMinutes - otherOffer.estimatedMinutes) <= 1
            val distanceClose = if (offer.distanceKm != null && otherOffer.distanceKm != null) {
                abs(offer.distanceKm - otherOffer.distanceKm) <= 0.5
            } else {
                abs(offer.amount - otherOffer.amount) < 0.5
            }

            if (minutesClose && distanceClose) {
                AppLog.d(AppLog.TAG_PIPELINE, "Cross-platform duplicate: ${offer.platform} ≈ ${otherPlatform} (${offer.estimatedMinutes}min/${offer.distanceKm}km) — skipping")
                return true
            }
        }
        return false
    }
}
