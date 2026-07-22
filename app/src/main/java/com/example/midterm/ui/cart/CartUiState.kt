package com.example.midterm.ui.cart

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.CartItem

/**
 * UI state for the Cart screen.
 *
 * UDF Contract:
 * - Immutable snapshot emitted by CartViewModel via StateFlow.
 * - CartActivity reads and renders — never mutates directly.
 * - User actions (add, remove, toggle) go to CartViewModel as function calls.
 */
data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Long = 0L,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
)
