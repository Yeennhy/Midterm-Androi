package com.example.midterm.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Dynamic accessibility extension functions for the seminar demo.
 *
 * Safe accessibility helpers that modify contentDescription, focusability,
 * live regions, and semantic labels WITHOUT mutating layout parameters
 * (width/height) or hiding view visibilities.
 */
private fun View.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

// ─────────────────────────────────────────────────────────────
// 1. SINGLE-VIEW HELPERS
// ─────────────────────────────────────────────────────────────

fun View.applyAccessibilitySupport(label: String, targetSizeDp: Int = 48) {
    contentDescription = label
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    isFocusable = true

    val minPx = dpToPx(targetSizeDp)
    if (minimumWidth < minPx) minimumWidth = minPx
    if (minimumHeight < minPx) minimumHeight = minPx

    ViewCompat.setAccessibilityDelegate(this, object : androidx.core.view.AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
        }
    })
}

fun View.removeAccessibilitySupport(shrinkTargetDp: Int = 24) {
    contentDescription = null
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    isFocusable = false

    ViewCompat.setAccessibilityDelegate(this, null)
}

// ─────────────────────────────────────────────────────────────
// 2. FOCUS GROUPING
// ─────────────────────────────────────────────────────────────

fun ViewGroup.groupForAccessibility(label: String, targetSizeDp: Int = 48) {
    contentDescription = label
    isFocusable = true
    isClickable = true
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES

    val minPx = dpToPx(targetSizeDp)
    if (minimumWidth < minPx) minimumWidth = minPx
    if (minimumHeight < minPx) minimumHeight = minPx
}

// ─────────────────────────────────────────────────────────────
// 3. TREE PRUNING
// ─────────────────────────────────────────────────────────────

fun View.pruneFromAccessibilityTree() {
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
}

fun View.restoreToAccessibilityTree() {
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
}

// ─────────────────────────────────────────────────────────────
// 4. LIVE REGIONS
// ─────────────────────────────────────────────────────────────

fun View.makeLiveRegion() {
    accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
}

fun View.postAnnouncement(message: String) {
    announceForAccessibility(message)
}

// ─────────────────────────────────────────────────────────────
// 5. TRAVERSAL ORDER
// ─────────────────────────────────────────────────────────────

fun View.setAccessibilityTraversal(afterResId: Int?) {
    if (afterResId != null) {
        accessibilityTraversalAfter = afterResId
    }
}
