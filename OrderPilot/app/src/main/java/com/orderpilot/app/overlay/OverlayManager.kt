package com.orderpilot.app.overlay

import com.orderpilot.app.domain.AnalysisResult
import com.orderpilot.app.domain.AppLanguage
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.settings.DisplayConfig

interface OverlayManager {
    fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage)
    fun hide()
    fun hideByPlatform(platform: Platform)
    fun isShowing(): Boolean
    fun overlayCount(): Int
    fun getActiveOffers(): Map<Platform, Offer>
}