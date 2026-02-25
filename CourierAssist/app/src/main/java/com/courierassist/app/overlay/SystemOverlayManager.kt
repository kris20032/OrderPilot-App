package com.courierassist.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.courierassist.app.R
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.ProfitLevel

class SystemOverlayManager(private val context: Context) : OverlayManager {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP
    }

    override fun show(result: AnalysisResult, offer: Offer) {
        hide()

        val view = LayoutInflater.from(context).inflate(R.layout.overlay_offer, null)

        val bgColor = when (result.level) {
            ProfitLevel.GREEN -> 0xCC4CAF50.toInt()
            ProfitLevel.YELLOW -> 0xCCFF9800.toInt()
            ProfitLevel.RED -> 0xCCF44336.toInt()
        }
        view.setBackgroundColor(bgColor)

        val label = when (result.level) {
            ProfitLevel.GREEN -> "★"
            ProfitLevel.YELLOW -> "~"
            ProfitLevel.RED -> "✗"
        }
        val zlPerHourFormatted = String.format("%.0f", result.zlPerHour)
        val amountFormatted = String.format("%.2f", offer.amount)

        view.findViewById<TextView>(R.id.tv_overlay_text).text =
            "$label ${zlPerHourFormatted} zł/h | ${amountFormatted} zł | ${offer.estimatedMinutes} min"

        try {
            windowManager.addView(view, layoutParams)
            overlayView = view
        } catch (e: Exception) {
            // Permission not granted or other error
        }
    }

    override fun hide() {
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                // View already removed
            }
            overlayView = null
        }
    }
}
