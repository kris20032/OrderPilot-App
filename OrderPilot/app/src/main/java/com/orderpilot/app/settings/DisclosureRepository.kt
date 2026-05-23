package com.orderpilot.app.settings

import android.content.Context

/**
 * Persists user's acceptance of the Prominent Disclosure (KD4, Task 3.2).
 *
 * Dedicated SharedPrefs file `order_pilot_disclosure.xml` — excluded z auto-backup
 * (backup_rules.xml + data_extraction_rules.xml). KD5 Play Store wymaga świeżej
 * zgody na dysclosure po restore na nowym urządzeniu.
 *
 * Versioning: jeśli zmieni się wording disclosure (dodana apka target, nowy typ
 * danych czytanych) → bump [CURRENT_DISCLOSURE_VERSION] → user zostanie poproszony
 * o ponowny consent.
 */
class DisclosureRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isAccepted(): Boolean = acceptedVersion() >= CURRENT_DISCLOSURE_VERSION

    fun acceptedVersion(): Int = prefs.getInt(KEY_ACCEPTED_VERSION, 0)

    fun markAccepted() {
        prefs.edit().putInt(KEY_ACCEPTED_VERSION, CURRENT_DISCLOSURE_VERSION).apply()
    }

    companion object {
        /**
         * Bumpować przy KAŻDEJ zmianie wordingu disclosure (M5, KD4).
         * v1 = initial Play Store release (2026-04-20).
         */
        const val CURRENT_DISCLOSURE_VERSION = 1

        private const val PREFS_NAME = "order_pilot_disclosure"
        private const val KEY_ACCEPTED_VERSION = "accepted_version"
    }
}
