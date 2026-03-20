package com.courierassist.app.overlay

import com.courierassist.app.domain.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class OverlayAutoHider(
    private val overlayManager: OverlayManager,
    private val onHidden: () -> Unit = {}
) {
    private val hideJobs = ConcurrentHashMap<Platform, Job>()

    fun onOverlayShown(scope: CoroutineScope, hideDelayMs: Long = 15_000L, platform: Platform) {
        hideJobs[platform]?.cancel()
        hideJobs[platform] = scope.launch {
            delay(hideDelayMs)
            withContext(Dispatchers.Main) { overlayManager.hideByPlatform(platform) }
            hideJobs.remove(platform)
            onHidden()
        }
    }

    fun hideNow(scope: CoroutineScope) {
        hideJobs.values.forEach { it.cancel() }
        hideJobs.clear()
        scope.launch(Dispatchers.Main) {
            overlayManager.hide()
            onHidden()
        }
    }
}
