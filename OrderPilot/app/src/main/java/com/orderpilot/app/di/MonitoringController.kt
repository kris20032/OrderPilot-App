package com.orderpilot.app.di

import android.content.Context
import com.orderpilot.app.service.MonitoringForegroundService
import com.orderpilot.app.service.ServiceWatchdog

/**
 * Centralny source of truth dla stanu monitoringu (intencja usera).
 *
 * Persystowany w SharedPreferences (ten sam plik co AppSettings), cache w pamięci (@Volatile).
 * Stan zmienia się tylko przez start()/stop() wołane z UI (przyciski Start/Stop).
 *
 * onTaskRemoved (swipe z recents) NIE zmienia stanu — to nie jest Stop.
 *
 * Zob. docs/dev/STATE_REFACTOR_PLAN.md, STATE_REFACTOR_CHECKPOINT.md
 */
object MonitoringController {

    enum class State { ACTIVE, STOPPED }

    private const val PREFS_NAME = "order_pilot_settings"
    private const val PREFS_KEY = "monitoring_state"
    private const val TAG = "OP_MonitoringController"

    @Volatile
    private var cached: State = State.STOPPED

    @Volatile
    private var initialized = false

    fun initialize(context: Context) {
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(PREFS_KEY, State.STOPPED.name) ?: State.STOPPED.name
        cached = runCatching { State.valueOf(raw) }.getOrDefault(State.STOPPED)

        val alreadyInitialized = initialized
        initialized = true

        AppLog.d(TAG, "initialized: state=$cached (first=${ !alreadyInitialized })")

        // Jesli persystowany stan = ACTIVE, wznow foreground service + watchdog
        // (np. po restarcie telefonu lub process kill)
        // Przy ponownym wywołaniu (z Watchdog/BootReceiver) nadal bezpiecznie — startForegroundService jest idempotentne
        if (cached == State.ACTIVE) {
            MonitoringForegroundService.start(context.applicationContext)
            ServiceWatchdog.schedule(context.applicationContext)
        }
    }

    fun start(context: Context) {
        cached = State.ACTIVE
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREFS_KEY, State.ACTIVE.name)
            .commit()
        AppLog.d(TAG, "state → ACTIVE")

        // Foreground service + watchdog
        MonitoringForegroundService.start(context.applicationContext)
        ServiceWatchdog.schedule(context.applicationContext)
    }

    fun stop(context: Context) {
        cached = State.STOPPED
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREFS_KEY, State.STOPPED.name)
            .commit()
        AppLog.d(TAG, "state → STOPPED")

        // Stop foreground service + cancel watchdog
        MonitoringForegroundService.stop(context.applicationContext)
        ServiceWatchdog.cancel(context.applicationContext)
    }

    fun isActive(): Boolean = cached == State.ACTIVE

    fun state(): State = cached
}
