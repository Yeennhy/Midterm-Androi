package com.example.midterm.data.repository

import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.source.LocalMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository holding available vouchers and providing validation logic.
 *
 * MVVM Design: The CheckoutViewModel asks VoucherRepository to validate
 * a code against the current order total. Validation logic lives here
 * rather than in the ViewModel to keep the ViewModel thin and testable.
 */
class VoucherRepository {

    private val vouchers: List<Voucher> = LocalMockData.vouchers

    private val _appliedProductVoucher = MutableStateFlow<Voucher?>(null)
    val appliedProductVoucher: StateFlow<Voucher?> = _appliedProductVoucher.asStateFlow()

    private val _appliedDeliveryVoucher = MutableStateFlow<Voucher?>(null)
    val appliedDeliveryVoucher: StateFlow<Voucher?> = _appliedDeliveryVoucher.asStateFlow()

    fun applyProductVoucher(voucher: Voucher?) {
        _appliedProductVoucher.value = voucher
    }

    fun applyDeliveryVoucher(voucher: Voucher?) {
        _appliedDeliveryVoucher.value = voucher
    }

    fun removeVoucher(type: VoucherType) {
        when (type) {
            VoucherType.PRODUCT -> _appliedProductVoucher.value = null
            VoucherType.DELIVERY -> _appliedDeliveryVoucher.value = null
        }
    }

    fun clearVouchers() {
        _appliedProductVoucher.value = null
        _appliedDeliveryVoucher.value = null
    }

    /** Product vouchers to render in the "Product discount" tab (hidden ones excluded). */
    fun getProductVouchers(): List<Voucher> =
        vouchers.filter { it.type == VoucherType.PRODUCT && !it.isHidden }

    /** Delivery vouchers to render in the "Delivery discount" tab (hidden ones excluded). */
    fun getDeliveryVouchers(): List<Voucher> =
        vouchers.filter { it.type == VoucherType.DELIVERY && !it.isHidden }

    /**
     * Looks up a voucher by exact code, searching the *entire* catalog, including
     * hidden ones. This is the only way a hidden voucher can ever be found — it is
     * intentionally left out of [getProductVouchers] / [getDeliveryVouchers].
     */
    fun getVoucherByCode(code: String): Voucher? =
        vouchers.find { it.code.equals(code.trim(), ignoreCase = true) }

    /**
     * Validates whether a voucher can be applied to the given [orderTotal].
     * Returns null if the code doesn't exist or the order doesn't meet minSpend.
     */
    fun validateVoucher(code: String, orderTotal: Long): Voucher? {
        val voucher = getVoucherByCode(code) ?: return null
        return if (orderTotal >= voucher.minSpend) voucher else null
    }
}
