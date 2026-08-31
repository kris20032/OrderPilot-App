package com.orderpilot.app.pipeline

import com.orderpilot.app.capture.PopupCropper
import com.orderpilot.app.capture.ScreenCaptureService
import com.orderpilot.app.di.AppLog
import com.orderpilot.app.di.MonitoringController
import com.orderpilot.app.domain.AnalysisResult
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.engine.OfferAnalyzer
import com.orderpilot.app.engine.OfferFilter
import com.orderpilot.app.ocr.OcrEngine
import com.orderpilot.app.overlay.OverlayManager
import com.orderpilot.app.overlay.OverlayAutoHider
import com.orderpilot.app.parser.ParserRegistry
import com.orderpilot.app.settings.SettingsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap

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
    // CoroutineExceptionHandler: bez tego nieobsłużony wyjątek z capture/OCR/cropper
    // (np. OOM przy createBitmap, IllegalState na zamkniętym ImageReader, błąd ML Kit)
    // propaguje do domyślnego handlera wątku i ZABIJA cały proces — monitoring milknie
    // w środku zmiany. Tu logujemy i jedziemy dalej (następny event zrobi nowy screenshot).
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        AppLog.w(AppLog.TAG_PIPELINE, "Pipeline uncaught exception: ${e.message}")
    }
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + exceptionHandler)
    private val lastResults = ConcurrentHashMap<Platform, AnalysisResult>()
    private val lastResultTimes = ConcurrentHashMap<Platform, Long>()
    private val resultExpiryMs = 60_000L // reset po 60s bez zlecenia

    fun process(packageName: String) {
        scope.launch {
            withTimeoutOrNull(PIPELINE_TIMEOUT_MS) {
                processInternal(packageName)
            } ?: AppLog.w(AppLog.TAG_PIPELINE, "Pipeline timed out after ${PIPELINE_TIMEOUT_MS}ms")
        }
    }

    private suspend fun processInternal(packageName: String) {
        if (!MonitoringController.isActive()) return
        val t0 = System.currentTimeMillis()
        val capture = captureService() ?: run {
            AppLog.w(AppLog.TAG_PIPELINE, "ScreenCaptureService not ready")
            return
        }
        if (!capture.isReady()) {
            AppLog.w(AppLog.TAG_PIPELINE, "capture.isReady()=false, skipping")
            return
        }

        val screenshot = capture.capture(packageName) ?: run {
            AppLog.w(AppLog.TAG_PIPELINE, "Screenshot null")
            return
        }
        val tCapture = System.currentTimeMillis()
        AppLog.d(AppLog.TAG_PIPELINE, "Screenshot captured [${tCapture - t0}ms]")

        // crop() może rzucić (OOM/createBitmap) — wtedy zwolnij screenshot zanim wyjątek
        // poleci do exceptionHandler, inaczej po dodaniu handlera mielibyśmy wyciek ~8-10 MB
        // na ofertę → OOM. Gdy crop zwróci TEN SAM bitmap (invalid dims) — nie zwalniaj tu,
        // bo recykluje go finally OCR poniżej.
        val cropped = try {
            popupCropper.crop(screenshot)
        } catch (e: Throwable) {
            screenshot.recycle()
            throw e
        }
        if (cropped !== screenshot) screenshot.recycle()

        val ocrLines: List<String>
        try {
            ocrLines = ocrEngine.recognize(cropped)
        } finally {
            cropped.recycle()
        }
        val tOcr = System.currentTimeMillis()
        AppLog.d(AppLog.TAG_PIPELINE, "OCR done [${tOcr - tCapture}ms, total ${tOcr - t0}ms]")
        if (ocrLines.isEmpty()) {
            AppLog.w(AppLog.TAG_PIPELINE, "OCR returned no lines")
            return
        }

        val settings = settingsRepository.load()
        val parser = parserRegistry.getParser(packageName) ?: run {
            AppLog.w(AppLog.TAG_PIPELINE, "No parser for $packageName")
            return
        }
        val offer = parser.parse(ocrLines) ?: run {
            AppLog.w(AppLog.TAG_PIPELINE, "Parser returned null")
            return
        }

        if (!offerFilter.passes(offer, settings.filtersFor(offer.platform))) {
            AppLog.d(AppLog.TAG_PIPELINE, "Offer filtered out")
            return
        }

        // Cross-platform duplicate check
        if (OfferDuplicateChecker.isCrossPlatformDuplicate(offer, overlayManager.getActiveOffers())) return

        val result = offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
        val tTotal = System.currentTimeMillis()
        AppLog.d(AppLog.TAG_PIPELINE, "Analyzed: ${result.zlPerHour} zł/h → ${result.level} [total ${tTotal - t0}ms]")

        // Per-platform duplicate check
        if (isSameAsPrevious(offer.platform, result)) {
            AppLog.d(AppLog.TAG_PIPELINE, "Same result as before for ${offer.platform}, skipping overlay update")
            return
        }

        // Overlay guard — blokuje belkę jeśli user kliknął Stop w trakcie pipeline
        if (!MonitoringController.isActive()) return

        withContext(Dispatchers.Main) {
            overlayManager.show(result, settings.display, settings.language)
            // Czas wyświetlania belki per platforma (spójnie ze ścieżką accessibility);
            // wcześniej brano tylko czas globalny → override per platforma był gubiony.
            overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(result.offer.platform) * 1000L, result.offer.platform)
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
            result.offer.distanceKm == prev.offer.distanceKm &&
            result.offer.isCash == prev.offer.isCash) return true

        lastResults[platform] = result
        lastResultTimes[platform] = now
        return false
    }

    fun onOverlayHidden() { /* lastResults zachowane — resetują się po 60s bez zlecenia */ }

    fun cancel() = scope.cancel()

    companion object {
        private const val PIPELINE_TIMEOUT_MS = 10_000L // cały pipeline normalnie < 1s; 10s = safety net
    }
}
