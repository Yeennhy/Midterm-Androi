package com.example.midterm.data.model

/**
 * Immutable entity representing a payment method option.
 *
 * @property iconResId Drawable resource for the payment icon (e.g., card logo).
 * @property description Human-readable detail (e.g., "Ending in **4242" for cards).
 */
data class PaymentMethod(
    val id: String,
    val name: String,
    val iconResId: Int,
    val description: String
)
