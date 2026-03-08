package com.courierassist.app.overlay

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverlayAutoHider(
    private val overlayManager: OverlayManager,
    private val onHidden: () -> Unit = {}
) {
    private var hideJob: Job? = null

    fun onOverlayShown(scope: CoroutineScope, hideDelayMs: Long = 15_000L) {
        hideJob?.cancel()
        hideJob = scope.launch {
            delay(hideDelayMs)
            withContext(Dispatchers.Main) { overlayManager.hide() }
            onHidden()
        }
    }

    fun hideNow(scope: CoroutineScope) {
        hideJob?.cancel()
        scope.launch(Dispatchers.Main) {
            overlayManager.hide()
            onHidden()
        }
    }
}