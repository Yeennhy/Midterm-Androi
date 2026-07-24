package com.example.midterm.data.repository

import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.Product
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.data.source.LocalMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Thread-safe, in-memory cart repository using StateFlow.
 *
 * UDF Design: The cart state is held as a private MutableStateFlow and
 * exposed as an immutable StateFlow. Mutations (add, remove, update)
 * emit new lists, which triggers recomposition/observation in the UI.
 *
 * Manual DI: CartRepository is a singleton-like instance injected via
 * ViewModelProvider.Factory constructor.
 */
class CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(LocalMockData.initialCartItems)
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addProduct(product: Product, quantity: Int = 1, variant: ProductVariant? = null) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.matches(product.id, variant?.id) }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(product = product, quantity = quantity, selectedVariant = variant))
        }
        _cartItems.value = current
    }

    fun removeProduct(productId: String, variantId: String? = null) {
        _cartItems.value = _cartItems.value.filterNot { it.matches(productId, variantId) }
    }

    fun updateQuantity(productId: String, quantity: Int, variantId: String? = null) {
        if (quantity <= 0) {
            removeProduct(productId, variantId)
            return
        }
        _cartItems.value = _cartItems.value.map { item ->
            if (item.matches(productId, variantId)) item.copy(quantity = quantity) else item
        }
    }

    fun toggleSelection(productId: String, variantId: String? = null) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.matches(productId, variantId)) item.copy(isSelected = !item.isSelected) else item
        }
    }

    fun setSelectAll(isSelected: Boolean) {
        _cartItems.value = _cartItems.value.map { item ->
            item.copy(isSelected = isSelected)
        }
    }

    /** Map operation finding the specific item and updating its variant while keeping all other items intact */
    fun updateVariant(productId: String, oldVariantId: String?, newVariant: ProductVariant) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.product.id == productId && (oldVariantId == null || item.selectedVariant?.id == oldVariantId)) {
                item.copy(selectedVariant = newVariant)
            } else {
                item
            }
        }
    }

    /** Removes only selected items upon purchase completion; unselected items persist in the cart */
    fun clearSelectedItems() {
        _cartItems.value = _cartItems.value.filterNot { it.isSelected }
    }

    fun clearCart() {
        clearSelectedItems()
    }

    fun getSelectedItems(): List<CartItem> = _cartItems.value.filter { it.isSelected }

    private fun CartItem.matches(productId: String, variantId: String?): Boolean =
        product.id == productId && (variantId == null || selectedVariant?.id == variantId)
}
