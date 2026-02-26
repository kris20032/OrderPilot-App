package com.courierassist.app.ocr

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrEngine {

    companion object {
        private const val TAG = "CourierAssist"
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun recognize(bitmap: Bitmap, callback: (List<String>) -> Unit) {
        Log.d(TAG, "OCR input bitmap: ${bitmap.width}x${bitmap.height} config=${bitmap.config}")
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val lines = visionText.textBlocks.flatMap { block ->
                    block.lines.map { it.text }
                }
                Log.d(TAG, "OCR raw text: '${visionText.text}'")
                Log.d(TAG, "OCR lines (${lines.size}): $lines")
                callback(lines)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "OCR failed: ${e.message}")
                callback(emptyList())
            }
    }

    fun close() {
        recognizer.close()
    }
}
