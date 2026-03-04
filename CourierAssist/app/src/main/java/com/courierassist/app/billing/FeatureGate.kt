package com.courierassist.app.billing

import com.courierassist.app.domain.Platform

object FeatureGate {
    fun isPro(): Boolean = true
    fun canUsePlatform(platform: Platform): Boolean = when (platform) {
        Platform.UBER -> true
        else -> isPro()
    }
}