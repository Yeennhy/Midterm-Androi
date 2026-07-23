package com.example.midterm.ui.voucher

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.R
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.VoucherType
import com.example.midterm.databinding.ActivityVoucherBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.VoucherAdapter
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.removeAccessibilitySupport
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

class VoucherActivity : BaseActivity<ActivityVoucherBinding>(ActivityVoucherBinding::inflate) {

    private lateinit var viewModel: VoucherViewModel
    private lateinit var adapter: VoucherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voucherRepository = ServiceLocator.voucherRepository
        val cartRepository = ServiceLocator.cartRepository
        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { VoucherViewModel(voucherRepository, cartRepository, seminarRepository) }
        )[VoucherViewModel::class.java]

        binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        setupViews()
        observeState()
    }

    private fun setupViews() {
        adapter = VoucherAdapter { voucher ->
            val state = viewModel.uiState.value
            val appliedForType = if (voucher.type == VoucherType.PRODUCT) {
                state.appliedProductVoucher
            } else {
                state.appliedDeliveryVoucher
            }
            if (appliedForType?.code == voucher.code) {
                viewModel.removeVoucher(voucher.type)
            } else {
                viewModel.onVoucherSelected(voucher)
            }
        }
        binding.rvVouchers.layoutManager = LinearLayoutManager(this)
        binding.rvVouchers.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }

        binding.tabProduct.setOnClickListener { viewModel.onTabSelected(0) }
        binding.tabDelivery.setOnClickListener { viewModel.onTabSelected(1) }

        binding.btnApplyCode.setOnClickListener { viewModel.redeemHiddenCode() }
        binding.etVoucherCode.doAfterTextChanged {
            viewModel.onHiddenCodeInputChanged(it?.toString().orEmpty())
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val isProductTab = state.selectedTab == 0
                    binding.tvTabProduct.setBoldStyle(isProductTab)
                    binding.tvTabDelivery.setBoldStyle(!isProductTab)
                    binding.indicatorProduct.visibility = if (isProductTab) View.VISIBLE else View.INVISIBLE
                    binding.indicatorDelivery.visibility = if (isProductTab) View.INVISIBLE else View.VISIBLE

                    val appliedForTab = if (isProductTab) state.appliedProductVoucher else state.appliedDeliveryVoucher
                    adapter.setSelectedCode(appliedForTab?.code)
                    adapter.submitList(if (isProductTab) state.productVouchers else state.deliveryVouchers)

                    binding.tvCodeError.visibility = if (state.hiddenCodeError) View.VISIBLE else View.GONE

                    val finalAmount = state.orderTotal - state.totalSavings
                    if (state.totalSavings > 0) {
                        binding.tvOriginalPrice.visibility = View.VISIBLE
                        binding.tvOriginalPrice.text = CurrencyFormatter.format(state.orderTotal)
                        binding.tvFinalPrice.text = CurrencyFormatter.format(finalAmount)
                    } else {
                        binding.tvOriginalPrice.visibility = View.GONE
                        binding.tvFinalPrice.text = CurrencyFormatter.format(state.orderTotal)
                    }

                    if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                        binding.root.applyAccessibilitySupport(label = "Discount screen")
                    } else {
                        binding.root.removeAccessibilitySupport()
                    }
                }
            }
        }
    }

    private fun TextView.setBoldStyle(active: Boolean) {
        typeface = ResourcesCompat.getFont(
            context,
            if (active) R.font.hanken_grotesk_bold else R.font.hanken_grotesk_regular
        )
    }
}
