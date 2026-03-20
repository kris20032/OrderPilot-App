package com.courierassist.app.ocr

import android.graphics.Bitmap
import com.courierassist.app.di.AppLog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class OcrEngine {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognize(bitmap: Bitmap): List<String> = suspendCancellableCoroutine { cont ->
        recognizer.process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener { result ->
                val lines = result.textBlocks.flatMap { it.lines }.map { it.text }
                AppLog.d(AppLog.TAG_OCR, "Recognized ${lines.size} lines")
                if (cont.isActive) cont.resume(lines)
            }
            .addOnFailureListener {
                AppLog.w(AppLog.TAG_OCR, "OCR failed: ${it.message}")
                if (cont.isActive) cont.resume(emptyList())
            }
    }

    fun close() = recognizer.close()
}
