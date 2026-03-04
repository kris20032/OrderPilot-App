package com.courierassist.app.capture

import android.graphics.Bitmap
import com.courierassist.app.di.AppLog

class PopupCropper {

    companion object {
        // Uber's offer popup occupies roughly the bottom 60% of the screen.
        // The crop starts at 40% from the top, keeping everything below that.
        private const val CROP_START_RATIO = 0.40
    }

    fun crop(fullScreenBitmap: Bitmap): Bitmap {
        val startY = (fullScreenBitmap.height * CROP_START_RATIO).toInt()
        val height = fullScreenBitmap.height - startY
        val cropped = Bitmap.createBitmap(fullScreenBitmap, 0, startY, fullScreenBitmap.width, height)
        AppLog.d(AppLog.TAG_CAPTURE, "PopupCropper: cropped region y=$startY h=$height (from full ${fullScreenBitmap.width}x${fullScreenBitmap.height})")
        return cropped
    }
}
