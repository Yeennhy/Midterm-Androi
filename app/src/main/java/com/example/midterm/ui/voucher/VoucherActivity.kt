package com.example.midterm.ui.voucher

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityVoucherBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.removeAccessibilitySupport
import kotlinx.coroutines.launch

class VoucherActivity : BaseActivity<ActivityVoucherBinding>(ActivityVoucherBinding::inflate) {

    private lateinit var viewModel: VoucherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voucherRepository = ServiceLocator.voucherRepository
        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { VoucherViewModel(voucherRepository, seminarRepository) }
        )[VoucherViewModel::class.java]

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                        binding.root.applyAccessibilitySupport(label = "Voucher screen")
                    } else {
                        binding.root.removeAccessibilitySupport()
                    }
                }
            }
        }
    }
}
