package com.example.midterm.ui.voucher

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Voucher

/**
 * UI State for the Voucher screen.
 */
data class VoucherUiState(
    val productVouchers: List<Voucher> = emptyList(),
    val deliveryVouchers: List<Voucher> = emptyList(),
    val appliedProductVoucher: Voucher? = null,
    val appliedDeliveryVoucher: Voucher? = null,
    val orderSubtotal: Long = 0L,
    val shippingFee: Long = 30000L,
    val totalSavings: Long = 0L,
    val discountedTotal: Long = 0L,
    val selectedTab: Int = 0,
    val hiddenCodeInput: String = "",
    val hiddenCodeError: Boolean = false,
    val codeSuccessMessage: String? = null,
    val announcementMessage: String? = null,
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
)
