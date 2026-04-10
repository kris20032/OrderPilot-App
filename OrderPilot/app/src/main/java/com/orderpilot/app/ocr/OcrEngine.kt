package com.orderpilot.app.ocr

import android.graphics.Bitmap
import com.orderpilot.app.di.AppLog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class OcrEngine {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Rozpoznaje tekst z bitmapy. UWAGA: caller odpowiada za recycle() bitmapy po powrocie.
     * InputImage.fromBitmap() nie kopiuje bitmapy — tylko ją opakowuje.
     */
    suspend fun recognize(bitmap: Bitmap): List<String> {
        if (bitmap.isRecycled) {
            AppLog.w(AppLog.TAG_OCR, "Bitmap already recycled, skipping OCR")
            return emptyList()
        }
        return withTimeoutOrNull(OCR_TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                recognizer.process(InputImage.fromBitmap(bitmap, 0))
                    .addOnSuccessListener { result ->
                        val lines = result.textBlocks.flatMap { it.lines }.map { it.text }
                        AppLog.d(AppLog.TAG_OCR, "Recognized ${lines.size} lines")
                        lines.forEachIndexed { i, line ->
                            AppLog.d(AppLog.TAG_OCR, "  OCR[$i]: $line")
                        }
                        if (cont.isActive) cont.resume(lines)
                    }
                    .addOnFailureListener { e ->
                        AppLog.w(AppLog.TAG_OCR, "OCR failed: ${e.message}")
                        if (cont.isActive) cont.resume(emptyList())
                    }
            }
        } ?: run {
            AppLog.w(AppLog.TAG_OCR, "OCR timed out after ${OCR_TIMEOUT_MS}ms")
            emptyList()
        }
    }

    fun close() = recognizer.close()

    companion object {
        private const val OCR_TIMEOUT_MS = 5000L // ML Kit normalnie < 500ms; 5s = safety net
    }
}
