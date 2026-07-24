package com.example.midterm.ui.unfriendly

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.repository.UnfriendlyCartRepository
import com.example.midterm.data.source.LocalMockData
import com.example.midterm.ui.base.BaseViewModel
import com.example.midterm.ui.cart.CartUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UnfriendlyCartViewModel(
    private val cartRepository: UnfriendlyCartRepository
) : BaseViewModel<CartUiState>(CartUiState()) {

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                cartRepository.selectedProductVoucher,
                cartRepository.selectedDeliveryVoucher
            ) { items, productVoucher, deliveryVoucher ->
                Triple(items, productVoucher, deliveryVoucher)
            }.collectLatest { (items, productVoucher, deliveryVoucher) ->
                updateUiState(items, productVoucher, deliveryVoucher)
            }
        }
    }

    private fun updateUiState(
        items: List<com.example.midterm.data.model.CartItem>,
        productVoucher: Voucher?,
        deliveryVoucher: Voucher?
    ) {
        val subtotal = cartRepository.getSelectedSubtotal()
        val baseShippingFee = if (subtotal == 0L) 0L else LocalMockData.DEFAULT_SHIPPING_FEE
        
        var discount = 0L
        productVoucher?.let {
            discount += (subtotal * it.value / 100)
        }
        
        var shippingFee = baseShippingFee
        deliveryVoucher?.let {
            val shipDiscount = (baseShippingFee * it.value / 100)
            shippingFee = baseShippingFee - shipDiscount
        }

        val totalPrice = subtotal + shippingFee - discount
        val selectedCount = cartRepository.getSelectedItemCount()
        val isSelectAll = items.isNotEmpty() && items.all { it.isSelected }

        updateState {
            it.copy(
                cartItems = items,
                subtotal = subtotal,
                shippingFee = shippingFee,
                discount = discount,
                totalPrice = totalPrice,
                selectedCount = selectedCount,
                isSelectAll = isSelectAll
            )
        }
    }

    fun increaseQuantity(productId: String, variantId: String? = null) {
        cartRepository.increaseQuantity(productId, variantId)
    }

    fun decreaseQuantity(productId: String, variantId: String? = null) {
        cartRepository.decreaseQuantity(productId, variantId)
    }

    fun toggleSelection(productId: String, variantId: String? = null) {
        cartRepository.toggleSelection(productId, variantId)
    }

    fun selectAll(selected: Boolean) {
        cartRepository.setAllSelection(selected)
    }

    fun changeVariant(productId: String, oldVariantId: String?, newVariant: ProductVariant) {
        cartRepository.updateVariant(productId, oldVariantId, newVariant)
    }
}
