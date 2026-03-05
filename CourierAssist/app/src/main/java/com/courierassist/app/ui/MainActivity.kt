package com.courierassist.app.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import com.courierassist.app.R
import com.courierassist.app.capture.ScreenCaptureService
import com.courierassist.app.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private var isRunning = false

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
        isRunning = ScreenCaptureService.instance != null
        updateUi()
    }

    private fun startCapture() {
        if (!isAccessibilityEnabled()) {
            Toast.makeText(this, "Włącz CourierAssist w Ustawieniach → Dostępność", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Wymagane uprawnienie do wyświetlania nakładki", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            return
        }
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }

    private fun stopCapture() {
        ScreenCaptureService.stopCapture(this)
        isRunning = false
        updateUi()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK && data != null) {
            ScreenCaptureService.startCapture(this, resultCode, data)
            isRunning = true
            updateUi()
        }
    }

    private fun updateUi() {
        binding.tvStatus.setText(if (isRunning) R.string.status_running else R.string.status_stopped)
        binding.btnToggle.setText(if (isRunning) R.string.btn_stop else R.string.btn_start)
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        return enabledServices.lowercase().contains("courierassist")
    }
}