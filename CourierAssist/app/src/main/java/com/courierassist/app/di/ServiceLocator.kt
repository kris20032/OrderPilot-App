package com.courierassist.app.di

import android.content.Context
import com.courierassist.app.capture.PopupCropper
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.engine.OfferAnalyzer
import com.courierassist.app.engine.OfferFilter
import com.courierassist.app.ocr.OcrEngine
import com.courierassist.app.overlay.OverlayAutoHider
import com.courierassist.app.overlay.OverlayManager
import com.courierassist.app.overlay.SystemOverlayManager
import com.courierassist.app.parser.ParserRegistry
import com.courierassist.app.parser.UberOcrParser
import com.courierassist.app.parser.WoltOcrParser
import com.courierassist.app.pipeline.PipelineOrchestrator
import com.courierassist.app.settings.SettingsRepository
import com.courierassist.app.settings.SharedPrefsSettingsRepository

object ServiceLocator {

    lateinit var settingsRepository: SettingsRepository
    lateinit var offerAnalyzer: OfferAnalyzer
    lateinit var offerFilter: OfferFilter
    lateinit var parserRegistry: ParserRegistry
    lateinit var popupCropper: PopupCropper
    lateinit var ocrEngine: OcrEngine
    lateinit var overlayManager: OverlayManager
    lateinit var overlayAutoHider: OverlayAutoHider
    lateinit var pipelineOrchestrator: PipelineOrchestrator

    fun init(context: Context) {
        settingsRepository = SharedPrefsSettingsRepository(context)
        offerAnalyzer = OfferAnalyzer()
        offerFilter = OfferFilter()
        parserRegistry = ParserRegistry(listOf(UberOcrParser(), WoltOcrParser()))
        popupCropper = PopupCropper()
        ocrEngine = OcrEngine()
        overlayManager = SystemOverlayManager(context)
        overlayAutoHider = OverlayAutoHider(overlayManager, onHidden = { pipelineOrchestrator.onOverlayHidden() })
        pipelineOrchestrator = PipelineOrchestrator(
            captureService = { ScreenCaptureService.instance },
            popupCropper = popupCropper,
            ocrEngine = ocrEngine,
            parserRegistry = parserRegistry,
            offerAnalyzer = offerAnalyzer,
            offerFilter = offerFilter,
            overlayManager = overlayManager,
            overlayAutoHider = overlayAutoHider,
            settingsRepository = settingsRepository
        )
    }
}