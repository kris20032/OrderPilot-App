package com.orderpilot.app.di

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLog {

    const val TAG_PIPELINE = "OP_Pipeline"
    const val TAG_CAPTURE  = "OP_Capture"
    const val TAG_OCR      = "OP_OCR"
    const val TAG_PARSER   = "OP_Parser"
    const val TAG_OVERLAY  = "OP_Overlay"
    const val TAG_SERVICE  = "OP_Service"
    const val TAG_MAIN     = "OP_Main"

    private const val BUFFER_SIZE = 2000
    private val buffer = ArrayDeque<String>(BUFFER_SIZE)
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
        addToBuffer("D", tag, msg)
    }

    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
        addToBuffer("W", tag, msg)
    }

    fun e(tag: String, msg: String, throwable: Throwable? = null) {
        Log.e(tag, msg, throwable)
        addToBuffer("E", tag, "$msg${throwable?.let { " | ${it.message}" } ?: ""}")
    }

    @Synchronized
    private fun addToBuffer(level: String, tag: String, msg: String) {
        val time = dateFormat.format(Date())
        val entry = "$time $level/$tag: $msg"
        if (buffer.size >= BUFFER_SIZE) buffer.removeFirst()
        buffer.addLast(entry)
    }

    @Synchronized
    fun getBufferedLogs(): String = buffer.joinToString("\n")
}
