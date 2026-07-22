package com.example.midterm.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Abstract base Activity that handles generic ViewBinding inflation automatically.
 *
 * ## Why this exists
 * Every Activity in this project follows the same pattern:
 * 1. Inflate a layout via ViewBinding
 * 2. Call setContentView(binding.root)
 * Instead of repeating this in every Activity, BaseActivity does it once.
 *
 * ## Usage
 * ```kotlin
 * class CartActivity : BaseActivity<ActivityCartBinding>(ActivityCartBinding::inflate) {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         // binding is already ready — use it directly
 *         binding.recyclerView.adapter = ...
 *     }
 * }
 * ```
 *
 * ## UDF Note
 * By eliminating `findViewById`, we get compile-time safety for all view references.
 * The `binding` property is available to all subclasses after `super.onCreate()`.
 *
 * @param VB The ViewBinding type generated for this Activity's layout.
 * @param inflate A lambda that creates the binding from a LayoutInflater.
 *                Typically a method reference like `ActivityCartBinding::inflate`.
 */
abstract class BaseActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : AppCompatActivity() {

    protected lateinit var binding: VB
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
    }
}
