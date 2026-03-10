package com.courierassist.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.courierassist.app.R
import com.courierassist.app.databinding.ActivitySetupBinding
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AppLanguage
class SetupActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = try {
            ServiceLocator.settingsRepository.load().language
        } catch (_: Exception) {
            AppLanguage.PL
        }
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isSamsung()) {
            binding.cardSamsung.visibility = View.VISIBLE
        }

        binding.btnOverlay.setOnClickListener { openOverlaySettings() }
        binding.btnAccessibility.setOnClickListener { openAccessibilitySettings() }
        binding.btnBattery.setOnClickListener { requestBatteryOptimizationExemption() }
        binding.btnSamsung.setOnClickListener { openSamsungBatterySettings() }
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatuses()
    }

    private fun updateStatuses() {
        val overlayOk = Settings.canDrawOverlays(this)
        val accessibilityOk = isAccessibilityEnabled()
        val batteryOk = isBatteryOptimizationDisabled()

        setStatus(binding.tvOverlayStatus, overlayOk)
        setStatus(binding.tvAccessibilityStatus, accessibilityOk)
        setStatus(binding.tvBatteryStatus, batteryOk)

        val allRequiredOk = overlayOk && accessibilityOk && batteryOk
        binding.btnContinue.isEnabled = allRequiredOk
        binding.btnContinue.alpha = if (allRequiredOk) 1f else 0.4f
    }

    private fun setStatus(view: android.widget.TextView, ok: Boolean) {
        if (ok) {
            view.text = "✓"
            view.setTextColor(getColor(R.color.status_green))
        } else {
            view.text = "✗"
            view.setTextColor(getColor(R.color.status_red))
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        return enabledServices.lowercase().contains("courierassist")
    }

    private fun isBatteryOptimizationDisabled(): Boolean {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    private fun isSamsung(): Boolean =
        Build.MANUFACTURER.lowercase() == "samsung"

    private fun openOverlaySettings() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName"))
        startActivity(intent)
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun requestBatteryOptimizationExemption() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        }
    }

    private fun openSamsungBatterySettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName"))
        startActivity(intent)
    }

    companion object {
        fun isSetupComplete(context: Context): Boolean {
            val overlayOk = Settings.canDrawOverlays(context)
            val accessibilityOk = run {
                val enabled = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: ""
                enabled.lowercase().contains("courierassist")
            }
            val batteryOk = (context.getSystemService(POWER_SERVICE) as PowerManager)
                .isIgnoringBatteryOptimizations(context.packageName)
            return overlayOk && accessibilityOk && batteryOk
        }
    }
}