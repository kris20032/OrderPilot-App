package com.orderpilot.app.di

import android.content.Context
import com.orderpilot.app.capture.PopupCropper
import com.orderpilot.app.capture.ScreenCaptureService
import com.orderpilot.app.engine.OfferAnalyzer
import com.orderpilot.app.engine.OfferFilter
import com.orderpilot.app.ocr.OcrEngine
import com.orderpilot.app.overlay.OverlayAutoHider
import com.orderpilot.app.overlay.OverlayManager
import com.orderpilot.app.overlay.SystemOverlayManager
import com.orderpilot.app.parser.ParserRegistry
import com.orderpilot.app.parser.BoltFoodOcrParser
import com.orderpilot.app.parser.GlovoOcrParser
import com.orderpilot.app.parser.UberOcrParser
import com.orderpilot.app.parser.WoltOcrParser
import com.orderpilot.app.pipeline.PipelineOrchestrator
import com.orderpilot.app.settings.DisclosureRepository
import com.orderpilot.app.settings.SettingsRepository
import com.orderpilot.app.settings.SharedPrefsSettingsRepository

object ServiceLocator {

    lateinit var settingsRepository: SettingsRepository
    lateinit var disclosureRepository: DisclosureRepository
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
        disclosureRepository = DisclosureRepository(context)
        offerAnalyzer = OfferAnalyzer()
        offerFilter = OfferFilter()
        parserRegistry = ParserRegistry(listOf(UberOcrParser(), WoltOcrParser(), GlovoOcrParser(), BoltFoodOcrParser()))
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