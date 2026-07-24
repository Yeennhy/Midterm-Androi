package com.example.midterm.data.repository

import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.Product
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.source.LocalMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UnfriendlyCartRepository {

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

    private val _selectedProductVoucher = MutableStateFlow<Voucher?>(null)
    val selectedProductVoucher: StateFlow<Voucher?> = _selectedProductVoucher.asStateFlow()

    private val _selectedDeliveryVoucher = MutableStateFlow<Voucher?>(null)
    val selectedDeliveryVoucher: StateFlow<Voucher?> = _selectedDeliveryVoucher.asStateFlow()

    fun applyVoucher(voucher: Voucher) {
        if (voucher.type == VoucherType.PRODUCT) {
            _selectedProductVoucher.value = if (_selectedProductVoucher.value?.code == voucher.code) null else voucher
        } else {
            _selectedDeliveryVoucher.value = if (_selectedDeliveryVoucher.value?.code == voucher.code) null else voucher
        }
    }

    fun removeVoucher(type: VoucherType) {
        if (type == VoucherType.PRODUCT) {
            _selectedProductVoucher.value = null
        } else {
            _selectedDeliveryVoucher.value = null
        }
    }

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
