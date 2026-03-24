package com.courierassist.app.capture

import android.app.PendingIntent
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
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.courierassist.app.di.AppLog
import com.courierassist.app.di.CourierAssistApp
import com.courierassist.app.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ScreenCaptureService : Service() {

    companion object {
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"
        private const val NOTIFICATION_ID = 1001
        private const val WAKELOCK_TAG = "CourierAssist::ScreenCapture"

        @Volatile
        var instance: ScreenCaptureService? = null

        @Volatile
        var isProjectionLost = false
            private set

        fun startCapture(context: Context, resultCode: Int, data: Intent) {
            isProjectionLost = false
            val intent = Intent(context, ScreenCaptureService::class.java).apply {
                putExtra(EXTRA_RESULT_CODE, resultCode)
                putExtra(EXTRA_RESULT_DATA, data)
            }
            context.startForegroundService(intent)
        }

        fun stopCapture(context: Context) {
            isProjectionLost = false
            context.stopService(Intent(context, ScreenCaptureService::class.java))
        }
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        acquireWakeLock()
        startForeground(NOTIFICATION_ID, buildActiveNotification())
        AppLog.d(AppLog.TAG_CAPTURE, "ScreenCaptureService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mediaProjection != null) {
            AppLog.w(AppLog.TAG_CAPTURE, "MediaProjection already set up, ignoring duplicate start")
            return START_NOT_STICKY
        }
        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, 0) ?: 0
        val resultData = intent?.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
        if (resultCode != 0 && resultData != null) {
            setupMediaProjection(resultCode, resultData)
        } else {
            AppLog.w(AppLog.TAG_CAPTURE, "Missing MediaProjection token")
        }
        return START_REDELIVER_INTENT
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
                AppLog.w(AppLog.TAG_CAPTURE, "MediaProjection stopped by system")
                isProjectionLost = true
                virtualDisplay?.release()
                imageReader?.close()
                virtualDisplay = null
                imageReader = null
                showLostNotification()
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
        isProjectionLost = false
        AppLog.d(AppLog.TAG_CAPTURE, "MediaProjection setup: ${metrics.widthPixels}x${metrics.heightPixels}")
    }

    fun isReady(): Boolean = imageReader != null && !isProjectionLost

    suspend fun capture(): Bitmap? = withContext(Dispatchers.IO) {
        if (isProjectionLost) {
            AppLog.w(AppLog.TAG_CAPTURE, "Projection lost, cannot capture")
            return@withContext null
        }
        delay(100)
        val image = imageReader?.acquireLatestImage() ?: run {
            AppLog.w(AppLog.TAG_CAPTURE, "No frame available")
            return@withContext null
        }
        try {
            val plane = image.planes[0]
            val rowPadding = plane.rowStride - plane.pixelStride * image.width

            if (rowPadding == 0) {
                // Brak paddingu (typowy przypadek) — jedna bitmapa zamiast dwóch
                val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(plane.buffer)
                AppLog.d(AppLog.TAG_CAPTURE, "Screenshot captured: ${bitmap.width}x${bitmap.height}")
                bitmap
            } else {
                // Jest padding — potrzebna pośrednia bitmapa z przycięciem
                val padded = Bitmap.createBitmap(
                    image.width + rowPadding / plane.pixelStride,
                    image.height,
                    Bitmap.Config.ARGB_8888
                )
                padded.copyPixelsFromBuffer(plane.buffer)
                val cropped = Bitmap.createBitmap(padded, 0, 0, image.width, image.height)
                padded.recycle()
                AppLog.d(AppLog.TAG_CAPTURE, "Screenshot captured (padded): ${cropped.width}x${cropped.height}")
                cropped
            }
        } finally {
            image.close()
        }
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG).apply {
            acquire()
        }
        AppLog.d(AppLog.TAG_CAPTURE, "WakeLock acquired")
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
            wakeLock = null
        }
    }

    private fun buildActiveNotification() = NotificationCompat.Builder(this, CourierAssistApp.CHANNEL_ID)
        .setContentTitle("CourierAssist aktywny")
        .setContentText("Analizowanie ofert")
        .setSmallIcon(android.R.drawable.ic_menu_compass)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            }
        }
        .build()

    private fun showLostNotification() {
        val reopenIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, reopenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CourierAssistApp.CHANNEL_ID)
            .setContentTitle("CourierAssist — wznów nagrywanie")
            .setContentText("Kliknij żeby ponownie uruchomić analizę ofert")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            startForeground(NOTIFICATION_ID, notification)
        } catch (_: Exception) {
            AppLog.w(AppLog.TAG_CAPTURE, "Failed to update notification")
        }
    }

    override fun onDestroy() {
        instance = null
        isProjectionLost = false
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader?.close()
        releaseWakeLock()
        AppLog.d(AppLog.TAG_CAPTURE, "ScreenCaptureService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
