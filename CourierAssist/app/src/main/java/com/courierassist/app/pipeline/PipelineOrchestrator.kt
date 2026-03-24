package com.courierassist.app.pipeline

import com.courierassist.app.capture.PopupCropper
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.Platform
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
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

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
    private val lastResults = ConcurrentHashMap<Platform, AnalysisResult>()
    private val lastResultTimes = ConcurrentHashMap<Platform, Long>()
    private val resultExpiryMs = 60_000L // reset po 60s bez zlecenia

    fun process(packageName: String) {
        scope.launch {
            val t0 = System.currentTimeMillis()
            val capture = captureService() ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "ScreenCaptureService not ready")
                return@launch
            }
            if (!capture.isReady()) {
                AppLog.w(AppLog.TAG_PIPELINE, "capture.isReady()=false, skipping")
                return@launch
            }

            val screenshot = capture.capture() ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "Screenshot null")
                return@launch
            }
            val tCapture = System.currentTimeMillis()
            AppLog.d(AppLog.TAG_PIPELINE, "Screenshot captured [${tCapture - t0}ms]")

            val cropped = popupCropper.crop(screenshot)
            screenshot.recycle()

            val ocrLines = ocrEngine.recognize(cropped)
            cropped.recycle()
            val tOcr = System.currentTimeMillis()
            AppLog.d(AppLog.TAG_PIPELINE, "OCR done [${tOcr - tCapture}ms, total ${tOcr - t0}ms]")
            if (ocrLines.isEmpty()) {
                AppLog.w(AppLog.TAG_PIPELINE, "OCR returned no lines")
                return@launch
            }

            val settings = settingsRepository.load()
            val parser = parserRegistry.getParser(packageName) ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "No parser for $packageName")
                return@launch
            }
            val offer = parser.parse(ocrLines) ?: run {
                AppLog.w(AppLog.TAG_PIPELINE, "Parser returned null")
                return@launch
            }

            if (!offerFilter.passes(offer, settings.filtersFor(offer.platform))) {
                AppLog.d(AppLog.TAG_PIPELINE, "Offer filtered out")
                return@launch
            }

            // Cross-platform duplicate check
            if (isCrossPlatformDuplicate(offer)) return@launch

            val result = offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            val tTotal = System.currentTimeMillis()
            AppLog.d(AppLog.TAG_PIPELINE, "Analyzed: ${result.zlPerHour} zł/h → ${result.level} [total ${tTotal - t0}ms]")

            // Per-platform duplicate check
            if (isSameAsPrevious(offer.platform, result)) {
                AppLog.d(AppLog.TAG_PIPELINE, "Same result as before for ${offer.platform}, skipping overlay update")
                return@launch
            }

            withContext(Dispatchers.Main) {
                overlayManager.show(result, settings.display, settings.language)
                overlayAutoHider.onOverlayShown(scope, settings.display.displayTimeSeconds * 1000L, result.offer.platform)
            }
        }
    }

    private fun isSameAsPrevious(platform: Platform, result: AnalysisResult): Boolean {
        val now = System.currentTimeMillis()
        val lastTime = lastResultTimes[platform] ?: 0L
        if (now - lastTime > resultExpiryMs) lastResults.remove(platform)

        val prev = lastResults[platform]
        if (prev != null &&
            result.level == prev.level &&
            result.offer.amount == prev.offer.amount &&
            result.offer.estimatedMinutes == prev.offer.estimatedMinutes &&
            result.offer.distanceKm == prev.offer.distanceKm) return true

        lastResults[platform] = result
        lastResultTimes[platform] = now
        return false
    }

    private fun isCrossPlatformDuplicate(offer: com.courierassist.app.domain.Offer): Boolean {
        val activeOffers = overlayManager.getActiveOffers()

        for ((otherPlatform, otherOffer) in activeOffers) {
            if (otherPlatform == offer.platform) continue
            val minutesClose = abs(offer.estimatedMinutes - otherOffer.estimatedMinutes) <= 1
            val distanceClose = if (offer.distanceKm != null && otherOffer.distanceKm != null) {
                abs(offer.distanceKm - otherOffer.distanceKm) <= 0.5
            } else {
                abs(offer.amount - otherOffer.amount) < 0.5
            }
            if (minutesClose && distanceClose) {
                AppLog.d(AppLog.TAG_PIPELINE, "Cross-platform duplicate: ${offer.platform} data matches ${otherPlatform} bar — skipping")
                return true
            }
        }
        return false
    }

    fun onOverlayHidden() { /* lastResults zachowane — resetują się po 60s bez zlecenia */ }

    fun cancel() = scope.cancel()
}
