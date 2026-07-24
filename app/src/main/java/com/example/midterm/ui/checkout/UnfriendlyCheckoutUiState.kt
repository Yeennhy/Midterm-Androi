package com.example.midterm.ui.checkout

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Address
import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.Voucher

data class UnfriendlyCheckoutUiState(
    val orderItems: List<CartItem> = emptyList(),
    val itemCount: Int = 0,
    val subtotal: Long = 0L,
    val discount: Long = 0L,
    val shippingFee: Long = 0L,
    val total: Long = 0L,
    val voucherProductCode: String= "",
    val voucherProductDiscount: Long = 0L,
    val voucherShippingCode: String= "",
    val voucherShippingDiscount: Long = 0L,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.NONE,
    val shippingAddress: String="",
    val isEditingAddress: Boolean = false,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.INACCESSIBLE
)
enum class PaymentMethod { CARD, EWALLET, BANK, CASH, NONE }