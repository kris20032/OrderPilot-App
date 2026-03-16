package com.courierassist.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.TouchDelegate
import android.view.View
import android.view.WindowManager
import com.courierassist.app.R
import com.courierassist.app.domain.AnalysisResult
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.Platform
import com.courierassist.app.settings.DisplayConfig

class SystemOverlayManager(private val context: Context) : OverlayManager {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val density = context.resources.displayMetrics.density
    private val topY = (48 * density).toInt()
    private val slotHeight = (60 * density).toInt() // estymacja: 14dp padding * 2 + 18sp tekst + margin
    private val slotGap = (4 * density).toInt()

    private data class OverlaySlot(
        val view: View,
        val platform: Platform,
        val result: AnalysisResult,
        val displayConfig: DisplayConfig,
        val language: AppLanguage,
        val createdAt: Long
    )

    private val slots = mutableListOf<OverlaySlot>()

    override fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage) {
        val platform = result.offer.platform
        val existingIndex = slots.indexOfFirst { it.platform == platform }

        if (existingIndex >= 0) {
            // Ta sama platforma — zastąp belkę
            removeSlot(existingIndex)
            addSlot(result, displayConfig, language, platform, position = 0)
        } else if (slots.size >= 2) {
            // Już 2 belki — usuń najstarszą (ostatnia na liście = dolna = starsza)
            removeSlot(slots.size - 1)
            addSlot(result, displayConfig, language, platform, position = 0)
        } else if (slots.size == 1) {
            // Jedna belka innej platformy — dodaj drugą, obu dodaj etykietę
            addSlot(result, displayConfig, language, platform, position = 0)
            // Przebuduj starą belkę z etykietą
            rebuildSlotView(1)
            // Przebuduj nową belkę z etykietą (bo teraz mamy 2)
            rebuildSlotView(0)
        } else {
            // Brak belek — dodaj normalnie (bez etykiety)
            addSlot(result, displayConfig, language, platform, position = 0)
        }

        repositionAll()
    }

    override fun hide() {
        while (slots.isNotEmpty()) {
            removeSlot(0)
        }
    }

    override fun hideByPlatform(platform: Platform) {
        val index = slots.indexOfFirst { it.platform == platform }
        if (index < 0) return
        removeSlot(index)

        // Jeśli została 1 belka — przebuduj bez etykiety i przesuń na górę
        if (slots.size == 1) {
            rebuildSlotView(0)
        }
        repositionAll()
    }

    override fun isShowing() = slots.isNotEmpty()

    override fun overlayCount() = slots.size

    private fun addSlot(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage, platform: Platform, position: Int) {
        val showLabel = slots.isNotEmpty() // etykieta jeśli to będzie 2. belka (lub zastąpienie przy 2 belkach)
        val label = if (showLabel) platformLabel(platform) else null
        val view = OverlayViewFactory.create(context, result, displayConfig, language, label)

        val params = createParams(position)
        setupCloseButton(view, platform)

        try {
            windowManager.addView(view, params)
            slots.add(position, OverlaySlot(view, platform, result, displayConfig, language, System.currentTimeMillis()))
        } catch (_: Exception) {}
    }

    private fun removeSlot(index: Int) {
        if (index < 0 || index >= slots.size) return
        val slot = slots.removeAt(index)
        try { windowManager.removeView(slot.view) } catch (_: Exception) {}
    }

    private fun rebuildSlotView(index: Int) {
        if (index < 0 || index >= slots.size) return
        val old = slots[index]
        try { windowManager.removeView(old.view) } catch (_: Exception) {}

        val label = if (slots.size > 1) platformLabel(old.platform) else null
        val newView = OverlayViewFactory.create(context, old.result, old.displayConfig, old.language, label)
        val params = createParams(index)
        setupCloseButton(newView, old.platform)

        try {
            windowManager.addView(newView, params)
            slots[index] = old.copy(view = newView)
        } catch (_: Exception) {}
    }

    private fun repositionAll() {
        for (i in slots.indices) {
            try {
                val params = createParams(i)
                windowManager.updateViewLayout(slots[i].view, params)
            } catch (_: Exception) {}
        }
    }

    private fun createParams(slotIndex: Int): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
            y = topY + slotIndex * (slotHeight + slotGap)
        }
    }

    private fun setupCloseButton(view: View, platform: Platform) {
        val touchSize = (44 * density).toInt()
        view.post {
            val closeBtn = view.findViewById<View>(R.id.tv_close) ?: return@post
            val delegate = Rect()
            closeBtn.getHitRect(delegate)
            val extra = (touchSize - delegate.width()) / 2
            delegate.inset(-extra, -extra)
            view.touchDelegate = TouchDelegate(delegate, closeBtn)
        }
        view.findViewById<View>(R.id.tv_close)?.setOnClickListener { hideByPlatform(platform) }
    }

    private fun platformLabel(platform: Platform): String = when (platform) {
        Platform.UBER -> "UBER"
        Platform.WOLT -> "WOLT"
        Platform.GLOVO -> "GLOVO"
        Platform.BOLT -> "BOLT"
    }
}
