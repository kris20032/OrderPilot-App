package com.orderpilot.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.orderpilot.app.R
import com.orderpilot.app.databinding.ActivityDisclosureBinding
import com.orderpilot.app.di.AppLog
import com.orderpilot.app.di.OrderPilotApp
import com.orderpilot.app.di.ServiceLocator

/**
 * Prominent Disclosure Activity (KD4, M1/F2, Task 3.1).
 *
 * Pokazywany PRZED system dialogiem accessibility — Play policy "Alternative use
 * of accessibility" wymaga że user musi widzieć full-screen disclosure przed grant.
 *
 * Flow:
 *  - „Anuluj" → `finishAffinity()` (zamyka apkę całkowicie, KD4 element 7)
 *  - „Rozumiem" → `DisclosureRepository.markAccepted()` → MainActivity → finish()
 *
 * MainActivity w `onCreate` sprawdza `disclosureRepository.isAccepted()` i przekierowuje
 * tu jeśli false (Task 3.3). Po accept przepływ: DisclosureActivity → MainActivity
 * (który już przepuszcza, bo flag=true) → (jeśli setup niekompletny) SetupActivity.
 */
class DisclosureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisclosureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisclosureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.btnDisclosureAccept.setOnClickListener { onAccept() }
        binding.btnDisclosureCancel.setOnClickListener { onCancel() }
        binding.tvPrivacyPolicyLink.setOnClickListener { openPrivacyPolicy() }

        // KD4 element 7: back button zamyka apkę (żeby nie dało się ominąć disclosure
        // przez back + relaunch — MainActivity i tak wróciłby tutaj, ale UX musi być
        // wyraźny, a nie zapętlony).
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = onCancel()
        })
    }

    private fun onAccept() {
        ServiceLocator.disclosureRepository.markAccepted()
        AppLog.d(AppLog.TAG_MAIN, "Disclosure accepted")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onCancel() {
        AppLog.d(AppLog.TAG_MAIN, "Disclosure cancelled — closing app")
        finishAffinity()
    }

    private fun openPrivacyPolicy() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(OrderPilotApp.PRIVACY_POLICY_URL)))
        } catch (_: Exception) {
            Toast.makeText(this, getString(R.string.toast_pp_error), Toast.LENGTH_SHORT).show()
        }
    }
}

