package com.orderpilot.app.engine

import com.orderpilot.app.domain.AnalysisResult
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.ProfitLevel

class OfferAnalyzer
{
    fun analyze(offer: Offer, thresholds: com.orderpilot.app.settings.ThresholdConfig): AnalysisResult {
        // Glovo: brak czasu → liczymy po zł/km
        if (offer.estimatedMinutes <= 0) {
            val zlPerKm = offer.distanceKm?.let { if (it > 0) offer.amount / it else null }
            val level = when {
                zlPerKm == null -> ProfitLevel.RED
                zlPerKm >= thresholds.greenMinZlPerKm -> ProfitLevel.GREEN
                zlPerKm >= thresholds.yellowMinZlPerKm -> ProfitLevel.YELLOW
                else -> ProfitLevel.RED
            }
            return AnalysisResult(offer = offer, zlPerHour = null, zlPerKm = zlPerKm, level = level)
        }

        val zlPerHour = offer.amount / (offer.estimatedMinutes / 60.0)
        val zlPerKm = offer.distanceKm?.let { if (it > 0) offer.amount / it else null }
        // Porównuj zaokrągloną wartość — żeby belka "34 zł/h" przy progu 34 była YELLOW, nie RED
        val zlPerHourRounded = Math.round(zlPerHour).toDouble()
        val level = when {
            zlPerHourRounded >= thresholds.greenMinZlPerHour -> ProfitLevel.GREEN
            zlPerHourRounded >= thresholds.yellowMinZlPerHour -> ProfitLevel.YELLOW
            else -> ProfitLevel.RED
        }
        return AnalysisResult(offer = offer, zlPerHour = zlPerHour, zlPerKm = zlPerKm, level = level)
    }
}
