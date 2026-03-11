package com.courierassist.app.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.settings.DisplayConfig

class SystemOverlayManager(private val context: Context) : OverlayManager {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private var keepAliveView: View? = null

    override fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage) {
        hide()
        val view = OverlayViewFactory.create(context, result, displayConfig, language)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
            y = (48 * context.resources.displayMetrics.density).toInt()
        }
        try {
            windowManager.addView(view, params)
            overlayView = view
        } catch (_: Exception) {}
    }

    override fun hide() {
        overlayView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            overlayView = null
        }
    }

    override fun isShowing() = overlayView != null

    // Stały mini overlay — utrzymuje proces przy życiu po screen off (jak RideHelper)
    // Cienki pasek 3dp po lewej stronie, pół-przezroczysty szary
    fun showKeepAlive() {
        if (keepAliveView != null) return
        val dp = context.resources.displayMetrics.density
        val view = View(context).apply {
            setBackgroundColor(Color.argb(180, 50, 50, 50))
        }
        val params = WindowManager.LayoutParams(
            (8 * dp).toInt(),
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.TOP
        }
        try {
            windowManager.addView(view, params)
            keepAliveView = view
        } catch (_: Exception) {}
    }

    fun hideKeepAlive() {
        keepAliveView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            keepAliveView = null
        }
    }
}