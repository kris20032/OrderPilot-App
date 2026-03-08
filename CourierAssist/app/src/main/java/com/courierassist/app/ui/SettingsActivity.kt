package com.courierassist.app.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.courierassist.app.R
import com.courierassist.app.databinding.ActivitySettingsBinding
import com.courierassist.app.di.ServiceLocator
import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.MetricType
import com.courierassist.app.settings.DisplayConfig
import com.courierassist.app.settings.ThresholdConfig
import android.widget.SeekBar

class SettingsActivity : Activity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSettings()
        setupSliders()
        binding.btnSave.setOnClickListener { saveSettings() }
    }

    private fun setupSliders() {
        binding.sbOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvOpacityLabel.text = getString(R.string.settings_opacity, progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
        binding.sbDisplayTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val seconds = progress + 5
                binding.tvDisplayTimeLabel.text = getString(R.string.settings_display_time, seconds)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    private fun loadSettings() {
        val settings = ServiceLocator.settingsRepository.load()
        val t = settings.globalThresholds

        binding.etGreenThreshold.setText(t.greenMinZlPerHour.toInt().toString())
        binding.etYellowThreshold.setText(t.yellowMinZlPerHour.toInt().toString())

        val metrics = settings.display.visibleMetrics
        binding.cbMetricZlPerHour.isChecked = MetricType.ZL_PER_HOUR in metrics
        binding.cbMetricZlPerKm.isChecked = MetricType.ZL_PER_KM in metrics
        binding.cbMetricAmount.isChecked = MetricType.AMOUNT in metrics
        binding.cbMetricTime.isChecked = MetricType.TIME in metrics
        binding.cbMetricDistance.isChecked = MetricType.DISTANCE in metrics

        binding.sbOpacity.progress = settings.display.overlayOpacity
        binding.tvOpacityLabel.text = getString(R.string.settings_opacity, settings.display.overlayOpacity)

        binding.sbDisplayTime.progress = settings.display.displayTimeSeconds - 5
        binding.tvDisplayTimeLabel.text = getString(R.string.settings_display_time, settings.display.displayTimeSeconds)

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
            Toast.makeText(this, R.string.settings_validation_error, Toast.LENGTH_SHORT).show()
            return
        }

        val metrics = mutableSetOf<MetricType>()
        if (binding.cbMetricZlPerHour.isChecked) metrics += MetricType.ZL_PER_HOUR
        if (binding.cbMetricZlPerKm.isChecked) metrics += MetricType.ZL_PER_KM
        if (binding.cbMetricAmount.isChecked) metrics += MetricType.AMOUNT
        if (binding.cbMetricTime.isChecked) metrics += MetricType.TIME
        if (binding.cbMetricDistance.isChecked) metrics += MetricType.DISTANCE

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
                overlayOpacity = binding.sbOpacity.progress,
                displayTimeSeconds = binding.sbDisplayTime.progress + 5
            )
        )
        ServiceLocator.settingsRepository.save(updated)
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}