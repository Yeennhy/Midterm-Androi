package com.example.midterm.ui.checkout

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Address
import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.data.model.Voucher

data class CheckoutUiState(
    val orderItems: List<CartItem> = emptyList(),
    val subtotal: Long = 0L,
    val discount: Long = 0L,
    val shippingFee: Long = 0L,
    val total: Long = 0L,
    val appliedVoucher: Voucher? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val shippingAddress: Address? = null,
    val isOrderConfirmed: Boolean = false,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
)
