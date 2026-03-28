package com.courierassist.app.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.courierassist.app.R
import com.courierassist.app.databinding.ActivitySetupBinding
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AppLanguage
import com.google.android.material.button.MaterialButton

class SetupActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = try {
            ServiceLocator.settingsRepository.load().language
        } catch (_: Exception) {
            AppLanguage.fromSystemLocale()
        }
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupManufacturerCard()

        binding.btnOverlay.setOnClickListener {
            showHint(R.string.toast_hint_overlay)
            openOverlaySettings()
        }
        binding.btnAccessibility.setOnClickListener {
            showHint(R.string.toast_hint_accessibility)
            openAccessibilitySettings()
        }
        binding.btnBattery.setOnClickListener { requestBatteryOptimizationExemption() }
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatuses()
    }

    // ─── Manufacturer detection & steps ───

    private enum class Manufacturer {
        SAMSUNG, XIAOMI, HUAWEI, OPPO, ONEPLUS, OTHER
    }

    private fun detectManufacturer(): Manufacturer {
        val m = Build.MANUFACTURER.lowercase()
        return when {
            m == "samsung" -> Manufacturer.SAMSUNG
            m == "xiaomi" || m == "redmi" || m == "poco" -> Manufacturer.XIAOMI
            m == "huawei" || m == "honor" -> Manufacturer.HUAWEI
            m == "oppo" || m == "realme" -> Manufacturer.OPPO
            m == "oneplus" -> Manufacturer.ONEPLUS
            else -> Manufacturer.OTHER
        }
    }

    private fun getManufacturerDisplayName(): String {
        val m = Build.MANUFACTURER
        return when (detectManufacturer()) {
            Manufacturer.SAMSUNG -> "Samsung"
            Manufacturer.XIAOMI -> m.replaceFirstChar { it.uppercase() } // Xiaomi/Redmi/POCO
            Manufacturer.HUAWEI -> m.replaceFirstChar { it.uppercase() } // Huawei/Honor
            Manufacturer.OPPO -> m.replaceFirstChar { it.uppercase() }   // Oppo/Realme
            Manufacturer.ONEPLUS -> "OnePlus"
            Manufacturer.OTHER -> m
        }
    }

    private data class SetupStep(
        val description: String,
        val buttonText: String? = null,
        val toastHintRes: Int? = null,
        val onClick: (() -> Unit)? = null
    )

    private fun getStepsForManufacturer(manufacturer: Manufacturer): List<SetupStep> {
        return when (manufacturer) {
            Manufacturer.SAMSUNG -> listOf(
                SetupStep(
                    getString(R.string.setup_samsung_step1),
                    getString(R.string.setup_samsung_step1_btn),
                    R.string.toast_hint_samsung_battery
                ) { openAppInfo() },
                SetupStep(
                    getString(R.string.setup_samsung_step2),
                    getString(R.string.setup_samsung_step2_btn),
                    R.string.toast_hint_samsung_sleeping
                ) { openDeviceCareBattery() },
                SetupStep(getString(R.string.setup_samsung_step3))
            )
            Manufacturer.XIAOMI -> listOf(
                SetupStep(
                    getString(R.string.setup_xiaomi_step1),
                    getString(R.string.setup_xiaomi_step1_btn),
                    R.string.toast_hint_xiaomi_autostart
                ) { openXiaomiAutostart() },
                SetupStep(
                    getString(R.string.setup_xiaomi_step2),
                    getString(R.string.setup_xiaomi_step2_btn),
                    R.string.toast_hint_xiaomi_battery
                ) { openAppInfo() },
                SetupStep(getString(R.string.setup_xiaomi_step3))
            )
            Manufacturer.HUAWEI -> listOf(
                SetupStep(
                    getString(R.string.setup_huawei_step1),
                    getString(R.string.setup_huawei_step1_btn),
                    R.string.toast_hint_huawei_launch
                ) { openHuaweiAppLaunch() },
                SetupStep(
                    getString(R.string.setup_huawei_step2),
                    getString(R.string.setup_huawei_step2_btn),
                    R.string.toast_hint_huawei_optimize
                ) { openAppInfo() }
            )
            Manufacturer.OPPO -> listOf(
                SetupStep(
                    getString(R.string.setup_oppo_step1),
                    getString(R.string.setup_oppo_step1_btn),
                    R.string.toast_hint_oppo_background
                ) { openAppInfo() },
                SetupStep(
                    getString(R.string.setup_oppo_step2),
                    getString(R.string.setup_oppo_step2_btn),
                    R.string.toast_hint_oppo_autolaunch
                ) { openAppInfo() },
                SetupStep(getString(R.string.setup_oppo_step3))
            )
            Manufacturer.ONEPLUS -> listOf(
                SetupStep(
                    getString(R.string.setup_oneplus_step1),
                    getString(R.string.setup_oneplus_step1_btn),
                    R.string.toast_hint_oneplus_optimize
                ) { openBatteryOptimization() },
                SetupStep(
                    getString(R.string.setup_oneplus_step2),
                    getString(R.string.setup_oneplus_step2_btn),
                    R.string.toast_hint_oneplus_deep
                ) { openBatterySettings() },
                SetupStep(getString(R.string.setup_oneplus_step3))
            )
            Manufacturer.OTHER -> emptyList()
        }
    }

    private fun setupManufacturerCard() {
        val manufacturer = detectManufacturer()
        if (manufacturer == Manufacturer.OTHER) return

        val steps = getStepsForManufacturer(manufacturer)
        if (steps.isEmpty()) return

        binding.cardManufacturer.visibility = View.VISIBLE
        binding.tvManufacturerTitle.text =
            getString(R.string.setup_manufacturer_title, getManufacturerDisplayName())

        val container = binding.containerManufacturerSteps
        container.removeAllViews()

        steps.forEachIndexed { index, step ->
            addStepView(container, index + 1, step)
        }
    }

    private fun addStepView(container: LinearLayout, number: Int, step: SetupStep) {
        val ctx = container.context
        val dp8 = dpToPx(8)
        val dp12 = dpToPx(12)
        val dp4 = dpToPx(4)

        // Step container
        val stepLayout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = if (number > 1) dp12 else 0
            }
        }

        // Number + description row
        val descRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Number circle
        val numberView = TextView(ctx).apply {
            text = "$number."
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ctx.getColor(R.color.on_surface))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dp8
            }
        }

        // Description
        val descView = TextView(ctx).apply {
            text = step.description
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(ctx.getColor(R.color.on_surface_variant))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        descRow.addView(numberView)
        descRow.addView(descView)
        stepLayout.addView(descRow)

        // Button (optional)
        if (step.buttonText != null && step.onClick != null) {
            val button = MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                text = step.buttonText
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                isAllCaps = false
                cornerRadius = dpToPx(20)
                minimumHeight = dpToPx(36)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp4
                }
                setOnClickListener {
                    step.toastHintRes?.let { res -> showHint(res) }
                    step.onClick.invoke()
                }
            }
            stepLayout.addView(button)
        }

        container.addView(stepLayout)
    }

    private fun showHint(resId: Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show()
    }

    private fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()

    // ─── Status checks ───

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

    private fun setStatus(view: TextView, ok: Boolean) {
        if (ok) {
            view.text = "\u2713" // ✓
            view.setTextColor(getColor(R.color.status_green))
        } else {
            view.text = "\u2717" // ✗
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

    // ─── Intent helpers ───

    private fun openOverlaySettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
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

    private fun openAppInfo() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun openBatterySettings() {
        safeStartActivity(
            Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
    }

    private fun openBatteryOptimization() {
        safeStartActivity(
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
    }

    private fun openDeviceCareBattery() {
        // Samsung Device Care → Battery
        safeStartActivity(
            Intent().apply {
                component = ComponentName(
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.battery.ui.BatteryActivity"
                )
            },
            Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
    }

    private fun openXiaomiAutostart() {
        // MIUI Autostart permission manager
        safeStartActivity(
            Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            },
            // Fallback: Security app
            Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.securitycenter.MainActivity"
                )
            },
            // Last resort: app info
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        )
    }

    private fun openHuaweiAppLaunch() {
        // Huawei Battery → App Launch
        safeStartActivity(
            Intent().apply {
                component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
            },
            Intent().apply {
                component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            },
            Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
    }

    /**
     * Try intents in order — first one that resolves wins, last one is fallback.
     */
    private fun safeStartActivity(vararg intents: Intent) {
        for (intent in intents) {
            try {
                startActivity(intent)
                return
            } catch (_: Exception) {
                // Try next
            }
        }
    }

    // ─── Static helper ───

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
