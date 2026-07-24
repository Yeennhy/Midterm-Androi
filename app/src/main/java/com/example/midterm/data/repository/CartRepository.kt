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

    private val _cartItems = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(LocalMockData.products[0], quantity = 2, selectedVariant = LocalMockData.products[0].variants[0]),
            CartItem(LocalMockData.products[1], quantity = 3, selectedVariant = LocalMockData.products[1].variants[0]),
            CartItem(LocalMockData.products[2], quantity = 1, selectedVariant = LocalMockData.products[2].variants[0]),
            CartItem(LocalMockData.products[3], quantity = 1, selectedVariant = LocalMockData.products[3].variants[1]),
            CartItem(LocalMockData.products[4], quantity = 1, selectedVariant = LocalMockData.products[4].variants[0]),
            CartItem(LocalMockData.products[5], quantity = 4, selectedVariant = LocalMockData.products[5].variants[2])
        )
    )
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

    fun updateVariant(productId: String, oldVariantId: String?, newVariant: ProductVariant) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.matches(productId, oldVariantId)) {
                item.copy(selectedVariant = newVariant)
            } else item
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getSelectedItems(): List<CartItem> = _cartItems.value.filter { it.isSelected }

    private fun CartItem.matches(productId: String, variantId: String?): Boolean =
        product.id == productId && selectedVariant?.id == variantId

    fun updateVariant(
        productId: String,
        oldVariantId: String?,
        newVariant: ProductVariant
    ) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.matches(productId, oldVariantId)) {
                item.copy(selectedVariant = newVariant)
            } else {
                item
            }
        }
    }
    fun setSelection(
        productId: String,
        selected: Boolean,
        variantId: String? = null
    ) {
        _cartItems.value = _cartItems.value.map { item ->
            if (item.matches(productId, variantId))
                item.copy(isSelected = selected)
            else
                item
        }
    }
    fun setAllSelection(selected: Boolean) {
        _cartItems.value =
            _cartItems.value.map {
                it.copy(isSelected = selected)
            }
    }
    fun removeSelected() {
        _cartItems.value =
            _cartItems.value.filterNot {
                it.isSelected
            }
    }
    fun increaseQuantity(
        productId: String,
        variantId: String? = null
    ) {
        _cartItems.value =
            _cartItems.value.map {

                if (it.matches(productId, variantId))

                    it.copy(quantity = it.quantity + 1)

                else

                    it
            }
    }
    fun decreaseQuantity(
        productId: String,
        variantId: String? = null
    ) {
        _cartItems.value =
            _cartItems.value.mapNotNull {

                if (!it.matches(productId, variantId))
                    return@mapNotNull it

                val newQty = it.quantity - 1

                if (newQty <= 0)
                    null
                else
                    it.copy(quantity = newQty)
            }
    }
    fun getCartItem(
        productId: String,
        variantId: String? = null
    ): CartItem? {

        return _cartItems.value.firstOrNull {

            it.matches(productId, variantId)

        }
    }
    fun getSelectedQuantity(): Int =
        _cartItems.value
            .filter { it.isSelected }
            .sumOf { it.quantity }
    fun getSelectedSubtotal(): Long {

        var subtotal = 0L

        _cartItems.value
            .filter { it.isSelected }
            .forEach { item ->

                val unitPrice =
                    item.product.price +
                            (item.selectedVariant?.extraPrice ?: 0L)

                subtotal += unitPrice * item.quantity
            }

        return subtotal
    }
    fun getCartSize(): Int =
        _cartItems.value.size
    fun isCartEmpty(): Boolean =
        _cartItems.value.isEmpty()
    fun getSelectedItemCount(): Int =
        _cartItems.value.count { it.isSelected }
}

