package com.courierassist.app.overlay

import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform
import com.courierassist.app.settings.DisplayConfig

interface OverlayManager {
    fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage)
    fun hide()
    fun hideByPlatform(platform: Platform)
    fun isShowing(): Boolean
    fun overlayCount(): Int
    fun getActiveOffers(): Map<Platform, Offer>
}