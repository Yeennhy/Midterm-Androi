package com.example.midterm.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.Product
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel for the Shopping Cart screen.
 */
class CartViewModel(
    private val cartRepository: CartRepository,
    private val seminarRepository: SeminarRepository
) : BaseViewModel<CartUiState>(CartUiState()) {

    init {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                seminarRepository.session
            ) { items, session ->
                val checkedItems = items.filter { it.isSelected }
                val calculatedSubtotal: Long = checkedItems.sumOf { item ->
                    val unitPrice = item.product.price + (item.selectedVariant?.extraPrice ?: 0L)
                    unitPrice * item.quantity
                }
                val selectedCount = checkedItems.size
                val isAllSelected = items.isNotEmpty() && items.all { it.isSelected }
                val isCheckoutEnabled = selectedCount > 0

                CartUiState(
                    cartItems = items,
                    subtotal = calculatedSubtotal,
                    selectedCount = selectedCount,
                    isAllSelected = isAllSelected,
                    isCheckoutEnabled = isCheckoutEnabled,
                    selectedCartItemForVariant = null,
                    accessibilityMode = session.accessibilityMode
                )
            }.collect { newState ->
                updateState { current ->
                    newState.copy(
                        selectedCartItemForVariant = current.selectedCartItemForVariant,
                        accessibilityAnnouncement = current.accessibilityAnnouncement,
                        errorMessage = current.errorMessage
                    )
                }
            }
        }
    }

    fun toggleSelection(productId: String, variantId: String? = null) {
        cartRepository.toggleSelection(productId, variantId)
    }

    fun toggleSelectAll() {
        val targetState = !_uiState.value.isAllSelected
        cartRepository.setSelectAll(targetState)
    }

    fun onQuantityChanged(item: CartItem, delta: Int) {
        val currentItems = _uiState.value.cartItems
        val firstItem = currentItems.firstOrNull()

        val isFirstItem = firstItem != null &&
                firstItem.product.id == item.product.id &&
                firstItem.selectedVariant?.id == item.selectedVariant?.id

        if (isFirstItem && delta < 0 && item.quantity <= 1) {
            updateState { it.copy(errorMessage = "Minimum quantity reached for this item") }
            return
        }

        val newQuantity = item.quantity + delta
        if (newQuantity <= 0) {
            cartRepository.removeProduct(item.product.id, item.selectedVariant?.id)
        } else {
            cartRepository.updateQuantity(item.product.id, newQuantity, item.selectedVariant?.id)
        }
    }

    fun clearErrorMessage() {
        updateState { it.copy(errorMessage = null) }
    }

    fun onAnnouncementConsumed() {
        updateState { it.copy(accessibilityAnnouncement = null) }
    }

    fun removeProduct(productId: String, variantId: String? = null) {
        cartRepository.removeProduct(productId, variantId)
    }

    fun onVariantClicked(cartItem: CartItem) {
        updateState { it.copy(selectedCartItemForVariant = cartItem) }
    }

    /** Map operation on existing cart items: finds target item, updates variant, keeps all other items intact */
    fun onVariantSelected(cartItem: CartItem, newVariant: ProductVariant) {
        val updatedList = _uiState.value.cartItems.map { item ->
            if (item.product.id == cartItem.product.id &&
                (cartItem.selectedVariant == null || item.selectedVariant?.id == cartItem.selectedVariant.id)) {
                item.copy(selectedVariant = newVariant)
            } else {
                item
            }
        }
        val checkedItems = updatedList.filter { it.isSelected }
        val newSubtotal = checkedItems.sumOf { item ->
            val unitPrice = item.product.price + (item.selectedVariant?.extraPrice ?: 0L)
            unitPrice * item.quantity
        }
        updateState {
            it.copy(
                cartItems = updatedList,
                subtotal = newSubtotal,
                selectedCartItemForVariant = null
            )
        }
        cartRepository.updateVariant(cartItem.product.id, cartItem.selectedVariant?.id, newVariant)
    }

    fun onVariantSheetDismissed() {
        updateState { it.copy(selectedCartItemForVariant = null) }
    }
}
