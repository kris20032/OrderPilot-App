package com.orderpilot.app.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.orderpilot.app.R
import com.orderpilot.app.databinding.ActivitySetupBinding
import com.orderpilot.app.di.ServiceLocator
import com.orderpilot.app.domain.AnalysisResult
import com.orderpilot.app.domain.AppLanguage
import com.orderpilot.app.domain.MetricType
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.domain.ProfitLevel
import com.orderpilot.app.overlay.OverlayViewFactory
import com.orderpilot.app.service.OrderPilotAccessibilityService
import com.orderpilot.app.settings.DisplayConfig
import com.orderpilot.app.ui.setup.SetupFlow
import com.orderpilot.app.ui.setup.SetupStatus
import com.orderpilot.app.ui.setup.SetupStep
import java.util.Locale

/**
 * Wizard konfiguracji v2 — jeden krok = jeden ekran, auto-przejście po nadaniu uprawnienia.
 *
 * Zasady UX:
 *  - najpierw NAGRODA (podgląd prawdziwej belki — ten sam kod co produkcyjny overlay),
 *    dopiero potem prośby o uprawnienia;
 *  - każdy ekran: po co to uprawnienie + instrukcja 1-2-3 + jeden duży przycisk;
 *  - powrót z ustawień systemowych → wykrycie nadania → ✓ + płynne przejście dalej;
 *  - po nadaniu overlay pokazujemy belkę NA ŻYWO (moment „wow" + nauka, czym ona jest);
 *  - kroki producenta (autostart itd.) są NIEBLOKUJĄCE, na końcu, zawsze dostępne
 *    ponownie z podsumowania.
 *
 * Logika kolejności kroków: [SetupFlow] (czysta, testowana jednostkowo).
 */
class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private val handler = Handler(Looper.getMainLooper())
    private val prefs by lazy { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    private var currentStep: SetupStep? = null
    private var advancing = false
    private var overlayDemoShown = false
    private var notificationsDeniedPermanently = false

    /** Podgląd belki na ekranie powitalnym — rotacja GREEN/YELLOW/RED. */
    private var previewIndex = 0
    private val previewRunnable = object : Runnable {
        override fun run() {
            showNextPreview()
            handler.postDelayed(this, 2400)
        }
    }

    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            ) {
                // „Nie pytaj ponownie" / polityka systemu — dialog już się nie pokaże.
                notificationsDeniedPermanently = true
            }
            refresh()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySystemBarsInsets()

        binding.viewFlipper.setInAnimation(this, R.anim.step_in)
        binding.viewFlipper.setOutAnimation(this, R.anim.step_out)

        binding.btnWelcomeStart.setOnClickListener {
            prefs.edit().putBoolean(KEY_WELCOME_SEEN, true).apply()
            goTo(SetupFlow.nextStep(SetupStep.WELCOME, readStatus()))
        }
        binding.btnOverlayOpen.setOnClickListener {
            showHint(R.string.toast_hint_overlay)
            openOverlaySettings()
        }
        binding.btnAccessibilityOpen.setOnClickListener {
            showHint(R.string.toast_hint_accessibility)
            openAccessibilitySettings()
        }
        binding.btnNotifications.setOnClickListener { onNotificationsClick() }
        binding.btnBatteryOpen.setOnClickListener { requestBatteryOptimizationExemption() }
        binding.btnOemDone.setOnClickListener { goTo(SetupStep.DONE) }
        binding.btnDoneTestOverlay.setOnClickListener { showOverlayDemo() }
        binding.btnDoneOemHints.setOnClickListener { goTo(SetupStep.OEM) }
        binding.btnDoneFinish.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setupOemScreen()
        setupWelcomePreview()
    }

    override fun onResume() {
        super.onResume()
        refresh()
        if (currentStep == SetupStep.WELCOME) startPreviewRotation()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(previewRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        hideOverlayDemo()
    }

    /** Edge-to-edge (targetSdk 35) — bez tego dolne przyciski wchodzą pod pasek nawigacji. */
    private fun applySystemBarsInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    // ─── Maszyna kroków ───

    private fun readStatus() = SetupStatus(
        overlayOk = Settings.canDrawOverlays(this),
        accessibilityOk = isAccessibilityEnabled(this),
        notificationsOk = areNotificationsGranted(this),
        batteryOk = isBatteryOptimizationDisabled(this),
        notificationsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
        oemApplicable = true, // zawsze: znane marki mają dedykowane kroki, reszta generyczne
        welcomeSeen = prefs.getBoolean(KEY_WELCOME_SEEN, false)
    )

    /**
     * Rdzeń auto-przejść: wywoływane przy każdym powrocie do aktywności i po wyniku
     * dialogu uprawnień. Gdy bieżący krok został właśnie spełniony → ✓ i po chwili dalej.
     */
    private fun refresh() {
        val status = readStatus()

        val step = currentStep ?: SetupFlow.initialStep(status).also {
            currentStep = it
            render(it, status, animate = false)
        }

        updateGrantedMarkers(status)
        updateNotificationsButton()

        if (!advancing && step.isPermissionStep() && SetupFlow.isStepSatisfied(step, status)) {
            advancing = true
            var delay = ADVANCE_DELAY_MS
            if (step == SetupStep.OVERLAY && !overlayDemoShown) {
                // Moment „wow": użytkownik właśnie odblokował belkę — pokaż mu ją NAPRAWDĘ.
                // Dłuższa pauza, żeby zdążył przeczytać podpis i zarejestrować belkę.
                overlayDemoShown = true
                binding.tvOverlayGranted.setText(R.string.setup_overlay_demo_caption)
                showOverlayDemo()
                delay = ADVANCE_DELAY_DEMO_MS
            }
            handler.postDelayed({
                advancing = false
                goTo(SetupFlow.nextStep(step, readStatus()))
            }, delay)
        }
    }

    private fun goTo(step: SetupStep) {
        val status = readStatus()
        currentStep = step
        render(step, status, animate = true)
    }

    private fun render(step: SetupStep, status: SetupStatus, animate: Boolean) {
        val index = when (step) {
            SetupStep.WELCOME -> 0
            SetupStep.OVERLAY -> 1
            SetupStep.ACCESSIBILITY -> 2
            SetupStep.NOTIFICATIONS -> 3
            SetupStep.BATTERY -> 4
            SetupStep.OEM -> 5
            SetupStep.DONE -> 6
        }
        if (!animate) {
            binding.viewFlipper.inAnimation = null
            binding.viewFlipper.outAnimation = null
        }
        binding.viewFlipper.displayedChild = index
        if (!animate) {
            binding.viewFlipper.setInAnimation(this, R.anim.step_in)
            binding.viewFlipper.setOutAnimation(this, R.anim.step_out)
        }

        // Nagłówek postępu
        val progress = SetupFlow.progress(step, status)
        binding.layoutProgressHeader.isVisible = progress != null
        if (progress != null) {
            val (current, total) = progress
            binding.tvProgressLabel.text = getString(R.string.setup_progress_label, current, total)
            binding.progressBar.max = total
            // Pasek pokazuje UKOŃCZONE kroki (bieżący jeszcze trwa).
            binding.progressBar.setProgressCompat(current - 1, animate)
        }

        when (step) {
            SetupStep.WELCOME -> startPreviewRotation()
            SetupStep.DONE -> renderDoneScreen(status)
            else -> handler.removeCallbacks(previewRunnable)
        }
        updateGrantedMarkers(status)
    }

    private fun updateGrantedMarkers(status: SetupStatus) {
        binding.tvOverlayGranted.isVisible = status.overlayOk
        binding.tvAccessibilityGranted.isVisible = status.accessibilityOk
        binding.tvNotificationsGranted.isVisible = status.notificationsOk
        binding.tvBatteryGranted.isVisible = status.batteryOk

        binding.btnOverlayOpen.isVisible = !status.overlayOk
        binding.btnAccessibilityOpen.isVisible = !status.accessibilityOk
        binding.btnNotifications.isVisible = !status.notificationsOk
        binding.btnBatteryOpen.isVisible = !status.batteryOk
    }

    private fun SetupStep.isPermissionStep() = this in listOf(
        SetupStep.OVERLAY, SetupStep.ACCESSIBILITY, SetupStep.NOTIFICATIONS, SetupStep.BATTERY
    )

    // ─── Ekran powitalny: podgląd belki ───

    private fun setupWelcomePreview() {
        showNextPreview()
    }

    private fun startPreviewRotation() {
        handler.removeCallbacks(previewRunnable)
        handler.postDelayed(previewRunnable, 2400)
    }

    private fun showNextPreview() {
        val sample = PREVIEW_SAMPLES[previewIndex % PREVIEW_SAMPLES.size]
        previewIndex++
        val view = OverlayViewFactory.create(this, sample, previewDisplayConfig(), appLanguage())
        // Podgląd jest atrapą: bez close/drag (fabryka nie podpina listenerów — widok bierny).
        val container = binding.flBelkaPreview
        view.alpha = 0f
        container.addView(view)
        view.animate().alpha(1f).setDuration(350).start()
        // Usuń poprzednie widoki po crossfade (zostaw max 2 podczas animacji).
        while (container.childCount > 2) container.removeViewAt(0)
        if (container.childCount == 2) {
            val old = container.getChildAt(0)
            old.animate().alpha(0f).setDuration(350).withEndAction {
                container.removeView(old)
            }.start()
        }
    }

    private fun previewDisplayConfig() = DisplayConfig(
        visibleMetrics = setOf(MetricType.ZL_PER_HOUR, MetricType.ZL_PER_KM, MetricType.AMOUNT),
        overlayOpacity = 95
    )

    private fun appLanguage(): AppLanguage = try {
        ServiceLocator.settingsRepository.load().language
    } catch (_: Exception) {
        AppLanguage.fromSystemLocale()
    }

    // ─── Demo prawdziwej belki systemowej (po nadaniu overlay / test z podsumowania) ───

    private fun showOverlayDemo() {
        if (!Settings.canDrawOverlays(this)) return
        try {
            ServiceLocator.overlayManager.show(PREVIEW_SAMPLES[0], previewDisplayConfig(), appLanguage())
        } catch (_: Exception) {
            return
        }
        handler.postDelayed({ hideOverlayDemo() }, OVERLAY_DEMO_MS)
    }

    private fun hideOverlayDemo() {
        try {
            ServiceLocator.overlayManager.hide()
        } catch (_: Exception) {}
    }

    // ─── Ekran „Gotowe" ───

    private fun renderDoneScreen(status: SetupStatus) {
        // Celebracja: duży zielony check wskakuje z lekkim odbiciem (lekka animacja View,
        // zero obciążenia — jeden przebieg przy wejściu na ekran).
        binding.ivDoneCheck.apply {
            scaleX = 0.3f
            scaleY = 0.3f
            alpha = 0f
            animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(450)
                .setInterpolator(android.view.animation.OvershootInterpolator(1.4f))
                .start()
        }

        val container = binding.containerDoneChecklist
        container.removeAllViews()
        val items = buildList {
            add(getString(R.string.setup_step_overlay_title))
            add(getString(R.string.setup_step_accessibility_title))
            if (status.notificationsSupported) add(getString(R.string.setup_step_notifications_title))
            add(getString(R.string.setup_step_battery_title))
        }
        items.forEach { label ->
            val row = TextView(this).apply {
                text = getString(R.string.setup_done_check_row, label)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTextColor(getColor(R.color.on_surface))
                setPadding(0, dpToPx(6), 0, dpToPx(6))
            }
            container.addView(row)
        }

        // Wskazówka językowa (znany limit OCR: cyrylica) — tylko dla UI w UA/RU.
        binding.tvDoneLangHint.isVisible = appLanguage() in listOf(AppLanguage.UK, AppLanguage.RU)

        binding.btnDoneOemHints.isVisible = true
        binding.btnDoneOemHints.text =
            getString(R.string.setup_done_oem_hints_btn, getManufacturerDisplayName())
    }

    // ─── Powiadomienia (API 33+) ───

    private fun onNotificationsClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (notificationsDeniedPermanently) {
            openAppNotificationSettings()
        } else {
            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun updateNotificationsButton() {
        binding.tvNotificationsSettingsHint.isVisible = notificationsDeniedPermanently &&
            !areNotificationsGranted(this)
        binding.btnNotifications.setText(
            if (notificationsDeniedPermanently) R.string.setup_step_notifications_btn_settings
            else R.string.setup_step_notifications_btn
        )
    }

    private fun openAppNotificationSettings() {
        safeStartActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName),
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")),
            Intent(Settings.ACTION_SETTINGS)
        )
    }

    // ─── Ekran OEM (wskazówki producenta) ───

    private enum class Manufacturer {
        SAMSUNG, XIAOMI, HUAWEI, OPPO, VIVO, ONEPLUS, OTHER
    }

    private fun detectManufacturer(): Manufacturer {
        val m = Build.MANUFACTURER.lowercase(Locale.ROOT)
        return when {
            m == "samsung" -> Manufacturer.SAMSUNG
            m == "xiaomi" || m == "redmi" || m == "poco" -> Manufacturer.XIAOMI
            m == "huawei" || m == "honor" -> Manufacturer.HUAWEI
            m == "oppo" || m == "realme" -> Manufacturer.OPPO
            m == "vivo" || m == "iqoo" -> Manufacturer.VIVO
            m == "oneplus" -> Manufacturer.ONEPLUS
            else -> Manufacturer.OTHER
        }
    }

    private fun getManufacturerDisplayName(): String {
        val m = Build.MANUFACTURER
        return when (detectManufacturer()) {
            Manufacturer.SAMSUNG -> "Samsung"
            Manufacturer.ONEPLUS -> "OnePlus"
            Manufacturer.OTHER -> m.replaceFirstChar { it.uppercase() }
            else -> m.replaceFirstChar { it.uppercase() } // Xiaomi/Redmi/POCO/Huawei/Honor/Oppo/Realme/Vivo/iQOO
        }
    }

    private data class SetupOemStep(
        val description: String,
        val buttonText: String? = null,
        val toastHintRes: Int? = null,
        val onClick: (() -> Unit)? = null
    )

    private fun getStepsForManufacturer(manufacturer: Manufacturer): List<SetupOemStep> {
        return when (manufacturer) {
            Manufacturer.SAMSUNG -> listOf(
                SetupOemStep(
                    getString(R.string.setup_samsung_step1),
                    getString(R.string.setup_samsung_step1_btn),
                    R.string.toast_hint_samsung_battery
                ) { openAppInfo() },
                SetupOemStep(
                    getString(R.string.setup_samsung_step2),
                    getString(R.string.setup_samsung_step2_btn),
                    R.string.toast_hint_samsung_sleeping
                ) { openDeviceCareBattery() },
                SetupOemStep(getString(R.string.setup_samsung_step3))
            )
            Manufacturer.XIAOMI -> listOf(
                SetupOemStep(
                    getString(R.string.setup_xiaomi_step1),
                    getString(R.string.setup_xiaomi_step1_btn),
                    R.string.toast_hint_xiaomi_autostart
                ) { openXiaomiAutostart() },
                SetupOemStep(
                    getString(R.string.setup_xiaomi_step2),
                    getString(R.string.setup_xiaomi_step2_btn),
                    R.string.toast_hint_xiaomi_battery
                ) { openAppInfo() },
                SetupOemStep(getString(R.string.setup_xiaomi_step3))
            )
            Manufacturer.HUAWEI -> listOf(
                SetupOemStep(
                    getString(R.string.setup_huawei_step1),
                    getString(R.string.setup_huawei_step1_btn),
                    R.string.toast_hint_huawei_launch
                ) { openHuaweiAppLaunch() },
                SetupOemStep(
                    getString(R.string.setup_huawei_step2),
                    getString(R.string.setup_huawei_step2_btn),
                    R.string.toast_hint_huawei_optimize
                ) { openAppInfo() }
            )
            Manufacturer.OPPO -> listOf(
                SetupOemStep(
                    // M16: dedykowany Startup Manager ColorOS zamiast ślepego App Info
                    getString(R.string.setup_oppo_step_autostart),
                    getString(R.string.setup_oppo_step_autostart_btn),
                    R.string.toast_hint_oppo_autolaunch
                ) { openOppoAutostart() },
                SetupOemStep(
                    getString(R.string.setup_oppo_step1),
                    getString(R.string.setup_oppo_step1_btn),
                    R.string.toast_hint_oppo_background
                ) { openAppInfo() },
                SetupOemStep(getString(R.string.setup_oppo_step3))
            )
            Manufacturer.VIVO -> listOf(
                SetupOemStep(
                    getString(R.string.setup_vivo_step1),
                    getString(R.string.setup_vivo_step1_btn),
                    R.string.toast_hint_vivo_autostart
                ) { openVivoAutostart() },
                SetupOemStep(getString(R.string.setup_vivo_step2)),
                SetupOemStep(getString(R.string.setup_vivo_step3))
            )
            Manufacturer.ONEPLUS -> listOf(
                SetupOemStep(
                    getString(R.string.setup_oneplus_step1),
                    getString(R.string.setup_oneplus_step1_btn),
                    R.string.toast_hint_oneplus_optimize
                ) { openBatteryOptimization() },
                SetupOemStep(
                    getString(R.string.setup_oneplus_step2),
                    getString(R.string.setup_oneplus_step2_btn),
                    R.string.toast_hint_oneplus_deep
                ) { openBatterySettings() },
                SetupOemStep(getString(R.string.setup_oneplus_step3))
            )
            Manufacturer.OTHER -> listOf(
                // M13: generyczna karta zamiast ukrywania — te dwa nawyki ratują każdy telefon.
                SetupOemStep(getString(R.string.setup_generic_step1)),
                SetupOemStep(getString(R.string.setup_generic_step2))
            )
        }
    }

    private fun setupOemScreen() {
        binding.tvOemTitle.text =
            getString(R.string.setup_step_oem_title, getManufacturerDisplayName())

        val container = binding.containerManufacturerSteps
        container.removeAllViews()
        getStepsForManufacturer(detectManufacturer()).forEachIndexed { index, step ->
            addStepView(container, index + 1, step)
        }
    }

    private fun addStepView(container: LinearLayout, number: Int, step: SetupOemStep) {
        val ctx = container.context
        val dp8 = dpToPx(8)
        val dp12 = dpToPx(12)
        val dp4 = dpToPx(4)

        val stepLayout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = if (number > 1) dp12 else 0
            }
        }

        val descRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

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

        val descView = TextView(ctx).apply {
            text = step.description
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
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

    // ─── Intent helpers ───

    private fun openOverlaySettings() {
        safeStartActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        )
    }

    /**
     * Ustawienia dostępności z podświetleniem NASZEJ usługi (mniej szukania).
     * Nieudokumentowane extras wspierane przez AOSP/Pixel i część OEM — na innych
     * urządzeniach po prostu ignorowane (czyli zwykła lista dostępności).
     */
    private fun openAccessibilitySettings() {
        val serviceComponent = ComponentName(this, OrderPilotAccessibilityService::class.java)
            .flattenToString()
        val highlightIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            putExtra(EXTRA_FRAGMENT_ARG_KEY, serviceComponent)
            putExtra(
                EXTRA_SHOW_FRAGMENT_ARGUMENTS,
                Bundle().apply { putString(EXTRA_FRAGMENT_ARG_KEY, serviceComponent) }
            )
        }
        safeStartActivity(highlightIntent, Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun requestBatteryOptimizationExemption() {
        // Świadomie NIE używamy ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS —
        // wymaga uprawnienia objętego restrykcjami Play. Otwieramy listę + hint.
        safeStartActivity(
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
        Toast.makeText(this, R.string.toast_hint_battery_optimization, Toast.LENGTH_LONG).show()
    }

    private fun openAppInfo() {
        safeStartActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")),
            Intent(Settings.ACTION_SETTINGS)
        )
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
            Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.securitycenter.MainActivity"
                )
            },
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        )
    }

    /** ColorOS (OPPO/Realme) Startup Manager — komponenty wg biblioteki AutoStarter. */
    private fun openOppoAutostart() {
        safeStartActivity(
            Intent().apply {
                component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            },
            Intent().apply {
                component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.startupapp.StartupAppListActivity"
                )
            },
            Intent().apply {
                component = ComponentName(
                    "com.oppo.safe",
                    "com.oppo.safe.permission.startup.StartupAppListActivity"
                )
            },
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        )
    }

    /** Vivo/iQOO Autostart (FuntouchOS/OriginOS) — komponenty wg biblioteki AutoStarter. */
    private fun openVivoAutostart() {
        safeStartActivity(
            Intent().apply {
                component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            },
            Intent().apply {
                component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
            },
            Intent().apply {
                component = ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                )
            },
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

    /** Próbuje intenty po kolei — pierwszy, który się otworzy, wygrywa. */
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

    companion object {
        private const val PREFS_NAME = "order_pilot_settings"
        private const val KEY_WELCOME_SEEN = "setup_welcome_seen"
        private const val ADVANCE_DELAY_MS = 1600L
        private const val ADVANCE_DELAY_DEMO_MS = 3000L
        private const val OVERLAY_DEMO_MS = 4500L

        // Nieudokumentowane, ale stabilne od lat klucze fragmentu ustawień (AOSP).
        private const val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"
        private const val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"

        /** Przykładowe belki do podglądu/demo — realistyczne wartości PL. */
        private val PREVIEW_SAMPLES = listOf(
            AnalysisResult(
                offer = Offer(Platform.UBER, amount = 24.00, estimatedMinutes = 30, distanceKm = 8.0),
                zlPerHour = 48.0, zlPerKm = 3.0, level = ProfitLevel.GREEN
            ),
            AnalysisResult(
                offer = Offer(Platform.UBER, amount = 17.50, estimatedMinutes = 30, distanceKm = 7.0),
                zlPerHour = 35.0, zlPerKm = 2.5, level = ProfitLevel.YELLOW
            ),
            AnalysisResult(
                offer = Offer(Platform.UBER, amount = 11.00, estimatedMinutes = 30, distanceKm = 9.0),
                zlPerHour = 22.0, zlPerKm = 1.2, level = ProfitLevel.RED
            )
        )

        fun isAccessibilityEnabled(context: Context): Boolean {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: ""
            return enabledServices.lowercase(Locale.ROOT).contains("orderpilot")
        }

        fun areNotificationsGranted(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
            return context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        }

        fun isBatteryOptimizationDisabled(context: Context): Boolean {
            val pm = context.getSystemService(POWER_SERVICE) as PowerManager
            return pm.isIgnoringBatteryOptimizations(context.packageName)
        }

        /**
         * Bramka „setup zakończony" używana przez MainActivity.
         * M12: na API 33+ wymagane też POST_NOTIFICATIONS — bez niego watchdog
         * i powiadomienie FGS są niewidoczne (użytkownik jedzie „na ślepo").
         */
        fun isSetupComplete(context: Context): Boolean {
            return Settings.canDrawOverlays(context) &&
                isAccessibilityEnabled(context) &&
                isBatteryOptimizationDisabled(context) &&
                areNotificationsGranted(context)
        }
    }
}
