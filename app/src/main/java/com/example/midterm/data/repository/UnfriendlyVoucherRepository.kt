package com.example.midterm.data.repository

import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.source.LocalMockData

class UnfriendlyVoucherRepository {

    private val vouchers: List<Voucher> = LocalMockData.vouchers

    fun getAllVouchers(): List<Voucher> = vouchers.filter { !it.isHidden }

    fun getVoucherByCode(code: String): Voucher? =
        vouchers.find { it.code.equals(code.trim(), ignoreCase = true) }

    fun validateVoucher(code: String, orderTotal: Long): Voucher? {
        val voucher = getVoucherByCode(code) ?: return null
        return if (orderTotal >= voucher.minSpend) voucher else null
    }
}
