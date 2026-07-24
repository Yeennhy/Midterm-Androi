package com.example.midterm.ui.checkout

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CartSummaryExtras(
    val itemCount: Int,
    val subtotal: Long,
    val shippingFee: Long,
    val voucherProductCode: String,
    val voucherProductDiscount: Long,
    val voucherShippingCode: String,
    val voucherShippingDiscount: Long
)
class UnfriendlyCheckoutViewModel(
    private val cartRepository: CartRepository,
    private val cartSummary: CartSummaryExtras
) : BaseViewModel<UnfriendlyCheckoutUiState>(UnfriendlyCheckoutUiState()) {

    init {
        applyCartSummary()
        observeCartItems()
        loadShippingAddress()
    }

    // Values now come from the cart screen's own calculation — no longer derived here
    private fun applyCartSummary() {
        val initTotal = cartSummary.subtotal + cartSummary.shippingFee
        val total = (initTotal - cartSummary.voucherShippingDiscount - cartSummary.voucherProductDiscount)
            .coerceAtLeast(0L)
        val netShippingFee = (cartSummary.shippingFee - cartSummary.voucherShippingDiscount).coerceAtLeast(0L)

        updateState { state ->
            state.copy(
                itemCount = cartSummary.itemCount,
                subtotal = cartSummary.subtotal,
                shippingFee = netShippingFee,
                initTotal = initTotal,
                total = total,
                voucherShippingCode = cartSummary.voucherShippingCode,
                voucherShippingDiscount = cartSummary.voucherShippingDiscount,
                voucherProductCode = cartSummary.voucherProductCode,
                voucherProductDiscount = cartSummary.voucherProductDiscount
            )
        }
    }

    // Still observed separately — this only feeds the actual item list (thumbnails/names),
    // the Intent only carried summary numbers, not full CartItem objects
    private fun observeCartItems() {
        viewModelScope.launch {
            cartRepository.cartItems.collectLatest { items ->
                updateState { it.copy(orderItems = items.filter { item -> item.isSelected }) }
            }
        }
    }

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