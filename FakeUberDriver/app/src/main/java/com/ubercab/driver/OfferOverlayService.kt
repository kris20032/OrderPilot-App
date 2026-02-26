package com.ubercab.driver

import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.ubercab.driver.databinding.OverlayOfferBinding

class OfferOverlayService : Service() {

    companion object {
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_TIME = "extra_time"
        const val EXTRA_DISTANCE = "extra_distance"
        const val EXTRA_PICKUP = "extra_pickup"
        const val EXTRA_DROPOFF = "extra_dropoff"

        private const val POPUP_DURATION_MS = 15_000L
    }

    private var windowManager: WindowManager? = null
    private var overlayView: android.view.View? = null
    private var countdownAnimator: ValueAnimator? = null
    private val handler = Handler(Looper.getMainLooper())
    private var dismissRunnable: Runnable? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        dismissCurrentOverlay()
        showOverlay(intent)
        return START_NOT_STICKY
    }

    private fun showOverlay(intent: Intent?) {
        val offer = OfferData(
            amount = intent?.getStringExtra(EXTRA_AMOUNT) ?: "22,56 zł",
            time = intent?.getStringExtra(EXTRA_TIME) ?: "30 min",
            distance = intent?.getStringExtra(EXTRA_DISTANCE) ?: "6.4 km",
            pickup = intent?.getStringExtra(EXTRA_PICKUP) ?: "Carrefour (Przywidzka 6)",
            dropoff = intent?.getStringExtra(EXTRA_DROPOFF) ?: "Aleksandry Gabrysiak, Gdańsk"
        )

        val binding = OverlayOfferBinding.inflate(LayoutInflater.from(this))

        binding.tvAmount.text = offer.amount
        binding.tvTimeDistance.text = "Łącznie ${offer.time} (${offer.distance})"
        binding.tvPickup.text = offer.pickup
        binding.tvDropoff.text = offer.dropoff

        binding.btnClose.setOnClickListener { dismissCurrentOverlay() }
        binding.btnAccept.setOnClickListener { dismissCurrentOverlay() }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            x = 0
            y = 80
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager?.addView(binding.root, params)
        overlayView = binding.root

        startProgressCountdown(binding.progressBar)

        dismissRunnable = Runnable { dismissCurrentOverlay() }
        handler.postDelayed(dismissRunnable!!, POPUP_DURATION_MS)
    }

    private fun startProgressCountdown(progressBar: ProgressBar) {
        countdownAnimator = ValueAnimator.ofInt(1000, 0).apply {
            duration = POPUP_DURATION_MS
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                progressBar.progress = animator.animatedValue as Int
            }
            start()
        }
    }

    private fun dismissCurrentOverlay() {
        countdownAnimator?.cancel()
        countdownAnimator = null

        dismissRunnable?.let { handler.removeCallbacks(it) }
        dismissRunnable = null

        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                // View already removed
            }
        }
        overlayView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissCurrentOverlay()
    }
}
