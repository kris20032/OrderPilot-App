package com.courierassist.app

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.databinding.ActivityMainBinding
import com.courierassist.app.service.CourierAccessibilityService

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CourierAssist"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "courierassist_prefs"

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            Log.d(TAG, "MediaProjection permission granted — starting ScreenCaptureService")
            ScreenCaptureService.startCapture(this, result.resultCode, result.data!!)
        } else {
            Log.w(TAG, "MediaProjection permission denied")
        }
        updateUI()
    }

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

            if (newValue) {
                // Włączanie — sprawdź uprawnienia
                if (!Settings.canDrawOverlays(this)) {
                    startActivity(Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ))
                }
                if (ScreenCaptureService.instance?.isReady() != true) {
                    requestMediaProjectionPermission()
                }
            }
            updateUI()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun requestMediaProjectionPermission() {
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(manager.createScreenCaptureIntent())
    }

    private fun updateUI() {
        val isEnabled = prefs.getBoolean(CourierAccessibilityService.KEY_ENABLED, false)
        if (isEnabled) {
            binding.btnToggle.text = getString(R.string.btn_stop)
            val projectionStatus = if (ScreenCaptureService.instance?.isReady() == true) "✓" else "⚠ brak ekranu"
            binding.tvStatus.text = "${getString(R.string.status_running)} $projectionStatus"
        } else {
            binding.btnToggle.text = getString(R.string.btn_start)
            binding.tvStatus.text = getString(R.string.status_stopped)
        }
    }
}
