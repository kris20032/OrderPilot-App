package com.courierassist.app.engine

import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.ProfitLevel

class OfferAnalyzer(
    private val greenMin: Double = 40.0,
    private val yellowMin: Double = 32.0
) {
    fun analyze(offer: Offer): AnalysisResult {
        if (offer.estimatedMinutes <= 0) {
            return AnalysisResult(zlPerHour = 0.0, level = ProfitLevel.RED)
        }
        val hours = offer.estimatedMinutes / 60.0
        val zlPerHour = offer.amount / hours
        val level = when {
            zlPerHour >= greenMin -> ProfitLevel.GREEN
            zlPerHour >= yellowMin -> ProfitLevel.YELLOW
            else -> ProfitLevel.RED
        }
        return AnalysisResult(zlPerHour = zlPerHour, level = level)
    }
}
