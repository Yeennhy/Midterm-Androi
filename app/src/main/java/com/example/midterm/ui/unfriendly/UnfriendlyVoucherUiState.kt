package com.example.midterm.ui.unfriendly

import com.example.midterm.data.model.Voucher

data class UnfriendlyVoucherUiState(
    val vouchers: List<Voucher> = emptyList(),
    val selectedProductVoucher: Voucher? = null,
    val selectedDeliveryVoucher: Voucher? = null,
    val orderTotal: Long = 0L,
    val subtotal: Long = 0L,
    val totalSavings: Long = 0L,
    val discountedTotal: Long = 0L,
    val hiddenCodeInput: String = "",
    val hiddenCodeError: Boolean = false
)
