package com.example.midterm.data.model

/**
 * A selectable option for a product (e.g. color), listed in the variant picker menu.
 *
 * @property extraPrice Added on top of the product's base price when this variant
 *                       is selected (0 if the variant doesn't change the price).
 * @property colorHex Optional swatch color for the picker menu (e.g. a color dot
 *                     next to "Red"). Null for non-color variants (e.g. sizes).
 */
data class ProductVariant(
    val id: String,
    val name: String,
    val extraPrice: Long = 0L,
    val colorHex: Int? = null
)
