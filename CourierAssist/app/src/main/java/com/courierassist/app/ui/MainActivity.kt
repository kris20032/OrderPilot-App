package com.courierassist.app.ui

import android.animation.ObjectAnimator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.courierassist.app.R
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.databinding.ActivityMainBinding
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.service.CourierAccessibilityService

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = try {
            ServiceLocator.settingsRepository.load().language
        } catch (_: Exception) {
            AppLanguage.PL
        }
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    private lateinit var binding: ActivityMainBinding
    private var isRunning = false
    private var pendingStart = false
    private var dotPulseAnimator: ObjectAnimator? = null

    companion object {
        private const val REQUEST_MEDIA_PROJECTION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnToggle.setOnClickListener {
            if (isRunning) stopCapture() else startCapture()
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (ScreenCaptureService.isProjectionLost) {
            ScreenCaptureService.stopCapture(this)
            isRunning = false
            pendingStart = false
            updateUi()
            Toast.makeText(this, "Nagrywanie ekranu zostało przerwane. Kliknij Start żeby wznowić.", Toast.LENGTH_LONG).show()
        } else if (!pendingStart) {
            isRunning = ScreenCaptureService.instance != null
            updateUi()
        }
        updateAccessibilityHint()
    }

    override fun onPause() {
        super.onPause()
        dotPulseAnimator?.cancel()
    }

    private fun startCapture() {
        if (!isAccessibilityEnabled()) {
            Toast.makeText(this, "Włącz CourierAssist w Ustawieniach → Dostępność", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return
        }
        if (!CourierAccessibilityService.isConnected) {
            Toast.makeText(this, "Wyłącz i włącz ponownie przełącznik CourierAssist w Ustawieniach → Dostępność", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Wymagane uprawnienie do wyświetlania nakładki", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            return
        }
        Toast.makeText(this, "Zezwól na nagrywanie ekranu — to pozwala analizować oferty", Toast.LENGTH_SHORT).show()
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }

    private fun stopCapture() {
        ScreenCaptureService.stopCapture(this)
        isRunning = false
        pendingStart = false
        updateUi()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK && data != null) {
            pendingStart = true
            ScreenCaptureService.startCapture(this, resultCode, data)
            isRunning = true
            updateUi()
        } else {
            pendingStart = false
        }
    }

    private fun updateUi() {
        binding.tvStatus.setText(if (isRunning) R.string.status_running else R.string.status_stopped)
        binding.tvStatusSubtitle.setText(if (isRunning) R.string.status_subtitle_running else R.string.status_subtitle_stopped)
        binding.btnToggle.setText(if (isRunning) R.string.btn_stop else R.string.btn_start)

        val dotColor = if (isRunning) R.color.status_green else R.color.status_red
        (binding.viewStatusDot.background as? GradientDrawable)?.setColor(
            ContextCompat.getColor(this, dotColor)
        )

        dotPulseAnimator?.cancel()
        if (isRunning) {
            dotPulseAnimator = ObjectAnimator.ofFloat(binding.viewStatusDot, View.ALPHA, 1f, 0.25f).apply {
                duration = 900
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        } else {
            binding.viewStatusDot.alpha = 1f
        }

        // Prosta animacja karty statusu przy zmianie stanu
        binding.cardStatus.animate()
            .scaleX(0.97f).scaleY(0.97f)
            .setDuration(100)
            .withEndAction {
                binding.cardStatus.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(150)
                    .start()
            }.start()

        updateAccessibilityHint()
    }

    private fun updateAccessibilityHint() {
        binding.layoutAccessibilityHint.visibility =
            if (isAccessibilityEnabled()) View.GONE else View.VISIBLE
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        return enabledServices.lowercase().contains("courierassist")
    }
}
