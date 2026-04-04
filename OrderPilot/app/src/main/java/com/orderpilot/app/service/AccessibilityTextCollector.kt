package com.orderpilot.app.service

import android.view.accessibility.AccessibilityNodeInfo
import com.orderpilot.app.di.AppLog

object AccessibilityTextCollector {

    private const val MAX_DEPTH = 30

    fun collectText(root: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        traverseNode(root, sb, 0)
        return sb.toString()
    }

    private fun traverseNode(node: AccessibilityNodeInfo, sb: StringBuilder, depth: Int) {
        if (depth > MAX_DEPTH) return
        node.text?.let { text ->
            if (text.isNotBlank()) sb.append(text).append('\n')
        }
        node.contentDescription?.let { desc ->
            if (desc.isNotBlank()) sb.append(desc).append('\n')
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                try {
                    traverseNode(child, sb, depth + 1)
                } finally {
                    child.recycle()
                }
            }
        }
    }
}
