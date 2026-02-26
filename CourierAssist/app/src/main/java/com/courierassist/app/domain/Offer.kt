package com.courierassist.app.domain

data class Offer(
    val platform: Platform,
    val amount: Double,
    val estimatedMinutes: Int,
    val distanceKm: Double? = null
)
