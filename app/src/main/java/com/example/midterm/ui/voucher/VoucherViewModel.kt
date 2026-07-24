package com.example.midterm.ui.voucher

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.data.repository.VoucherRepository
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
                seminarRepository.session
            ) { items, session ->
                val checkedItems = items.filter { it.isSelected }
                val subtotal: Long = checkedItems.sumOf { item ->
                    val unitPrice = item.product.price + (item.selectedVariant?.extraPrice ?: 0L)
                    unitPrice * item.quantity
                }
                subtotal to session.accessibilityMode
            }.collect { (subtotal, mode) ->
                val allProductVouchers = voucherRepository.getProductVouchers() +
                        unlockedHiddenVouchers.filter { it.type == VoucherType.PRODUCT }
                val allDeliveryVouchers = voucherRepository.getDeliveryVouchers() +
                        unlockedHiddenVouchers.filter { it.type == VoucherType.DELIVERY }

                updateState { current ->
                    val productSavings = computeVoucherSavings(current.appliedProductVoucher, subtotal, current.shippingFee)
                    val deliverySavings = computeVoucherSavings(current.appliedDeliveryVoucher, subtotal, current.shippingFee)
                    val totalSavings = productSavings + deliverySavings
                    val discountedTotal = (subtotal + current.shippingFee - totalSavings).coerceAtLeast(0L)

                    current.copy(
                        productVouchers = allProductVouchers.distinctBy { it.code },
                        deliveryVouchers = allDeliveryVouchers.distinctBy { it.code },
                        orderSubtotal = subtotal,
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

    fun redeemHiddenCode() {
        val current = _uiState.value
        val code = current.hiddenCodeInput.trim()
        if (code.isEmpty()) return

        val voucher = voucherRepository.getVoucherByCode(code)
        if (voucher == null) {
            updateState { it.copy(hiddenCodeError = true, codeSuccessMessage = null) }
            return
        }

        if (current.orderSubtotal < voucher.minSpend) {
            updateState { it.copy(hiddenCodeError = true, codeSuccessMessage = null) }
            return
        }

        if (voucher.isHidden && !unlockedHiddenVouchers.any { it.code.equals(voucher.code, ignoreCase = true) }) {
            unlockedHiddenVouchers.add(voucher)
        }

        val announcement = "Selected ${voucher.title.ifEmpty { voucher.code }}"
        applyVoucher(voucher, announcement)
        updateState {
            it.copy(
                hiddenCodeInput = "",
                hiddenCodeError = false,
                codeSuccessMessage = "Voucher ${voucher.code} applied successfully!"
            )
        }
    }

    /**
     * Handles voucher selection with strict category exclusivity:
     * - Selecting a new PRODUCT voucher replaces the previous PRODUCT voucher.
     * - Selecting a new DELIVERY voucher replaces the previous DELIVERY voucher.
     * - Tapping an already selected voucher toggles it off.
     */
    fun onVoucherSelected(voucher: Voucher) {
        val current = _uiState.value
        when (voucher.type) {
            VoucherType.PRODUCT -> {
                val isAlreadySelected = current.appliedProductVoucher?.code == voucher.code
                val newApplied = if (isAlreadySelected) null else voucher
                val announcement = if (isAlreadySelected) "Deselected ${voucher.title}" else "Selected ${voucher.title}"
                updateAppliedVouchers(productVoucher = newApplied, deliveryVoucher = current.appliedDeliveryVoucher, announcement = announcement)
            }
            VoucherType.DELIVERY -> {
                val isAlreadySelected = current.appliedDeliveryVoucher?.code == voucher.code
                val newApplied = if (isAlreadySelected) null else voucher
                val announcement = if (isAlreadySelected) "Deselected ${voucher.title}" else "Selected ${voucher.title}"
                updateAppliedVouchers(productVoucher = current.appliedProductVoucher, deliveryVoucher = newApplied, announcement = announcement)
            }
        }
    }

    fun clearAnnouncementMessage() {
        updateState { it.copy(announcementMessage = null) }
    }

    private fun applyVoucher(voucher: Voucher, announcement: String?) {
        val current = _uiState.value
        when (voucher.type) {
            VoucherType.PRODUCT -> updateAppliedVouchers(productVoucher = voucher, deliveryVoucher = current.appliedDeliveryVoucher, announcement = announcement)
            VoucherType.DELIVERY -> updateAppliedVouchers(productVoucher = current.appliedProductVoucher, deliveryVoucher = voucher, announcement = announcement)
        }
    }

    private fun updateAppliedVouchers(productVoucher: Voucher?, deliveryVoucher: Voucher?, announcement: String?) {
        val current = _uiState.value
        val productSavings = computeVoucherSavings(productVoucher, current.orderSubtotal, current.shippingFee)
        val deliverySavings = computeVoucherSavings(deliveryVoucher, current.orderSubtotal, current.shippingFee)
        val totalSavings = productSavings + deliverySavings
        val discountedTotal = (current.orderSubtotal + current.shippingFee - totalSavings).coerceAtLeast(0L)

        updateState {
            it.copy(
                appliedProductVoucher = productVoucher,
                appliedDeliveryVoucher = deliveryVoucher,
                totalSavings = totalSavings,
                discountedTotal = discountedTotal,
                announcementMessage = announcement
            )
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
}
