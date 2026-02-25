package com.courierassist.app.overlay

import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.Offer

interface OverlayManager {
    fun show(result: AnalysisResult, offer: Offer)
    fun hide()
}
