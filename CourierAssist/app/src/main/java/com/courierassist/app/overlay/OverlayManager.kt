package com.courierassist.app.overlay

import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.settings.DisplayConfig

interface OverlayManager {
    fun show(result: AnalysisResult, displayConfig: DisplayConfig)
    fun hide()
    fun isShowing(): Boolean
}