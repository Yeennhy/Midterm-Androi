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
                        totalSavings = computeSavings(it.appliedVoucher, orderTotal),
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

    fun removeVoucher() {
        updateState { it.copy(appliedVoucher = null, totalSavings = 0L) }
    }

    fun onHiddenCodeInputChanged(text: String) {
        updateState { it.copy(hiddenCodeInput = text, hiddenCodeError = false) }
    }

    /**
     * Redeems whatever code is currently typed in [VoucherUiState.hiddenCodeInput].
     * This is the only path that can unlock a hidden voucher, since
     * [VoucherRepository.getVoucherByCode] searches the full catalog regardless
     * of [Voucher.isHidden] — the code must match exactly.
     */
    fun redeemHiddenCode() {
        val current = _uiState.value
        val validated = voucherRepository.validateVoucher(current.hiddenCodeInput, current.orderTotal)
        if (validated == null) {
            updateState { it.copy(hiddenCodeError = true) }
        } else {
            applyVoucher(validated)
            updateState { it.copy(hiddenCodeInput = "", hiddenCodeError = false) }
        }
    }

    private fun applyVoucher(voucher: Voucher) {
        updateState {
            it.copy(
                appliedVoucher = voucher,
                totalSavings = computeSavings(voucher, it.orderTotal)
            )
        }
    }

    private fun computeSavings(voucher: Voucher?, orderTotal: Long): Long {
        voucher ?: return 0L
        if (orderTotal < voucher.minSpend) return 0L
        return when (voucher.type) {
            VoucherType.PRODUCT -> orderTotal * voucher.value / 100
            VoucherType.DELIVERY -> LocalMockData.DEFAULT_SHIPPING_FEE
        }
    }
}
