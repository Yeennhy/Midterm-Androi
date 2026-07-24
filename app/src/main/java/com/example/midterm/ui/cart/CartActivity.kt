package com.example.midterm.ui.cart

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.databinding.ActivityCartBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.makeLiveRegion
import kotlinx.coroutines.launch

class CartActivity : BaseActivity<ActivityCartBinding>(ActivityCartBinding::inflate) {

    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepository = ServiceLocator.cartRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { CartViewModel(cartRepository) }
        )[CartViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        // ── Traversal order demo (§3.2) ──────────────────────
        binding.root.makeLiveRegion()
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // UI updates based on state
                }
            }
        }
    }
}
