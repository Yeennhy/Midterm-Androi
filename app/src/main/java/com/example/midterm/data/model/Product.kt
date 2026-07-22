package com.example.midterm.data.model

/**
 * Immutable domain entity representing a product in the catalog.
 *
 * MVVM Purpose: This is the "Model" layer — a plain data class that holds
 * the source of truth for product information. It is used by Repositories
 * to supply data to ViewModels, which then expose it via StateFlow to the UI.
 *
 * @property price Stored as Long to cleanly represent Vietnamese Dong (VND)
 *                 without decimal precision issues (e.g. 100000 = 100.000đ).
 */
data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val category: String,
    val imageResId: Int
)
