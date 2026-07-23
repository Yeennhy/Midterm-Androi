package com.example.midterm.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.source.LocalMockData
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : BaseViewModel<CartUiState>(CartUiState()) {

    init {
        observeCart()
    }

    /**
     * Observe cart changes from repository.
     */
    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.cartItems.collectLatest { items ->
                updateUiState(items)
            }
        }
    }

    /**
     * Build UI state from repository data.
     */
    private fun updateUiState(items: List<com.example.midterm.data.model.CartItem>) {

        val subtotal = cartRepository.getSelectedSubtotal()

        val shippingFee =
            if (subtotal == 0L)
                0L
            else
                LocalMockData.DEFAULT_SHIPPING_FEE

        val discount = 0L

        val totalPrice =
            subtotal + shippingFee - discount

        val selectedCount = cartRepository.getSelectedItemCount()

        val isSelectAll =
            items.isNotEmpty() &&
                    items.all { it.isSelected }

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

    // ---------------- Quantity ----------------

    fun increaseQuantity(
        productId: String,
        variantId: String? = null
    ) {
        cartRepository.increaseQuantity(productId, variantId)
    }

    fun decreaseQuantity(
        productId: String,
        variantId: String? = null
    ) {
        cartRepository.decreaseQuantity(productId, variantId)
    }

    fun updateQuantity(
        productId: String,
        quantity: Int,
        variantId: String? = null
    ) {
        cartRepository.updateQuantity(productId, quantity, variantId)
    }

    // ---------------- Selection ----------------

    fun toggleSelection(
        productId: String,
        variantId: String? = null
    ) {
        cartRepository.toggleSelection(productId, variantId)
    }

    fun setSelection(
        productId: String,
        selected: Boolean,
        variantId: String? = null
    ) {
        cartRepository.setSelection(productId, selected, variantId)
    }

    fun selectAll(selected: Boolean) {
        cartRepository.setAllSelection(selected)
    }

    // ---------------- Variant ----------------

    fun changeVariant(
        productId: String,
        oldVariantId: String?,
        newVariant: ProductVariant
    ) {
        cartRepository.updateVariant(
            productId,
            oldVariantId,
            newVariant
        )
    }

    // ---------------- Remove ----------------

    fun removeItem(
        productId: String,
        variantId: String? = null
    ) {
        cartRepository.removeProduct(productId, variantId)
    }

    fun removeSelected() {
        cartRepository.removeSelected()
    }

    fun clearCart() {
        cartRepository.clearCart()
    }
}