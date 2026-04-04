package com.orderpilot.app.parser

import android.view.accessibility.AccessibilityNodeInfo
import com.orderpilot.app.domain.Offer

interface OfferParser {
    fun canHandle(packageName: String): Boolean
    fun parse(rootNode: AccessibilityNodeInfo): Offer?
}
