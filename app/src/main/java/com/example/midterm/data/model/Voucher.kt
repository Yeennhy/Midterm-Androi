package com.example.midterm.data.model

/**
 * Immutable entity for a discount voucher.
 *
 * @property code Unique code identifier (e.g. "MUJI_ZEN_20", "SUMMER24").
 * @property type What the voucher discounts: [VoucherType.PRODUCT] or [VoucherType.DELIVERY].
 * @property value Percentage (0-100) or fixed value discount amount.
 * @property minSpend Minimum spend required (in Long VND) to apply this voucher.
 * @property color Color integer for visual theme accent.
 * @property expiryDate Expiry date timestamp.
 * @property isHidden Secret vouchers are excluded from the visible voucher list.
 * @property title Display title (e.g. "Storewide Special").
 * @property description Subtitle condition (e.g. "Min. spend 30k • Cap 50k").
 * @property expiryText Expiry string (e.g. "EXPIRES 30 SEP").
 * @property discountBadge Left badge text (e.g. "20% OFF", "FREE", "20K OFF").
 * @property isFixedValue True if [value] is a fixed VND amount rather than a percentage.
 */
data class Voucher(
    val code: String,
    val type: VoucherType,
    val value: Int,
    val minSpend: Long = 0L,
    val color: Int = 0,
    val expiryDate: Long = 0L,
    val isHidden: Boolean = false,
    val title: String = "",
    val description: String = "",
    val expiryText: String = "",
    val discountBadge: String = "",
    val isFixedValue: Boolean = false,
    val badgeText: String = "",
    val expiryLabel: String = ""
) {
    val effectiveBadgeText: String
        get() {
            if (badgeText.isNotEmpty()) return badgeText
            if (discountBadge.isNotEmpty()) return discountBadge
            return when {
                isFixedValue -> "${value / 1000}K OFF"
                type == VoucherType.DELIVERY && value == 100 -> "FREE"
                else -> "$value% OFF"
            }
        }

    val effectiveExpiryLabel: String
        get() = expiryLabel.ifEmpty { expiryText }
}

enum class VoucherType {
    PRODUCT,  // Percent/Product discount on cart subtotal
    DELIVERY  // Delivery/Shipping fee discount
}
