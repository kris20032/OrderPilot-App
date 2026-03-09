package com.courierassist.app.overlay

import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.settings.DisplayConfig

interface OverlayManager {
    fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage)
    fun hide()
    fun isShowing(): Boolean
}