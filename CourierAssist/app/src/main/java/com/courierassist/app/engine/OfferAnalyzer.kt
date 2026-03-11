package com.courierassist.app.engine

import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.ProfitLevel

class OfferAnalyzer
{
    fun analyze(offer: Offer, thresholds: com.courierassist.app.settings.ThresholdConfig): AnalysisResult {
        if (offer.estimatedMinutes <= 0) {
            return AnalysisResult(offer = offer, zlPerHour = 0.0, zlPerKm = null, level = ProfitLevel.RED)
        }
        val zlPerHour = offer.amount / (offer.estimatedMinutes / 60.0)
        val zlPerKm = offer.distanceKm?.let { offer.amount / it }
        val level = when {
            zlPerHour >= thresholds.greenMinZlPerHour -> ProfitLevel.GREEN
            zlPerHour >= thresholds.yellowMinZlPerHour -> ProfitLevel.YELLOW
            else -> ProfitLevel.RED
        }
        return AnalysisResult(offer = offer, zlPerHour = zlPerHour, zlPerKm = zlPerKm, level = level)
    }
}
