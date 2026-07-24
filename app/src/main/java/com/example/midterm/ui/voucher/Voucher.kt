package com.example.midterm.ui.voucher

import com.example.midterm.data.model.Voucher

/**
 * Extension property to support fixed-value vouchers without modifying the original class.
 */
val Voucher.isFixedValue: Boolean
    get() = badgeText.contains("K OFF", ignoreCase = true)
