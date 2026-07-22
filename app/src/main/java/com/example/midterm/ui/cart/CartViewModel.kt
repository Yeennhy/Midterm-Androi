package com.example.midterm.ui.cart

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Product
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel for the Cart screen.
 *
 * UDF Flow:
 *   UI Event (add/remove/toggle) → CartViewModel → CartRepository mutation →
 *   StateFlow emission → CartUiState update → UI re-render
 *
 * Accessibility: Observes SeminarRepository.session.accessibilityMode so the
 * Activity can dynamically apply or strip accessibility support.
 *
 * Manual DI: Both repositories are injected via constructor (ViewModelFactory).
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
                val total = items
                    .filter { it.isSelected }
                    .sumOf { it.product.price * it.quantity }
                CartUiState(
                    cartItems = items,
                    totalPrice = total,
                    accessibilityMode = session.accessibilityMode
                )
            }.collect { state ->
                updateState { state }
            }
        }
    }

    fun addProduct(product: Product, quantity: Int = 1) {
        cartRepository.addProduct(product, quantity)
    }

    fun removeProduct(productId: String) {
        cartRepository.removeProduct(productId)
    }

    fun updateQuantity(productId: String, quantity: Int) {
        cartRepository.updateQuantity(productId, quantity)
    }

    fun toggleSelection(productId: String) {
        cartRepository.toggleSelection(productId)
    }

    fun onVariantClicked(product: Product) {
        // TODO: Show VariantSelectorSheet bottom sheet
    }

    fun onQuantityChanged(productId: String, delta: Int) {
        // TODO: Apply delta to current quantity and update via repository
    }
}
