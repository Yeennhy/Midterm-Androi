package com.example.midterm.ui.unfriendly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.data.ServiceLocator
import com.example.midterm.databinding.ActivityUnfriendlyVoucherBinding
import com.example.midterm.ui.base.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class UnfriendlyVoucherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnfriendlyVoucherBinding
    private lateinit var viewModel: UnfriendlyVoucherViewModel
    private lateinit var adapter: UnfriendlyVoucherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnfriendlyVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory {
                UnfriendlyVoucherViewModel(
                    ServiceLocator.unfriendlyVoucherRepository,
                    ServiceLocator.unfriendlyCartRepository
                )
            }
        )[UnfriendlyVoucherViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        adapter = UnfriendlyVoucherAdapter { voucher ->
            viewModel.selectVoucher(voucher)
        }
        binding.rvVouchers.layoutManager = LinearLayoutManager(this)
        binding.rvVouchers.adapter = adapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etVoucherCode.doAfterTextChanged { text ->
            viewModel.onCodeInputChanged(text?.toString() ?: "")
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.redeemCode()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.vouchers)
                    adapter.setSelectedVouchers(state.selectedProductVoucher, state.selectedDeliveryVoucher)
                    
                    binding.tvOriginalPrice.text = String.format(Locale.getDefault(), "%dđ", state.orderTotal)
                    binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

                    binding.tvDiscountedPrice.text = String.format(Locale.getDefault(), "%dđ", state.discountedTotal)
                    
                    if (state.hiddenCodeError) {
                        binding.etVoucherCode.error = "Invalid code or min spend not met"
                    } else {
                        binding.etVoucherCode.error = null
                    }
                }
            }
        }
    }
}
