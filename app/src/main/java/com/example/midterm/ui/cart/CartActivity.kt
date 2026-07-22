package com.example.midterm.ui.cart

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityCartBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.removeAccessibilitySupport
import kotlinx.coroutines.launch

class CartActivity : BaseActivity<ActivityCartBinding>(ActivityCartBinding::inflate) {

    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepository = ServiceLocator.cartRepository
        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { CartViewModel(cartRepository, seminarRepository) }
        )[CartViewModel::class.java]

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                        binding.root.applyAccessibilitySupport(label = "Cart screen")
                    } else {
                        binding.root.removeAccessibilitySupport()
                    }
                }
            }
        }
    }
}
