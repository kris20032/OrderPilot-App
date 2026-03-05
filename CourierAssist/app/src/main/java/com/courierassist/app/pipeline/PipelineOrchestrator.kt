package com.courierassist.app.pipeline

import com.courierassist.app.capture.PopupCropper
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.di.AppLog
import com.courierassist.app.engine.OfferAnalyzer
import com.courierassist.app.engine.OfferFilter
import com.courierassist.app.ocr.OcrEngine
import com.courierassist.app.overlay.OverlayManager
import com.courierassist.app.overlay.OverlayAutoHider
import com.courierassist.app.parser.ParserRegistry
import com.courierassist.app.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PipelineOrchestrator(
    private val captureService: () -> ScreenCaptureService?,
    private val popupCropper: PopupCropper,
    private val ocrEngine: OcrEngine,
    private val parserRegistry: ParserRegistry,
    private val offerAnalyzer: OfferAnalyzer,
    private val offerFilter: OfferFilter,
    private val overlayManager: OverlayManager,
    private val overlayAutoHider: OverlayAutoHider,
    private val settingsRepository: SettingsRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun process(packageName: String) {
        if (overlayManager.isShowing()) {
            AppLog.d(AppLog.TAG_PIPELINE, "Overlay already visible, skipping")
            return
        }
        scope.launch {
            val capture = captureService() ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "ScreenCaptureService not ready")
                return@launch
            }
            if (!capture.isReady()) return@launch

            val screenshot = capture.capture() ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "Screenshot null")
                return@launch
            }
            AppLog.d(AppLog.TAG_PIPELINE, "Screenshot captured")

            val cropped = popupCropper.crop(screenshot)
            screenshot.recycle()

            val ocrLines = ocrEngine.recognize(cropped)
            cropped.recycle()
            if (ocrLines.isEmpty()) {
                AppLog.w(AppLog.TAG_PIPELINE, "OCR returned no lines")
                return@launch
            }

            val settings = settingsRepository.load()
            val parser = parserRegistry.getParser(packageName) ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "No parser for $packageName")
                return@launch
            }
            val offer = parser.parse(ocrLines, settings.language) ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "Parser returned null")
                return@launch
            }

            if (!offerFilter.passes(offer, settings.filtersFor(offer.platform))) {
                AppLog.d(AppLog.TAG_PIPELINE, "Offer filtered out")
                return@launch
            }

            val result = offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_PIPELINE, "Analyzed: ${result.zlPerHour} zł/h → ${result.level}")

            withContext(Dispatchers.Main) {
                overlayManager.show(result, settings.display)
                overlayAutoHider.onOverlayShown(scope)
            }
        }
    }

    fun cancel() = scope.cancel()
}