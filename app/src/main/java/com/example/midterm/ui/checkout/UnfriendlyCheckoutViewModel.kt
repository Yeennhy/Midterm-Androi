package com.example.midterm.ui.checkout

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.source.LocalMockData
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UnfriendlyCheckoutViewModel(
    private val cartRepository: CartRepository
) : BaseViewModel<UnfriendlyCheckoutUiState>(UnfriendlyCheckoutUiState()) {

    init {
        observeCart()
        loadShippingAddress()
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.cartItems.collectLatest {
                val selectedItems = cartRepository.getSelectedItems()
                val subtotal = cartRepository.getSelectedSubtotal()
                val itemCount = cartRepository.getSelectedItemCount()

                val rawShippingFee = if (subtotal == 0L) 0L else LocalMockData.DEFAULT_SHIPPING_FEE

                // TODO: replace with real voucher validation once that logic exists
                val voucherShippingCode = "FREESHIP_NOW"
                val voucherShippingDiscount = rawShippingFee // fully waives shipping for now
                val voucherProductCode = "WELCOME"
                val voucherProductDiscount = 10000L
                val netShippingFee = rawShippingFee - voucherShippingDiscount

                val initTotal = subtotal + rawShippingFee
                val total = initTotal-voucherShippingDiscount-voucherProductDiscount

                updateState { state ->
                    state.copy(
                        orderItems = selectedItems,
                        itemCount = itemCount,
                        subtotal = subtotal,
                        shippingFee = netShippingFee,
                        initTotal = initTotal,
                        total = total,
                        voucherShippingCode = voucherShippingCode,
                        voucherShippingDiscount = voucherShippingDiscount,
                        voucherProductCode = voucherProductCode,
                        voucherProductDiscount = voucherProductDiscount
                    )
                }
            }
        }
    }

    // TODO: point this at wherever the address actually lives once it exists
    private fun loadShippingAddress() {
        updateState { it.copy(shippingAddress = "53 Nguyen Du, Sai Gon Ward, HCMC") }
    }

    fun startEditingAddress() = updateState { it.copy(isEditingAddress = true) }

    fun saveAddress(newAddress: String) {
        if (newAddress.isBlank()) return
        updateState { it.copy(shippingAddress = newAddress, isEditingAddress = false) }
    }

    fun selectPaymentMethod(method: PaymentMethod) =
        updateState { it.copy(selectedPaymentMethod = method) }

    fun confirmOrder(): String {
        val orderId = "SS-${(10000..99999).random()}"
        cartRepository.clearCart()
        return orderId
    }
}