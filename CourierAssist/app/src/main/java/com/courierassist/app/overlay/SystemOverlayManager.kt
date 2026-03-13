package com.courierassist.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.TouchDelegate
import android.view.View
import android.view.WindowManager
import com.courierassist.app.R
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.settings.DisplayConfig

class SystemOverlayManager(private val context: Context) : OverlayManager {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null

    override fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage) {
        hide()
        val view = OverlayViewFactory.create(context, result, displayConfig, language)

        // Obszar dotyku tylko na przycisku × — reszta belki pass-through
        val density = context.resources.displayMetrics.density
        val touchSize = (44 * density).toInt()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
            y = (48 * density).toInt()
        }

        // Powiększ obszar dotyku przycisku × do 44dp×44dp (po wyrenderowaniu)
        view.post {
            val closeBtn = view.findViewById<View>(R.id.tv_close) ?: return@post
            val delegate = Rect()
            closeBtn.getHitRect(delegate)
            val extra = (touchSize - delegate.width()) / 2
            delegate.inset(-extra, -extra)
            view.touchDelegate = TouchDelegate(delegate, closeBtn)
        }

        // Obsługa kliknięcia × — ukryj belkę
        view.findViewById<View>(R.id.tv_close)?.setOnClickListener { hide() }

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
}