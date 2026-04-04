package com.orderpilot.app.capture

import android.graphics.Bitmap
import com.orderpilot.app.di.AppLog

class PopupCropper {

    companion object {
        // Popup kurierski zajmuje dolną część ekranu. Crop od 30% od góry (dolne 70%).
        // Margines bezpieczeństwa na różne aspect ratio (foldable, 21:9, tablety).
        // Wcześniej 40% — na Xiaomi popup zaczynał się od 36%, margines tylko 4%.
        private const val CROP_START_RATIO = 0.30
    }

    fun crop(fullScreenBitmap: Bitmap): Bitmap {
        val startY = (fullScreenBitmap.height * CROP_START_RATIO).toInt()
        val height = fullScreenBitmap.height - startY
        if (height <= 0 || startY >= fullScreenBitmap.height) {
            AppLog.w(AppLog.TAG_CAPTURE, "PopupCropper: invalid crop dimensions y=$startY h=$height — returning full bitmap")
            return fullScreenBitmap
        }
        val cropped = Bitmap.createBitmap(fullScreenBitmap, 0, startY, fullScreenBitmap.width, height)
        AppLog.d(AppLog.TAG_CAPTURE, "PopupCropper: cropped region y=$startY h=$height (from full ${fullScreenBitmap.width}x${fullScreenBitmap.height})")
        return cropped
    }
}
