package com.example.midterm.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility for formatting Long price values as Vietnamese Dong (VND) strings.
 *
 * Usage: CurrencyFormatter.format(100000) -> "100.000đ"
 *
 * MVVM Note: This is a stateless utility in the utils layer. ViewModels
 * call it to transform raw Long prices into display strings before
 * populating UiState data classes. The UI never formats raw values.
 */
object CurrencyFormatter {

    private val formatter: NumberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    /**
     * Formats a VND price with thousand separators and the đồng symbol.
     * Example: 1500000 -> "1.500.000đ"
     */
    fun format(amount: Long): String = "${formatter.format(amount)}đ"

    /**
     * Formats a VND amount with a "₫" suffix and negative handling.
     * Example: -50000 -> "-50.000đ"
     */
    fun formatWithSign(amount: Long): String = formatter.format(amount) + "đ"
}
