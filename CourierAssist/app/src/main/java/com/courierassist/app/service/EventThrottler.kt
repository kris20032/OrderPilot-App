package com.courierassist.app.service

import com.courierassist.app.di.AppLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventThrottler(
    private val firstShotDelayMs: Long = 100L,
    private val cooldownMs: Long = 1500L
) {
    private var lastTriggerTime = 0L
    private var pendingJob: Job? = null

    fun onEvent(scope: CoroutineScope, action: suspend () -> Unit) {
        val now = System.currentTimeMillis()
        val sinceLastTrigger = now - lastTriggerTime
        if (sinceLastTrigger < cooldownMs) {
            AppLog.d(AppLog.TAG_SERVICE, "Throttler: cooldown (${sinceLastTrigger}ms < ${cooldownMs}ms)")
            return
        }
        if (pendingJob?.isActive == true) {
            AppLog.d(AppLog.TAG_SERVICE, "Throttler: job still active, skipping")
            return
        }

        pendingJob = scope.launch {
            delay(firstShotDelayMs)
            lastTriggerTime = System.currentTimeMillis()
            AppLog.d(AppLog.TAG_SERVICE, "Event triggered")
            action()
        }
    }
}