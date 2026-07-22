package com.example.midterm.data.model

/**
 * Immutable entity for a delivery address.
 *
 * MVVM Layer: This model lives in the data layer and is used by the
 * CheckoutViewModel to present shipping information. The UI observes
 * a StateFlow<CheckoutUiState> that contains this as part of its state.
 */
data class Address(
    val street: String,
    val ward: String,
    val district: String,
    val city: String,
    val isDefault: Boolean = false
)
