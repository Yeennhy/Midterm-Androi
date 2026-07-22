package com.example.midterm.ui.checkout

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityCheckoutBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.removeAccessibilitySupport
import kotlinx.coroutines.launch

class CheckoutActivity : BaseActivity<ActivityCheckoutBinding>(ActivityCheckoutBinding::inflate) {

    private lateinit var viewModel: CheckoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepository = ServiceLocator.cartRepository
        val voucherRepository = ServiceLocator.voucherRepository
        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory {
                CheckoutViewModel(cartRepository, voucherRepository, seminarRepository)
            }
        )[CheckoutViewModel::class.java]

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                        binding.root.applyAccessibilitySupport(label = "Checkout screen")
                    } else {
                        binding.root.removeAccessibilitySupport()
                    }
                }
            }
        }
    }
}
