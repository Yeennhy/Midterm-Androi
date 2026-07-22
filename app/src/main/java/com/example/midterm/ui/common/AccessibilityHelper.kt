package com.example.midterm.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Dynamic accessibility extension functions for the "Break / Fix" seminar demo.
 *
 * ─────────────────────────────────────────────────────────────
 * ## How it works
 * ─────────────────────────────────────────────────────────────
 *
 * The seminar has two global modes controlled by [SeminarSession.accessibilityMode]:
 *
 * - **ACCESSIBLE** (Fix): View metadata is set correctly for screen-readers.
 *   Content descriptions are applied, touch targets meet the 48dp minimum,
 *   and elements are focusable.
 *
 * - **INACCESSIBLE** (Break): Deliberately strips all accessibility metadata,
 *   shrinks touch targets, and hides elements from screen-readers to simulate
 *   an inaccessible app.
 *
 * ## UDF Integration
 * The MainViewModel observes [SeminarSession] from SeminarRepository.
 * When the mode toggles, it iterates over all relevant Views and calls
 * `applyAccessibilitySupport()` or `removeAccessibilitySupport()` accordingly.
 */

/**
 * Apply proper accessibility metadata to this View.
 *
 * This is the "Fix" operation. It ensures:
 * 1. A meaningful [contentDescription] for screen-readers (TalkBack).
 * 2. A minimum touch target of [targetSizeDp] (default 48dp per Material Design).
 * 3. The View is focusable and visible to accessibility services.
 *
 * @param label The content description string for screen-readers.
 * @param targetSizeDp Minimum touch target size in density-independent pixels.
 */
fun View.applyAccessibilitySupport(label: String, targetSizeDp: Int = 48) {
    val density = resources.displayMetrics.density
    val minPx = (targetSizeDp * density).toInt()

    contentDescription = label
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    isFocusable = true

    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val lp = layoutParams as ViewGroup.MarginLayoutParams
        var changed = false

        if (measuredWidth < minPx) {
            lp.width = minPx
            changed = true
        }
        if (measuredHeight < minPx) {
            lp.height = minPx
            changed = true
        }
        if (changed) {
            layoutParams = lp
        }
    }

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

/**
 * Strip all accessibility metadata from this View.
 *
 * This is the "Break" operation. It deliberately:
 * 1. Clears the content description, so TalkBack reads nothing.
 * 2. Hides the View from screen-readers entirely.
 * 3. Reduces touch target to a minimum (simulating small, hard-to-tap elements).
 *
 * @param shrinkTargetDp The reduced touch target size (default 24dp, half the
 *                       recommended minimum).
 */
fun View.removeAccessibilitySupport(shrinkTargetDp: Int = 24) {
    contentDescription = null
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    isFocusable = false

    val density = resources.displayMetrics.density
    val minPx = (shrinkTargetDp * density).toInt()

    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val lp = layoutParams as ViewGroup.MarginLayoutParams
        var changed = false

        if (layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT &&
            layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT &&
            measuredWidth > minPx
        ) {
            lp.width = minPx
            changed = true
        }
        if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT &&
            layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT &&
            measuredHeight > minPx
        ) {
            lp.height = minPx
            changed = true
        }
        if (changed) {
            layoutParams = lp
        }
    }

    ViewCompat.setAccessibilityDelegate(this, object : androidx.core.view.AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            info.setVisibleToUser(false)
        }
    })
}
