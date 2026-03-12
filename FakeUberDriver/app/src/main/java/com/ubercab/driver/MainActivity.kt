package com.ubercab.driver

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ubercab.driver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var autoRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendOffer.setOnClickListener {
            if (checkOverlayPermission()) {
                sendOffer()
            }
        }

        binding.switchAuto.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutInterval.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                if (checkOverlayPermission()) startAutoMode()
            } else {
                stopAutoMode()
            }
        }
    }

    private fun selectedLanguage(): UberLanguage = when (binding.rgLanguage.checkedRadioButtonId) {
        R.id.rbUK -> UberLanguage.UK
        R.id.rbEN -> UberLanguage.EN
        else -> UberLanguage.PL
    }

    private fun sendOffer() {
        val lang = selectedLanguage()
        val offer = OfferRandomizer.random(lang)
        val intent = Intent(this, OfferOverlayService::class.java).apply {
            putExtra(OfferOverlayService.EXTRA_AMOUNT, offer.amount)
            putExtra(OfferOverlayService.EXTRA_TIME, offer.time)
            putExtra(OfferOverlayService.EXTRA_DISTANCE, offer.distance)
            putExtra(OfferOverlayService.EXTRA_PICKUP, offer.pickup)
            putExtra(OfferOverlayService.EXTRA_DROPOFF, offer.dropoff)
            putExtra(OfferOverlayService.EXTRA_LANGUAGE, lang.name)
        }
        startService(intent)
        binding.tvStatus.text = "Wysłano: ${offer.amount} · ${offer.time} · ${offer.distance}"
    }

    private fun startAutoMode() {
        stopAutoMode()
        val intervalSec = binding.etInterval.text.toString().toLongOrNull() ?: 30L
        val intervalMs = intervalSec * 1000L

        autoRunnable = object : Runnable {
            override fun run() {
                sendOffer()
                handler.postDelayed(this, intervalMs)
            }
        }
        handler.post(autoRunnable!!)
        binding.tvStatus.text = "Auto mode: co ${intervalSec}s"
    }

    private fun stopAutoMode() {
        autoRunnable?.let { handler.removeCallbacks(it) }
        autoRunnable = null
        binding.tvStatus.text = "Auto mode wyłączony"
    }

    private fun checkOverlayPermission(): Boolean {
        if (Settings.canDrawOverlays(this)) return true

        AlertDialog.Builder(this)
            .setMessage(getString(R.string.permission_required))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
            .show()
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoMode()
    }
}
