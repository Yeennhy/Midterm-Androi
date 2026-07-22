package com.example.midterm.ui.voucher

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Voucher

/**
 * UI state for the Voucher screen.
 *
 * UDF Contract:
 * - Immutable snapshot emitted by VoucherViewModel via StateFlow.
 * - VoucherActivity reads and renders — never mutates directly.
 * - User actions (select voucher, apply) go to VoucherViewModel.
 */
data class VoucherUiState(
    val vouchers: List<Voucher> = emptyList(),
    val appliedVoucher: Voucher? = null,
    val orderTotal: Long = 0L,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE,
    val selectedTab: Int = 0,
    val availableVouchers: List<Voucher> = emptyList(),
    val totalSavings: Long = 0L
)
