package com.example.midterm.data.model

/**
 * Immutable entity for a discount or delivery voucher.
 */
data class Voucher(
    val code: String,
    val type: VoucherType,
    val value: Int,
    val minSpend: Long,
    val color: Int,
    val expiryDate: Long = 0L
)

enum class VoucherType {
    PERCENT,
    SHIPPING
}
