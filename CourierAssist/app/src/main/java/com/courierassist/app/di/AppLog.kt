package com.courierassist.app.di

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLog {

    const val TAG_PIPELINE = "CA_Pipeline"
    const val TAG_CAPTURE  = "CA_Capture"
    const val TAG_OCR      = "CA_OCR"
    const val TAG_PARSER   = "CA_Parser"
    const val TAG_OVERLAY  = "CA_Overlay"
    const val TAG_SERVICE  = "CA_Service"

    private const val BUFFER_SIZE = 500
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
