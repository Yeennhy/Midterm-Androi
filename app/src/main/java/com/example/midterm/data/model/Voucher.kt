package com.example.midterm.data.model

/**
 * Immutable entity for a discount voucher.
 *
 * @property type What the voucher discounts: the product subtotal or the delivery fee.
 * @property value Percent off (0-100) applied to the discounted amount.
 * @property title Short name shown as the card heading (e.g. "Standard Delivery").
 * @property description Secondary line under [title] (e.g. "Min. spend 100k • Cap 10k").
 * @property badgeText Text shown on the colored badge (e.g. "20% OFF", "FREE").
 * @property expiryLabel Display-only expiry/usage text (e.g. "EXPIRES 30 SEP", "ONE-TIME USE").
 * @property isHidden Secret vouchers are excluded from the visible voucher list on the
 *                     Discount page; they can only be unlocked by typing the exact [code]
 *                     into the "enter code" field.
 */
data class Voucher(
    val code: String,
    val type: VoucherType,
    val value: Int,
    val minSpend: Long,
    val title: String,
    val description: String,
    val badgeText: String,
    val expiryLabel: String,
    val expiryDate: Long = 0L,
    val isHidden: Boolean = false
)

enum class VoucherType {
    PRODUCT,
    DELIVERY
}
