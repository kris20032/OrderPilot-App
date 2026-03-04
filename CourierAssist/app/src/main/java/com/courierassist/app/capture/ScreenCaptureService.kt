package com.courierassist.app.capture

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
import androidx.core.app.NotificationCompat
import com.courierassist.app.di.AppLog
import com.courierassist.app.di.CourierAssistApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ScreenCaptureService : Service() {

    companion object {
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"
        private const val NOTIFICATION_ID = 1001

        @Volatile
        var instance: ScreenCaptureService? = null

        fun startCapture(context: Context, resultCode: Int, data: Intent) {
            val intent = Intent(context, ScreenCaptureService::class.java).apply {
                putExtra(EXTRA_RESULT_CODE, resultCode)
                putExtra(EXTRA_RESULT_DATA, data)
            }
            context.startForegroundService(intent)
        }

        fun stopCapture(context: Context) {
            context.stopService(Intent(context, ScreenCaptureService::class.java))
        }
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        startForeground(NOTIFICATION_ID, buildNotification())
        AppLog.d(AppLog.TAG_CAPTURE, "ScreenCaptureService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, 0) ?: 0
        val resultData = intent?.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
        if (resultCode != 0 && resultData != null) {
            setupMediaProjection(resultCode, resultData)
        } else {
            AppLog.w(AppLog.TAG_CAPTURE, "Missing MediaProjection token")
        }
        return START_NOT_STICKY
    }

    private fun setupMediaProjection(resultCode: Int, resultData: Intent) {
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = manager.getMediaProjection(resultCode, resultData) ?: run {
            AppLog.w(AppLog.TAG_CAPTURE, "getMediaProjection returned null")
            return
        }
        mediaProjection = projection
        projection.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                AppLog.w(AppLog.TAG_CAPTURE, "MediaProjection stopped")
                virtualDisplay?.release()
                imageReader?.close()
                virtualDisplay = null
                imageReader = null
            }
        }, Handler(Looper.getMainLooper()))

        val metrics = resources.displayMetrics
        imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 2)
        virtualDisplay = projection.createVirtualDisplay(
            "CourierAssistCapture",
            metrics.widthPixels, metrics.heightPixels, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )
        AppLog.d(AppLog.TAG_CAPTURE, "MediaProjection setup: ${metrics.widthPixels}x${metrics.heightPixels}")
    }

    fun isReady(): Boolean = imageReader != null

    suspend fun capture(): Bitmap? = withContext(Dispatchers.IO) {
        delay(200)
        val image = imageReader?.acquireLatestImage() ?: run {
            AppLog.w(AppLog.TAG_CAPTURE, "No frame available")
            return@withContext null
        }
        try {
            val plane = image.planes[0]
            val rowPadding = plane.rowStride - plane.pixelStride * image.width
            val bitmap = Bitmap.createBitmap(
                image.width + rowPadding / plane.pixelStride,
                image.height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(plane.buffer)
            val cropped = Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
            bitmap.recycle()
            AppLog.d(AppLog.TAG_CAPTURE, "Screenshot captured: ${cropped.width}x${cropped.height}")
            cropped
        } finally {
            image.close()
        }
    }

    override fun onDestroy() {
        instance = null
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader?.close()
        AppLog.d(AppLog.TAG_CAPTURE, "ScreenCaptureService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification() = NotificationCompat.Builder(this, CourierAssistApp.CHANNEL_ID)
        .setContentTitle("CourierAssist aktywny")
        .setContentText("Analizowanie ofert")
        .setSmallIcon(android.R.drawable.ic_menu_compass)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
}
