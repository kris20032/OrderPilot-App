package com.orderpilot.app.billing

import com.orderpilot.app.domain.Platform

object FeatureGate {
    fun isPro(): Boolean = true
    fun canUsePlatform(platform: Platform): Boolean = when (platform) {
        Platform.UBER -> true
        else -> isPro()
    }
}