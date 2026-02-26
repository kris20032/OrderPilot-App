package com.courierassist.app.service

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.courierassist.app.capture.PopupCropper
import com.courierassist.app.capture.ScreenCaptureManager
import com.courierassist.app.engine.OfferAnalyzer
import com.courierassist.app.ocr.OcrEngine
import com.courierassist.app.overlay.OverlayManager
import com.courierassist.app.overlay.SystemOverlayManager
import com.courierassist.app.parser.UberOcrParser

@RequiresApi(Build.VERSION_CODES.R)
class CourierAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "CourierAssist"
        private const val PREFS_NAME = "courierassist_prefs"
        const val KEY_ENABLED = "service_enabled"
        private const val DEBOUNCE_MS = 500L
        private const val UBER_PACKAGE = "com.ubercab.driver"
    }

    private lateinit var prefs: SharedPreferences
    private lateinit var screenCaptureManager: ScreenCaptureManager
    private lateinit var popupCropper: PopupCropper
    private lateinit var ocrEngine: OcrEngine
    private lateinit var uberOcrParser: UberOcrParser
    private lateinit var offerAnalyzer: OfferAnalyzer
    private lateinit var overlayManager: OverlayManager

    private val handler = Handler(Looper.getMainLooper())
    private var pendingRunnable: Runnable? = null
    private var isShowingOverlay = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        screenCaptureManager = ScreenCaptureManager(this)
        popupCropper = PopupCropper()
        ocrEngine = OcrEngine()
        uberOcrParser = UberOcrParser()
        offerAnalyzer = OfferAnalyzer()
        overlayManager = SystemOverlayManager(this)
        Log.d(TAG, "=== AccessibilityService connected — OCR pipeline ready ===")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        Log.d(TAG, "Event: pkg=$packageName type=${event.eventType} enabled=${isEnabled()}")

        if (!isEnabled()) return
        if (packageName != UBER_PACKAGE) return

        val eventType = event.eventType
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return

        // Debounce — screenshot + OCR takes ~200-300ms, so 500ms debounce is safe
        pendingRunnable?.let { handler.removeCallbacks(it) }
        val runnable = Runnable { triggerOcrPipeline() }
        pendingRunnable = runnable
        handler.postDelayed(runnable, DEBOUNCE_MS)
    }

    private fun triggerOcrPipeline() {
        Log.d(TAG, "--- triggerOcrPipeline ---")

        // Step 1: Screenshot
        screenCaptureManager.capture { fullBitmap ->
            if (fullBitmap == null) {
                Log.w(TAG, "Screenshot returned null — skipping")
                return@capture
            }

            // Step 2: Crop popup area
            val croppedBitmap = popupCropper.crop(fullBitmap)
            fullBitmap.recycle()

            // Step 3: OCR
            ocrEngine.recognize(croppedBitmap) { lines ->
                croppedBitmap.recycle()

                if (lines.isEmpty()) {
                    Log.w(TAG, "OCR returned no lines — skipping")
                    hideOverlayIfShowing()
                    return@recognize
                }

                // Step 4: Parse
                val offer = uberOcrParser.parse(lines)
                if (offer == null) {
                    Log.d(TAG, "No offer detected in OCR output")
                    hideOverlayIfShowing()
                    return@recognize
                }

                // Step 5: Analyze + show overlay (must run on main thread)
                handler.post {
                    Log.d(TAG, "Offer: amount=${offer.amount} min=${offer.estimatedMinutes} km=${offer.distanceKm}")
                    val result = offerAnalyzer.analyze(offer)
                    overlayManager.show(result, offer)
                    isShowingOverlay = true
                    Log.d(TAG, "Overlay shown: ${result.zlPerHour} zł/h → ${result.level}")
                }
            }
        }
    }

    private fun hideOverlayIfShowing() {
        if (isShowingOverlay) {
            handler.post {
                overlayManager.hide()
                isShowingOverlay = false
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "AccessibilityService interrupted")
        hideOverlayIfShowing()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideOverlayIfShowing()
        pendingRunnable?.let { handler.removeCallbacks(it) }
        ocrEngine.close()
    }

    private fun isEnabled(): Boolean {
        return prefs.getBoolean(KEY_ENABLED, false)
    }
}
