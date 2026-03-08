package com.courierassist.app.overlay

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.courierassist.app.R
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.MetricType
import com.courierassist.app.domain.ProfitLevel
import com.courierassist.app.settings.DisplayConfig

object OverlayViewFactory {

    fun create(context: Context, result: AnalysisResult, config: DisplayConfig): View {
        val view = LayoutInflater.from(context).inflate(R.layout.overlay_offer, null)
        val textView = view.findViewById<TextView>(R.id.tv_overlay_text)

        val parts = mutableListOf<String>()
        if (MetricType.ZL_PER_HOUR in config.visibleMetrics)
            parts += "%.0f zł/h".format(result.zlPerHour)
        if (MetricType.ZL_PER_KM in config.visibleMetrics && result.zlPerKm != null)
            parts += "%.1f zł/km".format(result.zlPerKm)
        if (MetricType.AMOUNT in config.visibleMetrics)
            parts += "%.2f ${result.offer.currency}".format(result.offer.amount)
        if (MetricType.TIME in config.visibleMetrics)
            parts += "${result.offer.estimatedMinutes} min"
        if (MetricType.DISTANCE in config.visibleMetrics && result.offer.distanceKm != null)
            parts += "%.1f km".format(result.offer.distanceKm)

        textView.text = parts.joinToString(" | ")
        textView.setTextColor(Color.WHITE)
        textView.setShadowLayer(3f, 1f, 1f, Color.BLACK)

        val alpha = (config.overlayOpacity / 100f * 255).toInt()
        val bgColor = when (result.level) {
            ProfitLevel.GREEN  -> Color.argb(alpha, 0x4C, 0xAF, 0x50)
            ProfitLevel.YELLOW -> Color.argb(alpha, 0xFF, 0x98, 0x00)
            ProfitLevel.RED    -> Color.argb(alpha, 0xF4, 0x43, 0x36)
        }
        view.setBackgroundColor(bgColor)
        return view
    }
}