package com.example.midterm.data.model

/**
 * Immutable entity for a discount voucher.
 *
 * @property type What the voucher discounts: the product subtotal or the delivery fee.
 * @property value Percent off (0-100) applied to the discounted amount.
 * @property isHidden Secret vouchers are excluded from the visible voucher list on the
 *                     Discount page; they can only be unlocked by typing the exact [code]
 *                     into the "enter code" field.
 */
data class Voucher(
    val code: String,
    val type: VoucherType,
    val value: Int,
    val minSpend: Long,
    val color: Int,
    val expiryDate: Long = 0L,
    val isHidden: Boolean = false
)

enum class VoucherType {
    PRODUCT,
    DELIVERY
}
