package com.example.midterm.ui.voucher

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.data.repository.VoucherRepository
import com.example.midterm.data.source.LocalMockData
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel for the Discount / Voucher screen.
 *
 * Exclusivity rule:
 * Users can apply at most ONE product voucher AND ONE delivery voucher simultaneously.
 */
class VoucherViewModel(
    private val voucherRepository: VoucherRepository,
    private val cartRepository: CartRepository,
    private val seminarRepository: SeminarRepository
) : BaseViewModel<VoucherUiState>(VoucherUiState()) {

    private val unlockedHiddenVouchers = mutableListOf<Voucher>()

    init {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                voucherRepository.appliedProductVoucher,
                voucherRepository.appliedDeliveryVoucher,
                seminarRepository.session
            ) { items, appliedProduct, appliedDelivery, session ->
                val selectedItems = items.filter { it.isSelected }
                val subtotal: Long = selectedItems.sumOf { item ->
                    val unitPrice = item.product.price + (item.selectedVariant?.extraPrice ?: 0L)
                    unitPrice * item.quantity
                }
                val baseShippingFee = LocalMockData.DEFAULT_SHIPPING_FEE
                Quadruple(subtotal, baseShippingFee, appliedProduct to appliedDelivery, session.accessibilityMode)
            }.collect { (subtotal, baseShippingFee, appliedPair, mode) ->
                val (appliedProduct, appliedDelivery) = appliedPair

                val allProductVouchers = voucherRepository.getProductVouchers() +
                        unlockedHiddenVouchers.filter { it.type == VoucherType.PRODUCT }
                val allDeliveryVouchers = voucherRepository.getDeliveryVouchers() +
                        unlockedHiddenVouchers.filter { it.type == VoucherType.DELIVERY }

                val productSavings = computeVoucherSavings(appliedProduct, subtotal, baseShippingFee)
                val deliverySavings = computeVoucherSavings(appliedDelivery, subtotal, baseShippingFee)
                val totalSavings = productSavings + deliverySavings
                val finalShipping = (baseShippingFee - deliverySavings).coerceAtLeast(0L)
                val discountedTotal = (subtotal + finalShipping - productSavings).coerceAtLeast(0L)

                updateState { current ->
                    current.copy(
                        productVouchers = allProductVouchers.distinctBy { it.code },
                        deliveryVouchers = allDeliveryVouchers.distinctBy { it.code },
                        appliedProductVoucher = appliedProduct,
                        appliedDeliveryVoucher = appliedDelivery,
                        orderSubtotal = subtotal,
                        shippingFee = finalShipping,
                        totalSavings = totalSavings,
                        discountedTotal = discountedTotal,
                        accessibilityMode = mode
                    )
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        updateState { it.copy(selectedTab = index) }
    }

    fun onCodeInputChanged(text: String) {
        updateState { it.copy(hiddenCodeInput = text, hiddenCodeError = false, codeSuccessMessage = null) }
    }

    fun onHiddenCodeInputChanged(text: String) {
        onCodeInputChanged(text)
    }

    fun redeemHiddenCode() {
        val current = _uiState.value
        val code = current.hiddenCodeInput.trim()
        if (code.isBlank()) {
            updateState { it.copy(hiddenCodeError = false) }
            return
        }

        val voucher = voucherRepository.getVoucherByCode(code)
        if (voucher == null || current.orderSubtotal < voucher.minSpend) {
            updateState { it.copy(hiddenCodeError = true, codeSuccessMessage = null) }
            return
        }

        if (voucher.isHidden && !unlockedHiddenVouchers.any { it.code.equals(voucher.code, ignoreCase = true) }) {
            unlockedHiddenVouchers.add(voucher)
        }

        applyVoucher(voucher)
        updateState {
            it.copy(
                hiddenCodeInput = "",
                hiddenCodeError = false,
                codeSuccessMessage = "Voucher ${voucher.code} applied successfully!",
                announcementMessage = "Selected ${voucher.title.ifEmpty { voucher.code }}"
            )
        }
    }

    fun onVoucherSelected(voucher: Voucher) {
        val current = _uiState.value
        when (voucher.type) {
            VoucherType.PRODUCT -> {
                val isAlreadySelected = current.appliedProductVoucher?.code == voucher.code
                if (isAlreadySelected) {
                    voucherRepository.removeVoucher(VoucherType.PRODUCT)
                    updateState { it.copy(announcementMessage = "Deselected ${voucher.title}") }
                } else {
                    voucherRepository.applyProductVoucher(voucher)
                    updateState { it.copy(announcementMessage = "Selected ${voucher.title}") }
                }
            }
            VoucherType.DELIVERY -> {
                val isAlreadySelected = current.appliedDeliveryVoucher?.code == voucher.code
                if (isAlreadySelected) {
                    voucherRepository.removeVoucher(VoucherType.DELIVERY)
                    updateState { it.copy(announcementMessage = "Deselected ${voucher.title}") }
                } else {
                    voucherRepository.applyDeliveryVoucher(voucher)
                    updateState { it.copy(announcementMessage = "Selected ${voucher.title}") }
                }
            }
        }
    }

    fun removeVoucher(type: VoucherType) {
        voucherRepository.removeVoucher(type)
    }

    fun clearAnnouncementMessage() {
        updateState { it.copy(announcementMessage = null) }
    }

    private fun applyVoucher(voucher: Voucher) {
        when (voucher.type) {
            VoucherType.PRODUCT -> voucherRepository.applyProductVoucher(voucher)
            VoucherType.DELIVERY -> voucherRepository.applyDeliveryVoucher(voucher)
        }
    }

    private fun computeVoucherSavings(voucher: Voucher?, subtotal: Long, shippingFee: Long): Long {
        voucher ?: return 0L
        if (subtotal < voucher.minSpend) return 0L

        return when (voucher.type) {
            VoucherType.PRODUCT -> {
                if (voucher.isFixedValue) {
                    voucher.value.toLong().coerceAtMost(subtotal)
                } else {
                    subtotal * voucher.value / 100
                }
            }
            VoucherType.DELIVERY -> {
                if (voucher.isFixedValue) {
                    voucher.value.toLong().coerceAtMost(shippingFee)
                } else {
                    shippingFee * voucher.value / 100
                }
            }
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
