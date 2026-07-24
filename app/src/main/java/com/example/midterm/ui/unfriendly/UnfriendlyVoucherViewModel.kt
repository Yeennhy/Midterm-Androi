package com.example.midterm.ui.unfriendly

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.repository.UnfriendlyCartRepository
import com.example.midterm.data.repository.UnfriendlyVoucherRepository
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UnfriendlyVoucherViewModel(
    private val voucherRepository: UnfriendlyVoucherRepository,
    private val cartRepository: UnfriendlyCartRepository
) : BaseViewModel<UnfriendlyVoucherUiState>(UnfriendlyVoucherUiState()) {

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                cartRepository.selectedProductVoucher,
                cartRepository.selectedDeliveryVoucher
            ) { items, productVoucher, deliveryVoucher ->
                Triple(items, productVoucher, deliveryVoucher)
            }.collect { (items, productVoucher, deliveryVoucher) ->
                val subtotal = cartRepository.getSelectedSubtotal()
                val selectedCount = cartRepository.getSelectedItemCount()
                
                // For simplicity in this demo, let's say total = subtotal + 30k (default shipping)
                val orderTotal = if (selectedCount > 0) subtotal + 30000 else 0L
                
                var savings = 0L
                productVoucher?.let {
                    savings += (subtotal * it.value / 100)
                }
                deliveryVoucher?.let {
                    // Delivery voucher is 100% off 30k or percentage of 30k
                    savings += (30000 * it.value / 100)
                }

                updateState {
                    it.copy(
                        vouchers = voucherRepository.getAllVouchers(),
                        selectedProductVoucher = productVoucher,
                        selectedDeliveryVoucher = deliveryVoucher,
                        subtotal = subtotal,
                        orderTotal = orderTotal,
                        totalSavings = savings,
                        discountedTotal = maxOf(0, orderTotal - savings)
                    )
                }
            }
        }
    }

    fun selectVoucher(voucher: Voucher) {
        cartRepository.applyVoucher(voucher)
    }

    fun onCodeInputChanged(code: String) {
        updateState { it.copy(hiddenCodeInput = code, hiddenCodeError = false) }
    }

    fun redeemCode() {
        val code = uiState.value.hiddenCodeInput
        val voucher = voucherRepository.validateVoucher(code, uiState.value.subtotal)
        if (voucher != null) {
            cartRepository.applyVoucher(voucher)
            updateState { it.copy(hiddenCodeInput = "", hiddenCodeError = false) }
        } else {
            updateState { it.copy(hiddenCodeError = true) }
        }
    }
}
