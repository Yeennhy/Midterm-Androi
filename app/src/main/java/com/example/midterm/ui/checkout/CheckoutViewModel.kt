package com.example.midterm.ui.checkout

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.Address
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.data.repository.CartRepository
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.data.repository.VoucherRepository
import com.example.midterm.data.source.LocalMockData
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val voucherRepository: VoucherRepository,
    private val seminarRepository: SeminarRepository
) : BaseViewModel<CheckoutUiState>(CheckoutUiState()) {

    init {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                voucherRepository.appliedProductVoucher,
                voucherRepository.appliedDeliveryVoucher,
                seminarRepository.session
            ) { items, appliedProduct, appliedDelivery, session ->
                val selectedItems = items.filter { it.isSelected }
                val itemsCount = selectedItems.sumOf { it.quantity }
                val rawItemsPrice = selectedItems.sumOf { item ->
                    val unitPrice = item.product.price + (item.selectedVariant?.extraPrice ?: 0L)
                    unitPrice * item.quantity
                }

                val productDiscount = computeProductDiscount(appliedProduct, rawItemsPrice)
                val subtotal = (rawItemsPrice - productDiscount).coerceAtLeast(0L)

                val baseShipping = LocalMockData.DEFAULT_SHIPPING_FEE
                val deliveryDiscount = computeDeliveryDiscount(appliedDelivery, baseShipping, rawItemsPrice)
                val finalShipping = (baseShipping - deliveryDiscount).coerceAtLeast(0L)

                val totalDiscount = productDiscount + deliveryDiscount
                val grandTotal = subtotal + finalShipping

                val currentSelectedMethod = _uiState.value.selectedPaymentMethod ?: LocalMockData.paymentMethods.firstOrNull()
                val currentAddress = _uiState.value.shippingAddress ?: LocalMockData.defaultAddress

                CheckoutUiState(
                    orderItems = selectedItems,
                    itemsCount = itemsCount,
                    rawItemsPrice = rawItemsPrice,
                    subtotal = subtotal,
                    productDiscount = productDiscount,
                    deliveryDiscount = deliveryDiscount,
                    totalDiscount = totalDiscount,
                    shippingFee = finalShipping,
                    total = grandTotal,
                    appliedProductVoucher = appliedProduct,
                    appliedDeliveryVoucher = appliedDelivery,
                    appliedVoucher = appliedProduct ?: appliedDelivery,
                    selectedPaymentMethod = currentSelectedMethod,
                    paymentMethods = LocalMockData.paymentMethods,
                    shippingAddress = currentAddress,
                    isEditingAddress = _uiState.value.isEditingAddress,
                    accessibilityMode = session.accessibilityMode
                )
            }.collect { state ->
                updateState { state }
            }
        }
    }

    fun toggleEditAddress() {
        updateState { it.copy(isEditingAddress = !it.isEditingAddress) }
    }

    fun saveAddress(newAddressText: String) {
        if (newAddressText.isBlank()) return
        val parts = newAddressText.split(",").map { it.trim() }
        val street = parts.getOrNull(0) ?: newAddressText
        val ward = parts.getOrNull(1) ?: ""
        val city = parts.getOrNull(2) ?: ""

        val newAddress = Address(street = street, ward = ward, district = "", city = city, isDefault = true)
        updateState { it.copy(shippingAddress = newAddress, isEditingAddress = false) }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        updateState { it.copy(selectedPaymentMethod = method) }
    }

    fun applyVoucher(code: String) {
        val current = _uiState.value
        val validated = voucherRepository.validateVoucher(code, current.rawItemsPrice)
        if (validated != null) {
            when (validated.type) {
                VoucherType.PRODUCT -> voucherRepository.applyProductVoucher(validated)
                VoucherType.DELIVERY -> voucherRepository.applyDeliveryVoucher(validated)
            }
        }
    }

    fun confirmOrder() {
        updateState { it.copy(isOrderConfirmed = true, isOrderSuccess = true) }
        cartRepository.clearSelectedItems()
        voucherRepository.clearVouchers()
        seminarRepository.markCompleted()
    }

    private fun computeProductDiscount(voucher: Voucher?, itemsPrice: Long): Long {
        voucher ?: return 0L
        if (itemsPrice < voucher.minSpend) return 0L
        return if (voucher.isFixedValue) {
            voucher.value.toLong().coerceAtMost(itemsPrice)
        } else {
            itemsPrice * voucher.value / 100
        }
    }

    private fun computeDeliveryDiscount(voucher: Voucher?, baseShipping: Long, itemsPrice: Long): Long {
        voucher ?: return 0L
        if (itemsPrice < voucher.minSpend) return 0L
        return if (voucher.isFixedValue) {
            voucher.value.toLong().coerceAtMost(baseShipping)
        } else {
            baseShipping * voucher.value / 100
        }
    }
}
