package com.orderpilot.app.ui

import android.animation.ObjectAnimator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.media.projection.MediaProjectionConfig
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.orderpilot.app.R
import com.orderpilot.app.capture.ScreenCaptureService
import com.orderpilot.app.databinding.ActivityMainBinding
import com.orderpilot.app.di.AppLog
import com.orderpilot.app.di.MonitoringController
import com.orderpilot.app.di.ServiceLocator
import com.orderpilot.app.domain.AppLanguage
import com.orderpilot.app.service.OrderPilotAccessibilityService
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        if (!SetupActivity.isSetupComplete(this)) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
            return
        }

        // Stan startowy pochodzi z MonitoringController (SharedPrefs) — przy fresh install = STOPPED.
        // NIE resetujemy tu stanu — to był bug (nadpisywał ACTIVE przy rotacji/powrocie).

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnToggle.setOnClickListener {
            if (isRunning) stopCapture() else startCapture()
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.btnCheckSetup.setOnClickListener {
            startActivity(Intent(this, SetupActivity::class.java))
        }
        binding.btnSaveLogs.setOnClickListener {
            saveLogs()
        }
    }

    override fun onResume() {
        super.onResume()
        // UI odzwierciedla MonitoringController (source of truth).
        // Jeśli stan = ACTIVE (np. persystowany po restarcie procesu), UI pokazuje Active.
        // User może kliknąć Stop żeby zatrzymać.
        if (ScreenCaptureService.isProjectionLost) {
            ScreenCaptureService.stopCapture(this)
            pendingStart = false
            if (!OrderPilotAccessibilityService.isConnected) {
                // Brak projekcji i brak accessibility → monitoring nie działa → wyłącz
                MonitoringController.stop(this)
                isRunning = false
                Toast.makeText(this, getString(R.string.toast_projection_lost), Toast.LENGTH_LONG).show()
            } else {
                // Projekcja utracona ale accessibility działa → monitoring kontynuuje jako fallback
                isRunning = MonitoringController.isActive()
            }
            updateUi()
        } else if (!pendingStart) {
            isRunning = MonitoringController.isActive()
            updateUi()
        }
        updateAccessibilityHint()
    }

    override fun onPause() {
        super.onPause()
        dotPulseAnimator?.cancel()
    }

    private fun startCapture() {
        MonitoringController.start(this)
        if (!OrderPilotAccessibilityService.isConnected) {
            Toast.makeText(this, getString(R.string.accessibility_hint), Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return
        }
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            manager.createScreenCaptureIntent(MediaProjectionConfig.createConfigForDefaultDisplay())
        } else {
            manager.createScreenCaptureIntent()
        }
        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION)
    }

    private fun stopCapture() {
        MonitoringController.stop(this)
        OrderPilotAccessibilityService.cancelActiveJobs()
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

    private fun saveLogs() {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "OrderPilot_log_$timestamp.txt"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            file.writeText(AppLog.getBufferedLogs())

            Toast.makeText(this, getString(R.string.toast_logs_saved), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.toast_logs_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        return enabledServices.lowercase(Locale.ROOT).contains("orderpilot")
    }
}
