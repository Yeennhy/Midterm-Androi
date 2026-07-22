package com.example.midterm.data.model

/**
 * Immutable entity for a discount or shipping voucher.
 *
 * Accessibility Demo: The [color] field is used to demonstrate color-only
 * dependent information that becomes inaccessible when AccessibilityHelper
 * removes color context.
 *
 * @property type PERCENT for percentage discount, SHIPPING for free shipping.
 * @property value Discount amount (percentage or flat amount in VND).
 * @property minSpend Minimum order total required to use this voucher (VND Long).
 * @property color A color resource int used in the demo to show color-only cues.
 */
data class Voucher(
    val code: String,
    val type: VoucherType,
    val value: Int,
    val minSpend: Long,
    val color: Int
)

enum class VoucherType {
    PERCENT,
    SHIPPING
}
