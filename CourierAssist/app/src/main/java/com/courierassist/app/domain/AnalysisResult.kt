package com.courierassist.app.domain

data class AnalysisResult(
    val offer: Offer,
    val zlPerHour: Double?,  // null dla Glovo (brak czasu — liczymy zł/km)
    val zlPerKm: Double?,
    val level: ProfitLevel
)
