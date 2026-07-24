package com.example.midterm.ui.cart

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.CartItem

/**
 * UI state for the Cart screen.
 */
data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Long = 0L,
    val selectedCount: Int = 0,
    val isAllSelected: Boolean = true,
    val isCheckoutEnabled: Boolean = false,
    val selectedCartItemForVariant: CartItem? = null,
    val accessibilityAnnouncement: String? = null,
    val errorMessage: String? = null,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
)
