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
import com.example.midterm.ui.common.groupForAccessibility
import com.example.midterm.ui.common.makeLiveRegion
import com.example.midterm.ui.common.postAnnouncement
import com.example.midterm.ui.common.pruneFromAccessibilityTree
import com.example.midterm.ui.common.removeAccessibilitySupport
import com.example.midterm.ui.common.restoreToAccessibilityTree
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

class VoucherActivity : BaseActivity<ActivityVoucherBinding>(ActivityVoucherBinding::inflate) {

    private lateinit var viewModel: VoucherViewModel
    private lateinit var adapter: VoucherAdapter
    private var previousHiddenCodeError = false
    private var previousTotalSavings: Long? = null

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
                    val isAccessible = state.accessibilityMode == AccessibilityMode.ACCESSIBLE

                    binding.tvTabProduct.setBoldStyle(isProductTab)
                    binding.tvTabDelivery.setBoldStyle(!isProductTab)
                    binding.indicatorProduct.visibility = if (isProductTab) View.VISIBLE else View.INVISIBLE
                    binding.indicatorDelivery.visibility = if (isProductTab) View.INVISIBLE else View.VISIBLE
                    binding.tabProduct.isSelected = isProductTab
                    binding.tabDelivery.isSelected = !isProductTab

                    val appliedForTab = if (isProductTab) state.appliedProductVoucher else state.appliedDeliveryVoucher
                    adapter.setAccessibilityMode(state.accessibilityMode)
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

                    if (isAccessible) {
                        binding.root.applyAccessibilitySupport(label = "Discount screen")
                        binding.btnBack.applyAccessibilitySupport(label = "Back")
                        binding.tvEnterCodeLabel.labelFor = binding.etVoucherCode.id

                        binding.tabProduct.groupForAccessibility(
                            label = "Product Discount tab" + if (isProductTab) ", selected" else ""
                        )
                        binding.tabDelivery.groupForAccessibility(
                            label = "Delivery Discount tab" + if (!isProductTab) ", selected" else ""
                        )

                        binding.tvCodeError.makeLiveRegion()
                        if (state.hiddenCodeError && !previousHiddenCodeError) {
                            binding.tvCodeError.postAnnouncement(getString(R.string.discount_code_invalid))
                        }

                        val savingsLabel = if (state.totalSavings > 0) {
                            "Total savings: ${CurrencyFormatter.format(state.totalSavings)}. " +
                                "New total: ${CurrencyFormatter.format(finalAmount)}."
                        } else {
                            "No discount applied. Total: ${CurrencyFormatter.format(state.orderTotal)}."
                        }
                        binding.footerSavingsCard.restoreToAccessibilityTree()
                        binding.footerSavingsCard.groupForAccessibility(label = savingsLabel)
                        binding.footerSavingsCard.makeLiveRegion()
                        if (state.totalSavings != previousTotalSavings && previousTotalSavings != null) {
                            binding.footerSavingsCard.postAnnouncement(savingsLabel)
                        }
                    } else {
                        binding.root.removeAccessibilitySupport()
                        binding.btnBack.removeAccessibilitySupport()
                        binding.tvEnterCodeLabel.labelFor = View.NO_ID

                        binding.tabProduct.pruneFromAccessibilityTree()
                        binding.tabDelivery.pruneFromAccessibilityTree()
                        binding.footerSavingsCard.pruneFromAccessibilityTree()
                    }
                    previousHiddenCodeError = state.hiddenCodeError
                    previousTotalSavings = state.totalSavings
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
