package com.courierassist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.courierassist.capture.ScreenCaptureService
import com.courierassist.ui.theme.CourierAssistTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    // UI state
    private var isRunning by mutableStateOf(false)
    private var screenshotCount by mutableStateOf(0L)
    private var lastTimestamp by mutableStateOf("")

    // polls ScreenCaptureService companion fields every 500 ms
    private val uiHandler = Handler(Looper.getMainLooper())
    private val uiRefresher = object : Runnable {
        override fun run() {
            screenshotCount = ScreenCaptureService.screenshotCount
            val ts = ScreenCaptureService.lastTimestamp
            lastTimestamp = if (ts == 0L) "–"
            else SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(ts))
            isRunning = ScreenCaptureService.isCapturing
            if (isRunning) uiHandler.postDelayed(this, 500)
        }
    }

    // step 1: ask notification permission (Android 13+)
    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { requestMediaProjection() }

    // step 2: ask MediaProjection permission
    private val projectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            launchService(result.resultCode, result.data!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourierAssistTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    CaptureScreen(
                        modifier = Modifier.padding(padding),
                        isRunning = isRunning,
                        screenshotCount = screenshotCount,
                        lastTimestamp = lastTimestamp,
                        onStart = { handleStart() },
                        onStop = { handleStop() }
                    )
                }
            }
        }
    }

    // ------------------------------------------------------------------ logic

    private fun handleStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            requestMediaProjection()
        }
    }

    private fun requestMediaProjection() {
        val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        projectionLauncher.launch(mgr.createScreenCaptureIntent())
    }

    private fun launchService(resultCode: Int, data: Intent) {
        val intent = Intent(this, ScreenCaptureService::class.java).apply {
            action = ScreenCaptureService.ACTION_START
            putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, resultCode)
            putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, data)
        }
        startForegroundService(intent)
        isRunning = true
        uiHandler.post(uiRefresher)
    }

    private fun handleStop() {
        val intent = Intent(this, ScreenCaptureService::class.java).apply {
            action = ScreenCaptureService.ACTION_STOP
        }
        startService(intent)
        isRunning = false
        uiHandler.removeCallbacks(uiRefresher)
    }

    override fun onDestroy() {
        super.onDestroy()
        uiHandler.removeCallbacks(uiRefresher)
    }
}

// -------------------------------------------------------------------- UI

@Composable
fun CaptureScreen(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    screenshotCount: Long,
    lastTimestamp: String,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CourierAssist", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(24.dp))

        Text(
            text = if (isRunning) "CAPTURE RUNNING" else "STOPPED",
            style = MaterialTheme.typography.titleMedium,
            color = if (isRunning) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Text("Screenshots: $screenshotCount", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(4.dp))
        Text("Last frame: $lastTimestamp", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = if (isRunning) onStop else onStart,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (isRunning) "STOP" else "START",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
