package com.courierassist.app.service

import android.view.accessibility.AccessibilityNodeInfo
import com.courierassist.app.di.AppLog

object AccessibilityTextCollector {

    fun collectText(root: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        traverseNode(root, sb)
        return sb.toString()
    }

    private fun traverseNode(node: AccessibilityNodeInfo, sb: StringBuilder) {
        node.text?.let { text ->
            if (text.isNotBlank()) sb.append(text).append('\n')
        }
        node.contentDescription?.let { desc ->
            if (desc.isNotBlank()) sb.append(desc).append('\n')
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                traverseNode(child, sb)
                child.recycle()
            }
        }
    }
}
