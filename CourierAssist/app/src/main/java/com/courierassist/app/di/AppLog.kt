package com.courierassist.app.di

import android.util.Log

object AppLog {

    const val TAG_PIPELINE = "CA_Pipeline"
    const val TAG_CAPTURE  = "CA_Capture"
    const val TAG_OCR      = "CA_OCR"
    const val TAG_PARSER   = "CA_Parser"
    const val TAG_OVERLAY  = "CA_Overlay"
    const val TAG_SERVICE  = "CA_Service"

    fun d(tag: String, msg: String) = Log.d(tag, msg)
    fun w(tag: String, msg: String) = Log.w(tag, msg)
    fun e(tag: String, msg: String, throwable: Throwable? = null) = Log.e(tag, msg, throwable)
}
