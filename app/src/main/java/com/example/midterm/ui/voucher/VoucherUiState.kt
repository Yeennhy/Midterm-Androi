package com.example.midterm.ui.voucher

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Voucher

/**
 * UI state for the Discount screen.
 *
 * UDF Contract:
 * - Immutable snapshot emitted by VoucherViewModel via StateFlow.
 * - VoucherActivity reads and renders — never mutates directly.
 * - User actions (select voucher, redeem code) go to VoucherViewModel.
 *
 * @property productVouchers Visible vouchers for the "Product discount" tab.
 * @property deliveryVouchers Visible vouchers for the "Delivery discount" tab.
 * @property appliedVoucher Currently applied voucher, visible or hidden.
 * @property selectedTab 0 = product tab, 1 = delivery tab.
 * @property hiddenCodeInput Text currently typed into the "enter code" field.
 * @property hiddenCodeError True when the last submitted code didn't match any voucher.
 * @property totalSavings Amount the applied voucher saves against [orderTotal].
 */
data class VoucherUiState(
    val productVouchers: List<Voucher> = emptyList(),
    val deliveryVouchers: List<Voucher> = emptyList(),
    val appliedVoucher: Voucher? = null,
    val orderTotal: Long = 0L,
    val selectedTab: Int = 0,
    val hiddenCodeInput: String = "",
    val hiddenCodeError: Boolean = false,
    val totalSavings: Long = 0L,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
)
