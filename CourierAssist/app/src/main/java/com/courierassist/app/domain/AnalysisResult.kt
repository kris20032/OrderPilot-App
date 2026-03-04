package com.courierassist.app.domain

data class AnalysisResult(
    val offer: Offer,
    val zlPerHour: Double,
    val zlPerKm: Double?,
    val level: ProfitLevel
)
