package com.courierassist.app.capture

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.Display
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.R)
class ScreenCaptureManager(private val service: AccessibilityService) {

    companion object {
        private const val TAG = "CourierAssist"
        private const val THROTTLE_MS = 1000L
    }

    private var lastCaptureTime = 0L

    fun capture(callback: (Bitmap?) -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastCaptureTime < THROTTLE_MS) {
            Log.d(TAG, "ScreenCapture throttled — skipping")
            callback(null)
            return
        }
        lastCaptureTime = now

        service.takeScreenshot(
            Display.DEFAULT_DISPLAY,
            service.mainExecutor,
            object : AccessibilityService.TakeScreenshotCallback {
                override fun onSuccess(result: AccessibilityService.ScreenshotResult) {
                    val hwBitmap = Bitmap.wrapHardwareBuffer(
                        result.hardwareBuffer, result.colorSpace
                    )
                    // ML Kit requires software-rendered bitmap (ARGB_8888)
                    val swBitmap = hwBitmap?.copy(Bitmap.Config.ARGB_8888, false)
                    result.hardwareBuffer.close()
                    hwBitmap?.recycle()
                    Log.d(TAG, "Screenshot captured: ${swBitmap?.width}x${swBitmap?.height}")
                    callback(swBitmap)
                }

                override fun onFailure(errorCode: Int) {
                    Log.w(TAG, "Screenshot failed, errorCode=$errorCode")
                    callback(null)
                }
            }
        )
    }
}
