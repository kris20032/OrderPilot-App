package com.orderpilot.app.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
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

        // Zlecenie gotówkowe — emoji na końcu (po metrykach)
        if (result.offer.isCash)
            parts += "\uD83D\uDCB5" // 💵

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

        // Przycisk zamknięcia — kółko w lekko ciemniejszym odcieniu belki
        val closeBtnInner = (view.findViewById<View>(R.id.tv_close) as? android.view.ViewGroup)
            ?.getChildAt(0)
        closeBtnInner?.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            // Przyciemniamy kolor belki o ~25% — subtelny kontrast, nie odcina się wizualnie
            setColor(darkenColor(bgColor, 0.75f))
        }

        // Drag handle — dwie kreski z lewej strony belki
        val dragHandle = view.findViewById<View>(R.id.drag_handle)
        dragHandle?.background = createDragHandleDrawable(density)

        return view
    }

    /** Tworzy drawable z dwoma cienkimi białymi liniami (drag indicator). */
    private fun createDragHandleDrawable(density: Float): LayerDrawable {
        val lineColor = Color.argb(153, 255, 255, 255) // biały 60% alpha
        val lineWidth = (12 * density).toInt()
        val lineHeight = (2 * density).toInt()
        val cornerRadius = 1f * density

        fun makeLine() = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(lineColor)
            setCornerRadius(cornerRadius)
            setSize(lineWidth, lineHeight)
        }

        val line1 = makeLine()
        val line2 = makeLine()

        return LayerDrawable(arrayOf(line1, line2)).apply {
            val handleWidth = (28 * density).toInt()
            val insetH = (handleWidth - lineWidth) / 2
            val gap = (3 * density).toInt() // offset od środka: -3dp i +3dp

            setLayerGravity(0, Gravity.CENTER)
            setLayerGravity(1, Gravity.CENTER)
            setLayerInset(0, insetH, 0, insetH, gap + lineHeight)
            setLayerInset(1, insetH, gap + lineHeight, insetH, 0)
        }
    }

    /** Przyciemnia kolor RGB zachowując alpha. factor=0.75 → 25% ciemniej. */
    private fun darkenColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.argb(a, r, g, b)
    }

    private data class Labels(
        val currencyPerHour: String,
        val currencyPerKm: String,
        val currency: String,
        val minutes: String,
        val km: String
    )

    /**
     * Jednostki na belce zależą od WALUTY ZLECENIA (= rynek), NIE od języka UI apki.
     * Język UI zmienia menu/ustawienia, ale belka musi być spójna z walutą:
     *   zł → h/km/min (łacińskie, zrozumiałe w PL)
     *   ₴  → год/км/хв (ukraińskie, rynek UA)
     *   ₽  → ч/км/мин (rosyjskie, rynek RU)
     * Fallback (nieznana waluta) → łacińskie jednostki.
     */
    private fun labels(language: AppLanguage, currency: String): Labels {
        val hourSuffix: String
        val kmUnit: String
        val minUnit: String

        when (currency) {
            "₴", "грн" -> {
                hourSuffix = "год"; kmUnit = "км"; minUnit = "хв"
            }
            "₽", "руб" -> {
                hourSuffix = "ч"; kmUnit = "км"; minUnit = "мін"
            }
            else -> {
                // zł, €, $, £ i każda inna — uniwersalne łacińskie jednostki
                hourSuffix = "h"; kmUnit = "km"; minUnit = "min"
            }
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
