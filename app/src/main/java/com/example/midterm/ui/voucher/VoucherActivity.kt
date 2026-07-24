package com.example.midterm.ui.voucher

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityVoucherBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.checkout.ConfirmationActivity
import com.example.midterm.ui.checkout.OrderSuccessActivity


import com.example.midterm.ui.common.VoucherAdapter
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.postAnnouncement
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

class VoucherActivity : BaseActivity<ActivityVoucherBinding>(ActivityVoucherBinding::inflate) {

    private lateinit var viewModel: VoucherViewModel
    private lateinit var voucherAdapter: VoucherAdapter
    private var lastTotalSavings: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val voucherRepository = ServiceLocator.voucherRepository
        val cartRepository = ServiceLocator.cartRepository
        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { VoucherViewModel(voucherRepository, cartRepository, seminarRepository) }
        )[VoucherViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        voucherAdapter = VoucherAdapter { voucher ->
            viewModel.onVoucherSelected(voucher)
        }

        binding.rvVouchers.apply {
            layoutManager = LinearLayoutManager(this@VoucherActivity)
            adapter = voucherAdapter
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnApplyCode.setOnClickListener {
            viewModel.redeemHiddenCode()
        }

        binding.etVoucherCode.doOnTextChanged { text, _, _, _ ->
            viewModel.onCodeInputChanged(text?.toString() ?: "")
        }

        binding.btnTabProduct.setOnClickListener {
            viewModel.onTabSelected(0)
        }

        binding.btnTabDelivery.setOnClickListener {
            viewModel.onTabSelected(1)
        }

        // Apply Vouchers & navigate to placeholder screen
        binding.btnConfirmVouchers.setOnClickListener {
            binding.root.announceForAccessibility("Navigating to Confirmation")
            val state = viewModel.uiState.value
            val finalTotal = (state.orderSubtotal + state.shippingFee - state.totalSavings).coerceAtLeast(0L)
            val intent = Intent(this, ConfirmationActivity::class.java).apply {
                putExtra(OrderSuccessActivity.EXTRA_TOTAL_PAID, finalTotal)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderUi(state)
                }
            }
        }
    }

    private fun renderUi(state: VoucherUiState) {
        voucherAdapter.accessibilityMode = state.accessibilityMode

        val isProductTab = state.selectedTab == 0
        val currentList = if (isProductTab) state.productVouchers else state.deliveryVouchers
        val currentSelectedCode = if (isProductTab) state.appliedProductVoucher?.code else state.appliedDeliveryVoucher?.code

        voucherAdapter.selectedVoucherCode = currentSelectedCode
        voucherAdapter.submitList(currentList)

        // Tab styling & underline positioning
        binding.btnTabProduct.setTextColor(if (isProductTab) 0xFF1E2421.toInt() else 0xFF7A8580.toInt())
        binding.btnTabDelivery.setTextColor(if (!isProductTab) 0xFF1E2421.toInt() else 0xFF7A8580.toInt())

        // Error message visibility
        binding.tvCodeError.visibility = if (state.hiddenCodeError) View.VISIBLE else View.GONE

        // Code success toast
        state.codeSuccessMessage?.let { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // TalkBack selection announcement
        state.announcementMessage?.let { msg ->
            binding.root.announceForAccessibility(msg)
            viewModel.clearAnnouncementMessage()
        }

        // Total savings bottom bar display
        binding.tvTotalSavingsAmount.text = CurrencyFormatter.format(state.totalSavings)

        // TalkBack Accessibility Labels & Live Announcements
        if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
            binding.root.applyAccessibilitySupport("Discount Voucher Screen")
            binding.btnBack.applyAccessibilitySupport("Navigate back to cart")
            binding.btnApplyCode.applyAccessibilitySupport("Apply voucher code button")
            binding.btnConfirmVouchers.applyAccessibilitySupport("Apply vouchers and proceed to order confirmation")

            if (lastTotalSavings != -1L && lastTotalSavings != state.totalSavings) {
                binding.root.postAnnouncement(
                    "Voucher updated. Total savings is now ${CurrencyFormatter.format(state.totalSavings)}."
                )
            }
        }
        lastTotalSavings = state.totalSavings
    }
}
