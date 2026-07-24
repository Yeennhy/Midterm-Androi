package com.example.midterm.ui.checkout

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Address
import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.data.model.Voucher

data class CheckoutUiState(
    val orderItems: List<CartItem> = emptyList(),
    val itemsCount: Int = 0,
    val rawItemsPrice: Long = 0L,
    val subtotal: Long = 0L,
    val productDiscount: Long = 0L,
    val deliveryDiscount: Long = 0L,
    val totalDiscount: Long = 0L,
    val shippingFee: Long = 0L,
    val total: Long = 0L,
    val appliedProductVoucher: Voucher? = null,
    val appliedDeliveryVoucher: Voucher? = null,
    val appliedVoucher: Voucher? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val shippingAddress: Address? = null,
    val isEditingAddress: Boolean = false,
    val isOrderConfirmed: Boolean = false,
    val isOrderSuccess: Boolean = false,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
)
