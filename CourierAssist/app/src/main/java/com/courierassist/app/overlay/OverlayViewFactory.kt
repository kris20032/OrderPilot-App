package com.courierassist.app.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.courierassist.app.R
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.MetricType
import com.courierassist.app.domain.ProfitLevel
import com.courierassist.app.settings.DisplayConfig

object OverlayViewFactory {

    fun create(context: Context, result: AnalysisResult, config: DisplayConfig, language: AppLanguage): View {
        val view = LayoutInflater.from(context).inflate(R.layout.overlay_offer, null)
        val textView = view.findViewById<TextView>(R.id.tv_overlay_text)

        val l = labels(language)
        val parts = mutableListOf<String>()
        if (MetricType.ZL_PER_HOUR in config.visibleMetrics && result.zlPerHour != null)
            parts += "%.0f ${l.currencyPerHour}".format(result.zlPerHour)
        if (MetricType.ZL_PER_KM in config.visibleMetrics && result.zlPerKm != null)
            parts += "%.1f ${l.currencyPerKm}".format(result.zlPerKm)
        if (MetricType.AMOUNT in config.visibleMetrics)
            parts += "%.2f ${l.currency}".format(result.offer.amount)
        if (MetricType.TIME in config.visibleMetrics && result.offer.estimatedMinutes > 0)
            parts += "${result.offer.estimatedMinutes} ${l.minutes}"
        if (MetricType.DISTANCE in config.visibleMetrics && result.offer.distanceKm != null)
            parts += "%.1f ${l.km}".format(result.offer.distanceKm)

        // Glovo partial: brak pełnych danych — zachęć do scrollu
        if (result.offer.isPartial)
            parts += "↓"

        textView.text = parts.joinToString(" | ")
        textView.setTextColor(Color.WHITE)
        textView.setShadowLayer(3f, 1f, 1f, Color.BLACK)

        val alpha = (config.overlayOpacity / 100f * 255).toInt()
        val bgColor = when (result.level) {
            ProfitLevel.GREEN  -> Color.argb(alpha, 0x4C, 0xAF, 0x50)
            ProfitLevel.YELLOW -> Color.argb(alpha, 0xFF, 0x98, 0x00)
            ProfitLevel.RED    -> Color.argb(alpha, 0xF4, 0x43, 0x36)
        }
        val density = context.resources.displayMetrics.density
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(bgColor)
            setCornerRadius(14f * density)
        }
        view.background = bg
        return view
    }

    private data class Labels(
        val currencyPerHour: String,
        val currencyPerKm: String,
        val currency: String,
        val minutes: String,
        val km: String
    )

    private fun labels(language: AppLanguage) = when (language) {
        AppLanguage.PL -> Labels("zł/h", "zł/km", "zł", "min", "km")
        AppLanguage.UK -> Labels("грн/год", "грн/км", "грн", "хв", "км")
        AppLanguage.EN -> Labels("PLN/h", "PLN/km", "PLN", "min", "km")
    }
}
