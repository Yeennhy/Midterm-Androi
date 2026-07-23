package com.example.midterm.ui.common

import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Dynamic accessibility extension functions for the "Break / Fix" seminar demo.
 *
 * ─────────────────────────────────────────────────────────────
 * ## Sections 3.1–3.2 — Blindness Demo (TalkBack)
 * ─────────────────────────────────────────────────────────────
 *
 * These helpers demonstrate four key TalkBack concepts:
 *
 * 1. **Focus Grouping** (`groupForAccessibility`)
 *    Makes a ViewGroup the single focusable unit. Children are hidden from
 *    TalkBack, so the screen-reader reads the parent's content description
 *    once instead of swiping through every child view. Used for product cards,
 *    profile tiles, and list items.
 *
 * 2. **Tree Pruning** (`removeAccessibilitySupportTree`)
 *    Sets `NO_HIDE_DESCENDANTS` on a container, removing the entire subtree
 *    from the accessibility tree. In BREAK mode this simulates how TalkBack
 *    becomes "lost" when developers forget to label containers.
 *
 * 3. **Live Regions** (`makeLiveRegion` / `announceForAccessibility`)
 *    Announces dynamic content changes without moving focus. Used to announce
 *    "Item added to cart" or "Voucher applied" while the user remains in place.
 *
 * 4. **Traversal Order** (`setAccessibilityTraversal`)
 *    Sets `accessibilityTraversalAfter` to enforce a logical reading order
 *    when the default XML order is incorrect (e.g., price before name).
 */
private fun View.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

// ─────────────────────────────────────────────────────────────
// 1. SINGLE-VIEW HELPERS (original Fix / Break)
// ─────────────────────────────────────────────────────────────

fun View.applyAccessibilitySupport(label: String, targetSizeDp: Int = 48) {
    val minPx = dpToPx(targetSizeDp)

    contentDescription = label
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    isFocusable = true

    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val lp = layoutParams as ViewGroup.MarginLayoutParams
        var changed = false
        if (lp.width != ViewGroup.LayoutParams.WRAP_CONTENT &&
            lp.width != ViewGroup.LayoutParams.MATCH_PARENT &&
            measuredWidth < minPx
        ) {
            lp.width = minPx
            changed = true
        }
        if (lp.height != ViewGroup.LayoutParams.WRAP_CONTENT &&
            lp.height != ViewGroup.LayoutParams.MATCH_PARENT &&
            measuredHeight < minPx
        ) {
            lp.height = minPx
            changed = true
        }
        if (changed) layoutParams = lp
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

fun View.removeAccessibilitySupport(shrinkTargetDp: Int = 24) {
    contentDescription = null
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    isFocusable = false

    val minPx = dpToPx(shrinkTargetDp)

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
        if (changed) layoutParams = lp
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

// ─────────────────────────────────────────────────────────────
// 2. FOCUS GROUPING (Report §3.2 — Cohesive announcements)
// ─────────────────────────────────────────────────────────────

/**
 * Turn this ViewGroup into a single focusable unit.
 *
 * TalkBack normally lets the user swipe through every focusable child.
 * For compound components (product cards, profile tiles), this is noisy.
 * Call this on the container to group children into one announcement.
 *
 * ## FIX (ACCESSIBLE)
 * ```kotlin
 * binding.productCard.groupForAccessibility(label = "Áo thun nam, 120.000₫, quantity 2")
 * ```
 * TalkBack reads: *"Áo thun nam, 120.000₫, quantity 2. Double tap to activate."*
 *
 * ## BREAK (INACCESSIBLE)
 * ```kotlin
 * binding.productCard.removeAccessibilitySupport()
 * ```
 * TalkBack skips the entire card — no announcement at all.
 */
fun ViewGroup.groupForAccessibility(label: String, targetSizeDp: Int = 48) {
    contentDescription = label
    isFocusable = true
    isClickable = true
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES

    val minPx = dpToPx(targetSizeDp)
    if (measuredWidth < minPx || measuredHeight < minPx) {
        minimumWidth = minOf(minPx, measuredWidth.coerceAtLeast(minPx))
        minimumHeight = minOf(minPx, measuredHeight.coerceAtLeast(minPx))
    }

    for (i in 0 until childCount) {
        getChildAt(i).importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }
}

// ─────────────────────────────────────────────────────────────
// 3. TREE PRUNING (§3.2 — TalkBack gets "lost")
// ─────────────────────────────────────────────────────────────

/**
 * Prune this container and ALL its descendants from the accessibility tree.
 *
 * In ACCESSIBLE mode, call this on non-essential decorative containers.
 * In INACCESSIBLE mode, call this on critical containers to demonstrate
 * how TalkBack becomes silent — the user swipes but hears nothing.
 *
 * This uses [IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS], which
 * removes the entire subtree in one call (unlike per-child removal).
 */
fun View.pruneFromAccessibilityTree() {
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
}

/**
 * Restore a pruned container back into the accessibility tree.
 */
fun View.restoreToAccessibilityTree() {
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
}

// ─────────────────────────────────────────────────────────────
// 4. LIVE REGIONS (§3.1 — Dynamic announcements)
// ─────────────────────────────────────────────────────────────

/**
 * Make this view a polite live region.
 *
 * When the view's text/content changes, TalkBack automatically reads
 * the new content without moving focus. Use this for status messages
 * like "Cart updated" or "Voucher applied".
 *
 * ## Usage
 * ```kotlin
 * binding.tvCartStatus.makeLiveRegion()
 * binding.tvCartStatus.text = "Added 1 item. Total: 120.000₫"
 * // TalkBack reads: "Added 1 item. Total: 120.000₫"
 * ```
 */
fun View.makeLiveRegion() {
    accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
}

/**
 * Immediately announce a message via TalkBack.
 *
 * Unlike [makeLiveRegion], this fires a one-time announcement
 * regardless of where the view lives in the tree.
 *
 * ## Usage
 * ```kotlin
 * view.postAnnouncement("Item removed from cart")
 * ```
 */
fun View.postAnnouncement(message: String) {
    announceForAccessibility(message)
}

// ─────────────────────────────────────────────────────────────
// 5. TRAVERSAL ORDER (§3.2 — Logical reading sequence)
// ─────────────────────────────────────────────────────────────

/**
 * Set the logical reading order by specifying which view comes before this one.
 *
 * When the XML layout order doesn't match the visual order (e.g., price column
 * before name column in a grid), TalkBack reads in XML order by default.
 * Use this to explicitly chain views in the correct sequence.
 *
 * ## Usage — chain three views in order:
 * ```kotlin
 * binding.tvProductName.setAccessibilityTraversal(null)           // first
 * binding.tvProductPrice.setAccessibilityTraversal(R.id.tvProductName) // second
 * binding.tvQuantity.setAccessibilityTraversal(R.id.tvProductPrice)    // third
 * ```
 *
 * TalkBack now reads: *"Name → Price → Quantity"*
 * regardless of XML ordering.
 *
 * @param afterResId Resource ID of the View that should be read BEFORE this one.
 *                   Pass `null` to make this the first element in the group.
 */
fun View.setAccessibilityTraversal(afterResId: Int?) {
    if (afterResId != null) {
        accessibilityTraversalAfter = afterResId
    }
}
