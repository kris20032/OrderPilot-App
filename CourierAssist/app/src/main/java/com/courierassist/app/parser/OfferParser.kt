package com.courierassist.app.parser

import android.view.accessibility.AccessibilityNodeInfo
import com.courierassist.app.domain.Offer

interface OfferParser {
    fun canHandle(packageName: String): Boolean
    fun parse(rootNode: AccessibilityNodeInfo): Offer?
}
