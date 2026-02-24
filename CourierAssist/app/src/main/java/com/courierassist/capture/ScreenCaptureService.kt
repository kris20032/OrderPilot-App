package com.courierassist.capture

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.courierassist.R

class ScreenCaptureService : Service() {

    companion object {
        private const val TAG = "CourierAssist"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "screen_capture_channel"
        private const val CAPTURE_INTERVAL_MS = 500L
        private const val LOG_EVERY_N = 20

        const val ACTION_START = "com.courierassist.ACTION_START"
        const val ACTION_STOP = "com.courierassist.ACTION_STOP"
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"

        // shared state read by MainActivity
        @Volatile var screenshotCount: Long = 0
        @Volatile var lastTimestamp: Long = 0L
        @Volatile var isCapturing: Boolean = false
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private lateinit var handlerThread: HandlerThread
    private lateinit var captureHandler: Handler

    private val captureRunnable = object : Runnable {
        override fun run() {
            captureFrame()
            if (isCapturing) {
                captureHandler.postDelayed(this, CAPTURE_INTERVAL_MS)
            }
        }
    }

    private val mediaProjectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            Log.w(TAG, "MediaProjection stopped by system")
            isCapturing = false
            stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        handlerThread = HandlerThread("CaptureThread").also { it.start() }
        captureHandler = Handler(handlerThread.looper)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, createNotification())

                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1)
                val resultData: Intent? = intent.getParcelableExtra(EXTRA_RESULT_DATA)

                if (resultData == null) {
                    Log.e(TAG, "Missing MediaProjection result data — aborting")
                    stopSelf()
                    return START_NOT_STICKY
                }

                val projectionManager =
                    getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                mediaProjection =
                    projectionManager.getMediaProjection(resultCode, resultData).also {
                        it.registerCallback(mediaProjectionCallback, captureHandler)
                    }

                startCapture()
            }
            ACTION_STOP -> {
                Log.i(TAG, "Stop requested")
                stopCapture()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopCapture()
        handlerThread.quitSafely()
    }

    // ------------------------------------------------------------------ capture

    private fun startCapture() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection!!.createVirtualDisplay(
            "CourierAssistCapture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            captureHandler
        )

        screenshotCount = 0
        lastTimestamp = 0L
        isCapturing = true

        Log.i(TAG, "Capture started — ${width}x${height} @ ${density}dpi")
        captureHandler.postDelayed(captureRunnable, CAPTURE_INTERVAL_MS)
    }

    private fun captureFrame() {
        val reader = imageReader ?: return
        try {
            val image = reader.acquireLatestImage() ?: return
            screenshotCount++
            lastTimestamp = System.currentTimeMillis()
            image.close()   // release immediately — no file, no bitmap, no OCR

            if (screenshotCount % LOG_EVERY_N == 0L) {
                Log.d(TAG, "Screenshots captured: $screenshotCount  ts=$lastTimestamp")
            }
        } catch (e: Exception) {
            Log.e(TAG, "captureFrame error: ${e.message}", e)
        }
    }

    private fun stopCapture() {
        isCapturing = false
        captureHandler.removeCallbacks(captureRunnable)

        virtualDisplay?.release()
        virtualDisplay = null

        imageReader?.close()
        imageReader = null

        mediaProjection?.unregisterCallback(mediaProjectionCallback)
        mediaProjection?.stop()
        mediaProjection = null

        Log.i(TAG, "Capture stopped. Total screenshots: $screenshotCount")
    }

    // ------------------------------------------------------------------ notification

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Screen Capture",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "CourierAssist capture loop" }

        (getSystemService(NotificationManager::class.java))
            .createNotificationChannel(channel)
    }

    private fun createNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CourierAssist")
            .setContentText("Capture loop running…")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
}
