package com.orderpilot.app.domain

data class Offer(
    val platform: Platform,
    val amount: Double,
    val estimatedMinutes: Int,       // 0 = brak czasu (Glovo)
    val distanceKm: Double? = null,  // łączny dystans (pickup + delivery) lub sam pickup
    val currency: String = "zł",
    val pickupDistanceKm: Double? = null, // dystans do restauracji (Glovo)
    val isPartial: Boolean = false,       // true = widoczny tylko 1 dystans, czeka na scroll
    val isCash: Boolean = false           // true = zlecenie gotówkowe (kurier odbiera pieniądze)
)
