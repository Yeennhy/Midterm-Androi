package com.example.midterm.ui.unfriendly

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.repository.UnfriendlyCartRepository
import com.example.midterm.data.source.LocalMockData
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UnfriendlyCartViewModel(
    private val cartRepository: UnfriendlyCartRepository
) : BaseViewModel<CartUnfriendlyUiState>(CartUnfriendlyUiState()) {

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
        
        val productDiscount = if (productVoucher != null) (subtotal * productVoucher.value / 100) else 0L
        val shippingDiscount = if (deliveryVoucher != null) (baseShippingFee * deliveryVoucher.value / 100) else 0L
        
        val netShippingFee = (baseShippingFee - shippingDiscount).coerceAtLeast(0L)
        val totalPrice = (subtotal + netShippingFee - productDiscount).coerceAtLeast(0L)
        val selectedCount = cartRepository.getSelectedItemCount()
        val isSelectAll = items.isNotEmpty() && items.all { it.isSelected }
        val initTotal = subtotal + baseShippingFee

        updateState {
            it.copy(
                cartItems = items,
                subtotal = subtotal,
                shippingFee = netShippingFee,
                discount = productDiscount,
                totalPrice = totalPrice,
                selectedCount = selectedCount,
                isSelectAll = isSelectAll,
                voucherProductCode = productVoucher?.code ?: "",
                voucherProductDiscount = productDiscount,
                voucherShippingCode = deliveryVoucher?.code ?: "",
                voucherShippingDiscount = shippingDiscount,
                initTotal = initTotal
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
