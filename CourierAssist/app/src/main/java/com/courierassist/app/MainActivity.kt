package com.courierassist.app

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.courierassist.app.databinding.ActivityMainBinding
import com.courierassist.app.service.CourierAccessibilityService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences

    private val PREFS_NAME = "courierassist_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        updateUI()

        binding.btnToggle.setOnClickListener {
            val current = prefs.getBoolean(CourierAccessibilityService.KEY_ENABLED, false)
            val newValue = !current
            prefs.edit().putBoolean(CourierAccessibilityService.KEY_ENABLED, newValue).apply()
            updateUI()

            // If enabling and overlay permission not granted, open settings
            if (newValue && !Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val isEnabled = prefs.getBoolean(CourierAccessibilityService.KEY_ENABLED, false)
        if (isEnabled) {
            binding.btnToggle.text = getString(R.string.btn_stop)
            binding.tvStatus.text = getString(R.string.status_running)
        } else {
            binding.btnToggle.text = getString(R.string.btn_start)
            binding.tvStatus.text = getString(R.string.status_stopped)
        }
    }
}
