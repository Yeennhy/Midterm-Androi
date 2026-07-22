package com.example.midterm.ui.checkout

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.data.repository.VoucherRepository
import com.example.midterm.data.source.LocalMockData
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val voucherRepository: VoucherRepository,
    private val seminarRepository: SeminarRepository
) : BaseViewModel<CheckoutUiState>(CheckoutUiState()) {

    init {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                seminarRepository.session
            ) { items, session ->
                val selectedItems = items.filter { it.isSelected }
                val subtotal = selectedItems.sumOf { it.product.price * it.quantity }
                val applied = _uiState.value.appliedVoucher
                val discount = if (applied?.type == VoucherType.PERCENT) {
                    subtotal * applied.value / 100
                } else 0L
                val shippingFee = if (applied?.type == VoucherType.SHIPPING) 0L else 30_000L

                CheckoutUiState(
                    orderItems = selectedItems,
                    subtotal = subtotal,
                    discount = discount,
                    shippingFee = shippingFee,
                    total = subtotal - discount + shippingFee,
                    appliedVoucher = applied,
                    selectedPaymentMethod = _uiState.value.selectedPaymentMethod,
                    paymentMethods = LocalMockData.paymentMethods,
                    shippingAddress = LocalMockData.defaultAddress,
                    accessibilityMode = session.accessibilityMode
                )
            }.collect { state ->
                updateState { state }
            }
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        updateState { it.copy(selectedPaymentMethod = method) }
    }

    fun applyVoucher(code: String) {
        val current = _uiState.value
        val validated = voucherRepository.validateVoucher(code, current.subtotal)
        updateState { it.copy(appliedVoucher = validated) }
    }

    fun confirmOrder() {
        updateState { it.copy(isOrderConfirmed = true) }
        cartRepository.clearCart()
        seminarRepository.markCompleted()
    }
}
