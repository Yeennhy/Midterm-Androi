package com.example.midterm.data.model

/**
 * Represents a challenge task in the accessibility seminar.
 *
 * Each task defines a required shopping list (products to add to cart)
 * and required vouchers that must be applied for the task to be completed.
 *
 * MVVM Note: The SeminarRepository holds the current active task.
 * The MainViewModel observes it, and the seminar state drives
 * which UI features are enabled/visible.
 */
data class SeminarTask(
    val id: String,
    val title: String,
    val description: String,
    val requiredProductIds: List<String>,
    val requiredVoucherCodes: List<String>
)
