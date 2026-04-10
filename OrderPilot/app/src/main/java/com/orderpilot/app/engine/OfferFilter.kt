package com.orderpilot.app.engine

import com.orderpilot.app.domain.Offer
import com.orderpilot.app.settings.FilterConfig

class OfferFilter {
    fun passes(offer: Offer, filters: FilterConfig): Boolean {
        val dist = offer.distanceKm ?: return true
        if (filters.minDistanceKm != null && dist < filters.minDistanceKm) return false
        if (filters.maxDistanceKm != null && dist > filters.maxDistanceKm) return false
        return true
    }
}