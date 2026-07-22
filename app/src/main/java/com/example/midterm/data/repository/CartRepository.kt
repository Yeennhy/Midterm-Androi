package com.example.midterm.data.repository

import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Thread-safe, in-memory cart repository using StateFlow.
 *
 * UDF Design: The cart state is held as a private MutableStateFlow and
 * exposed as an immutable StateFlow. Mutations (add, remove, update)
 * emit new lists, which triggers recomposition/observation in the UI.
 * This enforces a unidirectional data flow:
 *   UI Event -> ViewModel -> Repository mutation -> StateFlow emission -> UI update
 *
 * Manual DI: CartRepository is a singleton-like instance injected via
 * ViewModelProvider.Factory constructor.
 */
class CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addProduct(product: Product, quantity: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == product.id }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(product = product, quantity = quantity))
        }
        _cartItems.value = current
    }

    fun removeProduct(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeProduct(productId)
            return
        }
        _cartItems.value = _cartItems.value.map { item ->
            if (item.product.id == productId) item.copy(quantity = quantity) else item
        }
    }

    fun toggleSelection(productId: String) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.product.id == productId) item.copy(isSelected = !item.isSelected) else item
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getSelectedItems(): List<CartItem> = _cartItems.value.filter { it.isSelected }
}
