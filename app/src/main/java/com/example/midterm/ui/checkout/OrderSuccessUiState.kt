package com.example.midterm.ui.checkout


data class OrderSuccessUiState(
    val itemCount: Int = 0,
    val subtotal: Long = 0L,
    val shippingFeePostDiscount: Long = 0L,
    val total: Long = 0L,
    val orderID: String= "",
)