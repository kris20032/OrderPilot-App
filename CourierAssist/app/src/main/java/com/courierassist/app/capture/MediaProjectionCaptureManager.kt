package com.courierassist.app.capture

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.Log

class MediaProjectionCaptureManager(private val context: Context) {

    companion object {
        private const val TAG = "CourierAssist"

        // Static token — przekazywany z MainActivity do serwisu
        var resultCode: Int = 0
        var resultData: Intent? = null
        val isReady: Boolean get() = resultData != null
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var isSetup = false

    fun setup() {
        val data = resultData ?: run {
            Log.w(TAG, "MediaProjection: brak tokenu uprawnień")
            return
        }
        if (isSetup) return

        val manager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = manager.getMediaProjection(resultCode, data)

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "CourierAssistCapture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null, null
        )

        isSetup = true
        Log.d(TAG, "MediaProjection setup: ${width}x${height} density=$density")
    }

    fun capture(callback: (Bitmap?) -> Unit) {
        if (!isSetup) {
            setup()
        }
        if (!isSetup) {
            Log.w(TAG, "MediaProjection nie jest zainicjalizowany")
            callback(null)
            return
        }

        // Krótkie opóźnienie żeby VirtualDisplay zdążył narysować klatkę
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val image = imageReader?.acquireLatestImage()
            if (image == null) {
                Log.w(TAG, "ImageReader: brak klatki")
                callback(null)
                return@postDelayed
            }

            try {
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * image.width

                val bitmap = Bitmap.createBitmap(
                    image.width + rowPadding / pixelStride,
                    image.height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)

                // Przytnij do dokładnego rozmiaru ekranu (bez paddingu)
                val cropped = Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
                bitmap.recycle()

                Log.d(TAG, "Screenshot captured: ${cropped.width}x${cropped.height}")
                callback(cropped)
            } finally {
                image.close()
            }
        }, 200)
    }

    fun release() {
        virtualDisplay?.release()
        virtualDisplay = null
        mediaProjection?.stop()
        mediaProjection = null
        imageReader?.close()
        imageReader = null
        isSetup = false
        Log.d(TAG, "MediaProjection released")
    }
}
