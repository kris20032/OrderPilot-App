package com.courierassist.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.courierassist.app.R
import com.courierassist.app.databinding.ActivitySettingsBinding
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.MetricType
import com.courierassist.app.settings.DisplayConfig
import com.courierassist.app.settings.ThresholdConfig

class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val lang = try {
            ServiceLocator.settingsRepository.load().language
        } catch (_: Exception) {
            AppLanguage.PL
        }
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        loadSettings()
        setupSliders()
        binding.btnSave.setOnClickListener { saveSettings() }
    }

    private fun setupSliders() {
        binding.slOpacity.addOnChangeListener { _, value, _ ->
            binding.tvOpacityLabel.text = getString(R.string.settings_opacity, value.toInt())
        }
        binding.slDisplayTime.addOnChangeListener { _, value, _ ->
            binding.tvDisplayTimeLabel.text = getString(R.string.settings_display_time, value.toInt())
        }
    }

    private fun loadSettings() {
        val settings = ServiceLocator.settingsRepository.load()
        val t = settings.globalThresholds

        binding.etGreenThreshold.setText(t.greenMinZlPerHour.toInt().toString())
        binding.etYellowThreshold.setText(t.yellowMinZlPerHour.toInt().toString())

        val metrics = settings.display.visibleMetrics
        binding.swMetricZlPerHour.isChecked = MetricType.ZL_PER_HOUR in metrics
        binding.swMetricZlPerKm.isChecked = MetricType.ZL_PER_KM in metrics
        binding.swMetricAmount.isChecked = MetricType.AMOUNT in metrics
        binding.swMetricTime.isChecked = MetricType.TIME in metrics
        binding.swMetricDistance.isChecked = MetricType.DISTANCE in metrics

        val opacityRaw = settings.display.overlayOpacity.toFloat()
        val opacity = (Math.round((opacityRaw - 10f) / 5f) * 5f + 10f).coerceIn(10f, 100f)
        binding.slOpacity.value = opacity
        binding.tvOpacityLabel.text = getString(R.string.settings_opacity, opacity.toInt())

        val displayTime = settings.display.displayTimeSeconds.toFloat().coerceIn(1f, 30f)
        binding.slDisplayTime.value = displayTime
        binding.tvDisplayTimeLabel.text = getString(R.string.settings_display_time, displayTime.toInt())

        val langRadio = when (settings.language) {
            AppLanguage.PL -> R.id.rb_lang_pl
            AppLanguage.UK -> R.id.rb_lang_uk
            AppLanguage.EN -> R.id.rb_lang_en
        }
        binding.rgLanguage.check(langRadio)
    }

    private fun saveSettings() {
        val green = binding.etGreenThreshold.text.toString().toDoubleOrNull()
        val yellow = binding.etYellowThreshold.text.toString().toDoubleOrNull()

        if (green == null || yellow == null || yellow >= green) {
            Snackbar.make(binding.root, R.string.settings_validation_error, Snackbar.LENGTH_LONG).show()
            return
        }

        val metrics = mutableSetOf<MetricType>()
        if (binding.swMetricZlPerHour.isChecked) metrics += MetricType.ZL_PER_HOUR
        if (binding.swMetricZlPerKm.isChecked) metrics += MetricType.ZL_PER_KM
        if (binding.swMetricAmount.isChecked) metrics += MetricType.AMOUNT
        if (binding.swMetricTime.isChecked) metrics += MetricType.TIME
        if (binding.swMetricDistance.isChecked) metrics += MetricType.DISTANCE

        val language = when (binding.rgLanguage.checkedRadioButtonId) {
            R.id.rb_lang_uk -> AppLanguage.UK
            R.id.rb_lang_en -> AppLanguage.EN
            else -> AppLanguage.PL
        }

        val current = ServiceLocator.settingsRepository.load()
        val updated = current.copy(
            language = language,
            globalThresholds = ThresholdConfig(
                greenMinZlPerHour = green,
                yellowMinZlPerHour = yellow
            ),
            display = DisplayConfig(
                visibleMetrics = metrics,
                themeMode = current.display.themeMode,
                overlayOpacity = binding.slOpacity.value.toInt(),
                displayTimeSeconds = binding.slDisplayTime.value.toInt()
            )
        )
        val languageChanged = current.language != language
        ServiceLocator.settingsRepository.save(updated)
        if (languageChanged) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            finish()
        }
    }
}
