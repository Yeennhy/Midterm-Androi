package com.example.midterm.data.model

/**
 * Immutable entity representing a payment method option.
 *
 * Used in the checkout flow. The CheckoutViewModel exposes available
 * methods via StateFlow; the user selection updates the UI state.
 */
data class PaymentMethod(
    val id: String,
    val name: String,
    val iconResId: Int,
    val description: String
)
