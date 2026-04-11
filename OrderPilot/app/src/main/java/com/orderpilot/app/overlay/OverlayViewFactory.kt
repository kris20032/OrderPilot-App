package com.orderpilot.app.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.orderpilot.app.R
import com.orderpilot.app.domain.AnalysisResult
import com.orderpilot.app.domain.AppLanguage
import com.orderpilot.app.domain.MetricType
import com.orderpilot.app.domain.ProfitLevel
import com.orderpilot.app.settings.DisplayConfig

object OverlayViewFactory {

    fun create(context: Context, result: AnalysisResult, config: DisplayConfig, language: AppLanguage, platformLabel: String? = null): View {
        val view = LayoutInflater.from(context).inflate(R.layout.overlay_offer, null)
        val textView = view.findViewById<TextView>(R.id.tv_overlay_text)

        val currency = result.offer.currency.ifBlank { "zł" }
        val l = labels(language, currency)
        val nbsp = "\u00A0" // non-breaking space — trzyma wartość+jednostkę w jednej linii
        val parts = mutableListOf<String>()
        if (MetricType.ZL_PER_HOUR in config.visibleMetrics && result.zlPerHour != null)
            parts += "%.0f${nbsp}${l.currencyPerHour}".format(result.zlPerHour)
        if (MetricType.ZL_PER_KM in config.visibleMetrics && result.zlPerKm != null)
            parts += "%.1f${nbsp}${l.currencyPerKm}".format(result.zlPerKm)
        if (MetricType.AMOUNT in config.visibleMetrics)
            parts += "%.2f${nbsp}${l.currency}".format(result.offer.amount)
        if (MetricType.TIME in config.visibleMetrics && result.offer.estimatedMinutes > 0)
            parts += "${result.offer.estimatedMinutes}${nbsp}${l.minutes}"
        if (MetricType.DISTANCE in config.visibleMetrics && result.offer.distanceKm != null)
            parts += "%.1f${nbsp}${l.km}".format(result.offer.distanceKm)

        // Glovo partial: brak pełnych danych — zachęć do scrollu
        if (result.offer.isPartial)
            parts += "↓"

        // Etykieta platformy — widoczna tylko gdy 2 belki naraz
        if (platformLabel != null)
            parts.add(0, platformLabel)

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

    /**
     * Jednostki (czas/dystans) biorą z języka UI, waluta dynamicznie z Offer.currency (OCR).
     * Fallback "zł" gdy OCR nie wykrył waluty — nigdy nie hardcodujemy grn/UAH, bo apka
     * działa w PL i wszystkie platformy rozliczają się w PLN.
     */
    private fun labels(language: AppLanguage, currency: String): Labels {
        val hourSuffix = when (language) {
            AppLanguage.UK -> "год"
            AppLanguage.RU -> "ч"
            else -> "h"
        }
        val kmUnit = when (language) {
            AppLanguage.UK, AppLanguage.RU -> "км"
            else -> "km"
        }
        val minUnit = when (language) {
            AppLanguage.UK -> "хв"
            AppLanguage.RU -> "мин"
            else -> "min"
        }
        return Labels(
            currencyPerHour = "$currency/$hourSuffix",
            currencyPerKm = "$currency/$kmUnit",
            currency = currency,
            minutes = minUnit,
            km = kmUnit
        )
    }
}
