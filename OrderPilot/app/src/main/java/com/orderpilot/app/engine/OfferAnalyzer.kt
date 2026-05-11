package com.orderpilot.app.engine

import com.orderpilot.app.domain.AnalysisResult
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.ProfitLevel

class OfferAnalyzer
{
    fun analyze(offer: Offer, thresholds: com.orderpilot.app.settings.ThresholdConfig): AnalysisResult {
        val zlPerKm = offer.distanceKm?.let { if (it > 0) offer.amount / it else null }

        // Glovo: brak czasu → liczymy wyłącznie po zł/km
        if (offer.estimatedMinutes <= 0) {
            val level = levelFromZlPerKm(zlPerKm, thresholds)
            return AnalysisResult(offer = offer, zlPerHour = null, zlPerKm = zlPerKm, level = level)
        }

        val zlPerHour = offer.amount / (offer.estimatedMinutes / 60.0)
        // Porównuj zaokrągloną wartość — żeby belka "34 zł/h" przy progu 34 była YELLOW, nie RED
        val zlPerHourRounded = Math.round(zlPerHour).toDouble()
        val levelFromHour = when {
            zlPerHourRounded >= thresholds.greenMinZlPerHour -> ProfitLevel.GREEN
            zlPerHourRounded >= thresholds.yellowMinZlPerHour -> ProfitLevel.YELLOW
            else -> ProfitLevel.RED
        }

        // Combined-thresholds (AND-semantics) — kolor finalny = gorszy z dwóch poziomów,
        // żeby oba progi musiały być spełnione. Bez dystansu — decyduje tylko zł/h.
        val level = if (zlPerKm != null) {
            worstOf(levelFromHour, levelFromZlPerKm(zlPerKm, thresholds))
        } else {
            levelFromHour
        }

        return AnalysisResult(offer = offer, zlPerHour = zlPerHour, zlPerKm = zlPerKm, level = level)
    }

    private fun levelFromZlPerKm(
        zlPerKm: Double?,
        thresholds: com.orderpilot.app.settings.ThresholdConfig
    ): ProfitLevel = when {
        zlPerKm == null -> ProfitLevel.RED
        zlPerKm >= thresholds.greenMinZlPerKm -> ProfitLevel.GREEN
        zlPerKm >= thresholds.yellowMinZlPerKm -> ProfitLevel.YELLOW
        else -> ProfitLevel.RED
    }

    // GREEN.ordinal=0, YELLOW=1, RED=2 — wyższy ordinal = gorzej
    private fun worstOf(a: ProfitLevel, b: ProfitLevel): ProfitLevel =
        if (a.ordinal >= b.ordinal) a else b
}
