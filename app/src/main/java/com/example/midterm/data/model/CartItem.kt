package com.example.midterm.data.model

/**
 * Represents a single row in the shopping cart.
 *
 * UDF Note: CartItem is an immutable snapshot of the cart state at a point in time.
 * The CartRepository manages the in-memory list; mutations create new copies
 * so that the ViewModel can detect changes via StateFlow emission.
 *
 * @property isSelected Tracks checkbox/selection state for batch operations.
 * @property quantity Number of units of this product in the cart.
 * @property selectedVariant The variant chosen for this line (e.g. "Red"), if the
 *                            product has variants. Two lines for the same product
 *                            with different variants are distinct cart rows.
 */
data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val isSelected: Boolean = true,
    val selectedVariant: ProductVariant? = null
)
