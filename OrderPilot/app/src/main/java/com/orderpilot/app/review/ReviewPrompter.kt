package com.orderpilot.app.review

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import com.orderpilot.app.di.AppLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Prośba o recenzję w Google Play (oficjalne In-App Review API).
 *
 * Zasady (przemyślane, nie spamujemy):
 *  - liczymy DNI, w których kurier realnie używał monitoringu (odpalenie Start),
 *  - pytamy RAZ, dopiero od [MIN_DAYS_USED]. dnia użycia — user zdążył zobaczyć wartość,
 *  - moment: wejście do apki z AKTYWNYM monitoringiem (user w dobrym kontekście, nie w trasie),
 *  - samo API Google dodatkowo limituje wyświetlenia (quota) i pokazuje dialog
 *    tylko gdy uzna to za stosowne — nasze wywołanie to co najwyżej prośba.
 *
 * Czysta logika progu w [ReviewPolicy] (testowalna bez Androida).
 */
object ReviewPrompter {

    private const val PREFS_NAME = "order_pilot_settings"
    private const val KEY_DAYS_USED = "review_days_used"
    private const val KEY_ASKED = "review_asked"
    private const val TAG = "OP_Review"

    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /** Wołane przy starcie monitoringu — dopisuje dzisiejszy dzień do zbioru dni użycia. */
    fun onMonitoringStarted(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val days = prefs.getStringSet(KEY_DAYS_USED, emptySet()) ?: emptySet()
        val today = dayFormat.format(Date())
        if (today in days) return
        // Limit rozmiaru: po progu nie ma potrzeby zbierać dalej.
        if (days.size >= ReviewPolicy.MIN_DAYS_USED) return
        prefs.edit().putStringSet(KEY_DAYS_USED, days + today).apply()
    }

    /** Czy pokazać prośbę teraz? (nie pytaliśmy wcześniej + wystarczająco dni użycia) */
    fun shouldAsk(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val daysUsed = prefs.getStringSet(KEY_DAYS_USED, emptySet())?.size ?: 0
        return ReviewPolicy.shouldAsk(daysUsed = daysUsed, alreadyAsked = prefs.getBoolean(KEY_ASKED, false))
    }

    /**
     * Odpala systemowy dialog recenzji. Fire-and-forget: przy braku Google Play
     * (emulator, custom ROM) request kończy się cicho błędem i nic się nie dzieje.
     * [KEY_ASKED] ustawiamy dopiero po UDANYM launchu — nieudana próba nie pali
     * jedynej szansy na prośbę.
     */
    fun ask(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { request ->
            if (!request.isSuccessful) {
                AppLog.d(TAG, "Review flow unavailable: ${request.exception?.message}")
                return@addOnCompleteListener
            }
            manager.launchReviewFlow(activity, request.result).addOnCompleteListener {
                AppLog.d(TAG, "Review flow finished")
                activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit().putBoolean(KEY_ASKED, true).apply()
            }
        }
    }
}

/** Czysty próg decyzji — testowalny jednostkowo. */
object ReviewPolicy {
    const val MIN_DAYS_USED = 3

    fun shouldAsk(daysUsed: Int, alreadyAsked: Boolean): Boolean =
        !alreadyAsked && daysUsed >= MIN_DAYS_USED
}
