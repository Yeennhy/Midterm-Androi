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
 * ViewModel for the Discount screen.
 *
 * UDF Flow:
 *   UI Event (select voucher / redeem code) → VoucherViewModel → VoucherRepository →
 *   StateFlow emission → VoucherUiState update → UI re-render
 *
 * Manual DI: repositories are injected via constructor (ViewModelFactory).
 */
class VoucherViewModel(
    private val voucherRepository: VoucherRepository,
    private val cartRepository: CartRepository,
    private val seminarRepository: SeminarRepository
) : BaseViewModel<VoucherUiState>(VoucherUiState()) {

    init {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                seminarRepository.session
            ) { items, session ->
                val orderTotal = items
                    .filter { it.isSelected }
                    .sumOf { it.product.price * it.quantity }
                orderTotal to session.accessibilityMode
            }.collect { (orderTotal, mode) ->
                updateState {
                    it.copy(
                        productVouchers = voucherRepository.getProductVouchers(),
                        deliveryVouchers = voucherRepository.getDeliveryVouchers(),
                        orderTotal = orderTotal,
                        totalSavings = computeSavings(it.appliedProductVoucher, it.appliedDeliveryVoucher, orderTotal),
                        accessibilityMode = mode
                    )
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        updateState { it.copy(selectedTab = index) }
    }

    /** Applies a voucher tapped directly from the product/delivery list. */
    fun onVoucherSelected(voucher: Voucher) {
        applyVoucher(voucher)
    }

    /** Clears whichever voucher slot matches [type], leaving the other type's selection intact. */
    fun removeVoucher(type: VoucherType) {
        updateState {
            val next = when (type) {
                VoucherType.PRODUCT -> it.copy(appliedProductVoucher = null)
                VoucherType.DELIVERY -> it.copy(appliedDeliveryVoucher = null)
            }
            next.copy(
                totalSavings = computeSavings(next.appliedProductVoucher, next.appliedDeliveryVoucher, next.orderTotal)
            )
        }
    }

    fun onHiddenCodeInputChanged(text: String) {
        updateState { it.copy(hiddenCodeInput = text, hiddenCodeError = false) }
    }

    /**
     * Redeems whatever code is currently typed in [VoucherUiState.hiddenCodeInput].
     * This is the only path that can unlock a hidden voucher, since
     * [VoucherRepository.getVoucherByCode] searches the full catalog regardless
     * of [Voucher.isHidden] — the code must match exactly.
     *
     * An empty/blank input is not an error — Apply is a no-op until the user actually
     * types something, rather than immediately flagging "invalid code".
     */
    fun redeemHiddenCode() {
        val current = _uiState.value
        if (current.hiddenCodeInput.isBlank()) {
            updateState { it.copy(hiddenCodeError = false) }
            return
        }
        val validated = voucherRepository.validateVoucher(current.hiddenCodeInput, current.orderTotal)
        if (validated == null) {
            updateState { it.copy(hiddenCodeError = true) }
        } else {
            applyVoucher(validated)
            updateState { it.copy(hiddenCodeInput = "", hiddenCodeError = false) }
        }
    }

    /** Applies [voucher] into its type's slot — PRODUCT and DELIVERY slots never clobber each other. */
    private fun applyVoucher(voucher: Voucher) {
        updateState {
            val next = when (voucher.type) {
                VoucherType.PRODUCT -> it.copy(appliedProductVoucher = voucher)
                VoucherType.DELIVERY -> it.copy(appliedDeliveryVoucher = voucher)
            }
            next.copy(
                totalSavings = computeSavings(next.appliedProductVoucher, next.appliedDeliveryVoucher, next.orderTotal)
            )
        }
    }

    private fun computeSavings(productVoucher: Voucher?, deliveryVoucher: Voucher?, orderTotal: Long): Long {
        val productSavings = productVoucher
            ?.takeIf { orderTotal >= it.minSpend }
            ?.let { orderTotal * it.value / 100 }
            ?: 0L
        val deliverySavings = deliveryVoucher
            ?.takeIf { orderTotal >= it.minSpend }
            ?.let { LocalMockData.DEFAULT_SHIPPING_FEE }
            ?: 0L
        return productSavings + deliverySavings
    }
}
