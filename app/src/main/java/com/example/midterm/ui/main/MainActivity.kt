package com.example.midterm.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.R
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityMainBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.cart.CartActivity
import com.example.midterm.ui.checkout.CheckoutActivity
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.removeAccessibilitySupport
import com.example.midterm.ui.voucher.VoucherActivity
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { MainViewModel(seminarRepository) }
        )[MainViewModel::class.java]

        setupClickListeners()
        observeState()
    }

    private fun setupClickListeners() {
        binding.btnToggleAccessibility.setOnClickListener {
            viewModel.toggleAccessibility()
        }
        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.btnVoucher.setOnClickListener {
            startActivity(Intent(this, VoucherActivity::class.java))
        }
        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val modeLabel = when (state.accessibilityMode) {
                        AccessibilityMode.ACCESSIBLE -> "ACCESSIBLE"
                        AccessibilityMode.INACCESSIBLE -> "INACCESSIBLE"
                    }
                    binding.tvAccessibilityStatus.text =
                        getString(R.string.accessibility_status, modeLabel)

                    if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                        binding.btnToggleAccessibility.applyAccessibilitySupport(
                            label = "Toggle accessibility mode"
                        )
                        binding.btnCart.applyAccessibilitySupport(label = getString(R.string.nav_cart))
                        binding.btnVoucher.applyAccessibilitySupport(label = getString(R.string.nav_voucher))
                        binding.btnCheckout.applyAccessibilitySupport(label = getString(R.string.nav_checkout))
                    } else {
                        binding.btnToggleAccessibility.removeAccessibilitySupport()
                        binding.btnCart.removeAccessibilitySupport()
                        binding.btnVoucher.removeAccessibilitySupport()
                        binding.btnCheckout.removeAccessibilitySupport()
                    }
                }
            }
        }
    }
}
