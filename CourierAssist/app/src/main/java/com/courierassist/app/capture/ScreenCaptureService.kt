package com.courierassist.app.capture

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class ScreenCaptureService : Service() {

    companion object {
        private const val TAG = "CourierAssist"
        private const val CHANNEL_ID = "screen_capture_channel"
        private const val NOTIFICATION_ID = 1001
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"

        // Singleton dostęp dla AccessibilityService
        @Volatile
        var instance: ScreenCaptureService? = null

        fun startCapture(context: Context, resultCode: Int, resultData: Intent) {
            val intent = Intent(context, ScreenCaptureService::class.java).apply {
                putExtra(EXTRA_RESULT_CODE, resultCode)
                putExtra(EXTRA_RESULT_DATA, resultData)
            }
            context.startForegroundService(intent)
        }
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        Log.d(TAG, "ScreenCaptureService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, 0) ?: 0
        val resultData = intent?.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)

        if (resultCode != 0 && resultData != null) {
            setupMediaProjection(resultCode, resultData)
        } else {
            Log.w(TAG, "ScreenCaptureService: brak tokenu MediaProjection")
        }

        return START_NOT_STICKY
    }

    private fun setupMediaProjection(resultCode: Int, resultData: Intent) {
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = manager.getMediaProjection(resultCode, resultData) ?: run {
            Log.w(TAG, "getMediaProjection returned null")
            return
        }
        mediaProjection = projection

        // API 34+ wymaga rejestracji callbacku PRZED createVirtualDisplay
        projection.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Log.d(TAG, "MediaProjection stopped")
                virtualDisplay?.release()
                imageReader?.close()
                virtualDisplay = null
                imageReader = null
            }
        }, Handler(Looper.getMainLooper()))

        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = projection.createVirtualDisplay(
            "CourierAssistCapture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null, null
        )

        Log.d(TAG, "MediaProjection setup: ${width}x${height}")
    }

    fun capture(callback: (Bitmap?) -> Unit) {
        if (imageReader == null) {
            Log.w(TAG, "capture: imageReader is null")
            callback(null)
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
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

                val cropped = Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
                bitmap.recycle()

                Log.d(TAG, "Screenshot captured: ${cropped.width}x${cropped.height}")
                callback(cropped)
            } finally {
                image.close()
            }
        }, 200)
    }

    fun isReady(): Boolean = imageReader != null

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader?.close()
        Log.d(TAG, "ScreenCaptureService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Nagrywanie ekranu",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "CourierAssist analizuje oferty"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CourierAssist aktywny")
            .setContentText("Analizowanie ofert Uber Driver")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
