package com.example.midterm.ui.cart

import com.example.midterm.data.model.CartItem

/**
 * UI state for the Cart screen.
 *
 * Shared by both Friendly and Unfriendly Cart screens.
 * Immutable state exposed by CartViewModel.
 */
data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Long = 0L,
    val shippingFee: Long = 30_000L,
    val discount: Long = 0L,
    val totalPrice: Long = 0L,
    val selectedCount: Int = 0,
    val isSelectAll: Boolean = false,
    val voucherProductCode: String = "",
    val voucherProductDiscount: Long = 0L,
    val voucherShippingCode: String = "",
    val voucherShippingDiscount: Long = 0L,
    val initTotal: Long = 0L
)
