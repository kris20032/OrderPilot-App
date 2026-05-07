package com.orderpilot.app.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.orderpilot.app.R
import com.orderpilot.app.databinding.ActivitySettingsBinding
import com.orderpilot.app.di.OrderPilotApp
import com.orderpilot.app.di.ServiceLocator
import com.orderpilot.app.domain.AppLanguage
import com.orderpilot.app.domain.MetricType
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.settings.DisplayConfig
import com.orderpilot.app.settings.PlatformSettings
import com.orderpilot.app.settings.ThresholdConfig

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    // Tabs: 0=Global, 1=Uber, 2=Wolt, 3=Glovo, 4=Bolt
    private val tabPlatforms = listOf(null, Platform.UBER, Platform.WOLT, Platform.GLOVO, Platform.BOLT)
    private val tabNames = listOf("Global", "Uber", "Wolt", "Glovo", "Bolt")

    // Przechowuje wartości per tab (index → wartości)
    private data class TabValues(
        var greenHour: String = "",
        var yellowHour: String = "",
        var greenKm: String = "",
        var yellowKm: String = "",
        var displayTime: Float = 30f
    )
    private val tabData = Array(5) { TabValues() }
    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySystemBarsInsets()

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupTabs()
        loadSettings()
        setupSliders()
        binding.btnSave.setOnClickListener { saveSettings() }
        binding.tvPpLink.setOnClickListener { openPrivacyPolicy() }
    }

    /**
     * Edge-to-edge handling (targetSdk 35 / Android 15+ enforced). Bez tego Samsung 3-button
     * navbar zasłania dolny przycisk „Zapisz ustawienia" (zgłoszone przez Dominika 2026-05-07).
     * Padding root LinearLayoutu = systemBars insets, żeby toolbar nie szedł pod statusbar
     * a btnSave nie chował się pod navbar.
     */
    private fun applySystemBarsInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun openPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(OrderPilotApp.PRIVACY_POLICY_URL))
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.toast_pp_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabs() {
        for (name in tabNames) {
            binding.tabPlatform.addTab(binding.tabPlatform.newTab().setText(name))
        }
        binding.tabPlatform.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                saveCurrentTabToMemory()
                currentTab = tab.position
                loadCurrentTabFromMemory()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupSliders() {
        binding.slOpacity.addOnChangeListener { _, value, _ ->
            binding.tvOpacityLabel.text = getString(R.string.settings_opacity, value.toInt())
        }
        binding.slDisplayTime.addOnChangeListener { _, value, _ ->
            binding.tvDisplayTimeLabel.text = if (currentTab == 0)
                getString(R.string.settings_display_time, value.toInt())
            else
                getString(R.string.settings_display_time_platform, value.toInt())
        }
    }

    private fun loadSettings() {
        val settings = ServiceLocator.settingsRepository.load()

        // Global (tab 0)
        val gt = settings.globalThresholds
        tabData[0] = TabValues(
            greenHour = gt.greenMinZlPerHour.toInt().toString(),
            yellowHour = gt.yellowMinZlPerHour.toInt().toString(),
            greenKm = formatThreshold(gt.greenMinZlPerKm),
            yellowKm = formatThreshold(gt.yellowMinZlPerKm),
            displayTime = settings.display.displayTimeSeconds.toFloat().coerceIn(5f, 60f)
        )

        // Per platform (tabs 1-4)
        for (i in 1..4) {
            val platform = tabPlatforms[i]!!
            val override = settings.platformOverrides[platform]
            val t = override?.thresholds
            tabData[i] = TabValues(
                greenHour = t?.greenMinZlPerHour?.toInt()?.toString() ?: "",
                yellowHour = t?.yellowMinZlPerHour?.toInt()?.toString() ?: "",
                greenKm = t?.greenMinZlPerKm?.let { formatThreshold(it) } ?: "",
                yellowKm = t?.yellowMinZlPerKm?.let { formatThreshold(it) } ?: "",
                displayTime = (override?.displayTimeSeconds ?: settings.display.displayTimeSeconds).toFloat().coerceIn(5f, 60f)
            )
        }

        // Metryki
        val metrics = settings.display.visibleMetrics
        binding.swMetricZlPerHour.isChecked = MetricType.ZL_PER_HOUR in metrics
        binding.swMetricZlPerKm.isChecked = MetricType.ZL_PER_KM in metrics
        binding.swMetricAmount.isChecked = MetricType.AMOUNT in metrics
        binding.swMetricTime.isChecked = MetricType.TIME in metrics
        binding.swMetricDistance.isChecked = MetricType.DISTANCE in metrics

        // Opacity
        val opacityRaw = settings.display.overlayOpacity.toFloat()
        val opacity = (Math.round((opacityRaw - 10f) / 5f) * 5f + 10f).coerceIn(10f, 100f)
        binding.slOpacity.value = opacity
        binding.tvOpacityLabel.text = getString(R.string.settings_opacity, opacity.toInt())

        // Język
        val langRadio = when (settings.language) {
            AppLanguage.PL -> R.id.rb_lang_pl
            AppLanguage.UK -> R.id.rb_lang_uk
            AppLanguage.EN -> R.id.rb_lang_en
            AppLanguage.RU -> R.id.rb_lang_ru
        }
        binding.rgLanguage.check(langRadio)

        // Załaduj dane pierwszego taba (Global)
        currentTab = 0
        loadCurrentTabFromMemory()
    }

    private fun saveCurrentTabToMemory() {
        tabData[currentTab] = TabValues(
            greenHour = binding.etGreenThreshold.text.toString(),
            yellowHour = binding.etYellowThreshold.text.toString(),
            greenKm = binding.etGreenThresholdKm.text.toString(),
            yellowKm = binding.etYellowThresholdKm.text.toString(),
            displayTime = binding.slDisplayTime.value
        )
    }

    private fun loadCurrentTabFromMemory() {
        val data = tabData[currentTab]
        binding.etGreenThreshold.setText(data.greenHour)
        binding.etYellowThreshold.setText(data.yellowHour)
        binding.etGreenThresholdKm.setText(data.greenKm)
        binding.etYellowThresholdKm.setText(data.yellowKm)

        val displayTime = data.displayTime.coerceIn(5f, 60f)
        binding.slDisplayTime.value = displayTime
        binding.tvDisplayTimeLabel.text = if (currentTab == 0)
            getString(R.string.settings_display_time, displayTime.toInt())
        else
            getString(R.string.settings_display_time_platform, displayTime.toInt())

        // Hint
        binding.tvThresholdHint.setText(
            if (currentTab == 0) R.string.settings_threshold_hint_global
            else R.string.settings_threshold_hint_platform
        )

        // Placeholder hints dla per-platforma
        if (currentTab == 0) {
            binding.etGreenThreshold.hint = null
            binding.etYellowThreshold.hint = null
            binding.etGreenThresholdKm.hint = null
            binding.etYellowThresholdKm.hint = null
        } else {
            val global = tabData[0]
            binding.etGreenThreshold.hint = global.greenHour
            binding.etYellowThreshold.hint = global.yellowHour
            binding.etGreenThresholdKm.hint = global.greenKm
            binding.etYellowThresholdKm.hint = global.yellowKm
        }
    }

    private fun saveSettings() {
        // Zapisz aktualny tab do pamięci
        saveCurrentTabToMemory()

        // Walidacja Global (tab 0) — wymagane
        val globalGreenH = tabData[0].greenHour.toDoubleOrNull()
        val globalYellowH = tabData[0].yellowHour.toDoubleOrNull()
        if (globalGreenH == null || globalYellowH == null || globalYellowH >= globalGreenH) {
            Snackbar.make(binding.root, R.string.settings_validation_error, Snackbar.LENGTH_LONG).show()
            return
        }
        val globalGreenKm = tabData[0].greenKm.toDoubleOrNull() ?: 4.0
        val globalYellowKm = tabData[0].yellowKm.toDoubleOrNull() ?: 3.0

        val globalThresholds = ThresholdConfig(
            greenMinZlPerHour = globalGreenH,
            yellowMinZlPerHour = globalYellowH,
            greenMinZlPerKm = globalGreenKm,
            yellowMinZlPerKm = globalYellowKm
        )

        // Metryki
        val metrics = mutableSetOf<MetricType>()
        if (binding.swMetricZlPerHour.isChecked) metrics += MetricType.ZL_PER_HOUR
        if (binding.swMetricZlPerKm.isChecked) metrics += MetricType.ZL_PER_KM
        if (binding.swMetricAmount.isChecked) metrics += MetricType.AMOUNT
        if (binding.swMetricTime.isChecked) metrics += MetricType.TIME
        if (binding.swMetricDistance.isChecked) metrics += MetricType.DISTANCE

        // Język
        val language = when (binding.rgLanguage.checkedRadioButtonId) {
            R.id.rb_lang_uk -> AppLanguage.UK
            R.id.rb_lang_en -> AppLanguage.EN
            R.id.rb_lang_ru -> AppLanguage.RU
            else -> AppLanguage.PL
        }

        // Per-platform overrides
        val current = ServiceLocator.settingsRepository.load()
        val overrides = mutableMapOf<Platform, PlatformSettings>()
        for (i in 1..4) {
            val platform = tabPlatforms[i]!!
            val data = tabData[i]
            val greenH = data.greenHour.toDoubleOrNull()
            val yellowH = data.yellowHour.toDoubleOrNull()
            val greenKm = data.greenKm.toDoubleOrNull()
            val yellowKm = data.yellowKm.toDoubleOrNull()

            val thresholds = if (greenH != null || yellowH != null || greenKm != null || yellowKm != null) {
                ThresholdConfig(
                    greenMinZlPerHour = greenH ?: globalGreenH,
                    yellowMinZlPerHour = yellowH ?: globalYellowH,
                    greenMinZlPerKm = greenKm ?: globalGreenKm,
                    yellowMinZlPerKm = yellowKm ?: globalYellowKm
                )
            } else null

            val displayTime = data.displayTime.toInt()
            val globalDisplayTime = tabData[0].displayTime.toInt()

            val existing = current.platformOverrides[platform]
            overrides[platform] = PlatformSettings(
                thresholds = thresholds,
                filters = existing?.filters,
                displayTimeSeconds = if (displayTime != globalDisplayTime) displayTime else null
            )
        }

        val updated = current.copy(
            language = language,
            globalThresholds = globalThresholds,
            display = DisplayConfig(
                visibleMetrics = metrics,
                themeMode = current.display.themeMode,
                overlayOpacity = binding.slOpacity.value.toInt(),
                displayTimeSeconds = tabData[0].displayTime.toInt()
            ),
            platformOverrides = overrides
        )
        val languageChanged = current.language != language
        ServiceLocator.settingsRepository.save(updated)
        if (languageChanged) {
            val tag = when (language) {
                AppLanguage.PL -> "pl"
                AppLanguage.UK -> "uk"
                AppLanguage.EN -> "en"
                AppLanguage.RU -> "ru"
            }
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
        finish()
    }

    private fun formatThreshold(value: Double): String {
        return if (value == value.toLong().toDouble()) value.toInt().toString()
        else String.format("%.1f", value)
    }
}
