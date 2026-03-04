package com.courierassist.app.service

import com.courierassist.app.di.AppLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventThrottler(
    private val firstShotDelayMs: Long = 300L,
    private val cooldownMs: Long = 5000L
) {
    private var lastTriggerTime = 0L
    private var pendingJob: Job? = null

    fun onEvent(scope: CoroutineScope, action: () -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastTriggerTime < cooldownMs) return
        if (pendingJob?.isActive == true) return

        pendingJob = scope.launch {
            delay(firstShotDelayMs)
            lastTriggerTime = System.currentTimeMillis()
            AppLog.d(AppLog.TAG_SERVICE, "Event triggered")
            action()
        }
    }
}