package com.example.midterm.data.repository

import com.example.midterm.data.model.Voucher
import com.example.midterm.data.source.LocalMockData

/**
 * Repository holding available vouchers and providing validation logic.
 *
 * MVVM Design: The CheckoutViewModel asks VoucherRepository to validate
 * a code against the current order total. Validation logic lives here
 * rather than in the ViewModel to keep the ViewModel thin and testable.
 */
class VoucherRepository {

    private val vouchers: List<Voucher> = LocalMockData.vouchers

    fun getVouchers(): List<Voucher> = vouchers

    fun getVoucherByCode(code: String): Voucher? = vouchers.find { it.code == code }

    /**
     * Validates whether a voucher can be applied to the given [orderTotal].
     * Returns null if invalid, or the Voucher if it passes minSpend check.
     */
    fun validateVoucher(code: String, orderTotal: Long): Voucher? {
        val voucher = getVoucherByCode(code) ?: return null
        return if (orderTotal >= voucher.minSpend) voucher else null
    }
}
