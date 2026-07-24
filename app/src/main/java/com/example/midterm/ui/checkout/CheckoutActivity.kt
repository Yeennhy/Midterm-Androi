package com.example.midterm.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityCheckoutBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.PaymentMethodAdapter
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.makeLiveRegion
import com.example.midterm.ui.common.postAnnouncement
import com.example.midterm.ui.common.removeAccessibilitySupport
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

open class CheckoutActivity : BaseActivity<ActivityCheckoutBinding>(ActivityCheckoutBinding::inflate) {

    protected lateinit var viewModel: CheckoutViewModel
    private lateinit var paymentMethodAdapter: PaymentMethodAdapter

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

        setupViews()
        observeState()
    }

    private fun setupViews() {
        binding.btnBack.setOnClickListener { finish() }

        paymentMethodAdapter = PaymentMethodAdapter { method ->
            viewModel.selectPaymentMethod(method)
        }

        binding.rvPaymentMethods.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = paymentMethodAdapter
        }

        binding.btnEditAddress.setOnClickListener {
            viewModel.toggleEditAddress()
        }

        binding.btnSaveAddress.setOnClickListener {
            val input = binding.etAddressInput.text.toString()
            viewModel.saveAddress(input)
        }

        binding.btnConfirmOrder.setOnClickListener {
            val totalPaid = viewModel.uiState.value.total
            viewModel.confirmOrder()
            val intent = Intent(this, OrderSuccessActivity::class.java).apply {
                putExtra(OrderSuccessActivity.EXTRA_TOTAL_PAID, totalPaid)
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

    private fun renderUi(state: CheckoutUiState) {
        // 1. Address render
        state.shippingAddress?.let { address ->
            val addressString = listOfNotNull(
                address.street.takeIf { it.isNotBlank() },
                address.ward.takeIf { it.isNotBlank() },
                address.city.takeIf { it.isNotBlank() }
            ).joinToString(", ")
            binding.tvAddressText.text = addressString.ifEmpty { "53 Nguyen Du, Sai Gon Ward, HCMC" }
        }

        if (state.isEditingAddress) {
            binding.tvAddressText.visibility = View.GONE
            binding.layoutEditAddress.visibility = View.VISIBLE
            if (binding.etAddressInput.text.isEmpty()) {
                binding.etAddressInput.setText(binding.tvAddressText.text)
            }
        } else {
            binding.tvAddressText.visibility = View.VISIBLE
            binding.layoutEditAddress.visibility = View.GONE
        }

        // 2. Order Summary render
        val totalItemsCount = if (state.itemsCount > 0) state.itemsCount else state.orderItems.sumOf { it.quantity }
        binding.tvItemCount.text = "$totalItemsCount Items"

        val rawPrice = if (state.rawItemsPrice > 0) state.rawItemsPrice else (state.total + state.totalDiscount)
        binding.tvItemTotalPrice.text = CurrencyFormatter.format(rawPrice)

        // Applied Vouchers row
        val appliedProduct = state.appliedProductVoucher
        val appliedDelivery = state.appliedDeliveryVoucher
        if (appliedProduct != null || appliedDelivery != null) {
            val voucherNames = listOfNotNull(appliedProduct?.code, appliedDelivery?.code).joinToString(", ")
            binding.tvVoucherAppliedName.text = "$voucherNames Applied"
            binding.tvVoucherDiscountAmount.text = "-${CurrencyFormatter.format(state.totalDiscount)}"
            binding.layoutVoucherRow.visibility = View.VISIBLE
            binding.dividerVoucher.visibility = View.VISIBLE
        } else {
            binding.layoutVoucherRow.visibility = View.GONE
            binding.dividerVoucher.visibility = View.GONE
        }

        binding.tvSubtotalValue.text = CurrencyFormatter.format(state.subtotal)
        if (state.shippingFee == 0L) {
            binding.tvShippingValue.text = "FREE"
            binding.tvShippingValue.setTextColor(0xFF2E7D32.toInt())
        } else {
            binding.tvShippingValue.text = CurrencyFormatter.format(state.shippingFee)
            binding.tvShippingValue.setTextColor(0xFF1E2421.toInt())
        }

        // 3. Payment Methods render
        paymentMethodAdapter.selectedMethodId = state.selectedPaymentMethod?.id
        paymentMethodAdapter.accessibilityMode = state.accessibilityMode
        paymentMethodAdapter.submitList(state.paymentMethods)

        // 4. Bottom Total Amount render
        binding.tvTotalAmount.text = CurrencyFormatter.format(state.total)

        // 5. Accessibility Support
        if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
            binding.root.applyAccessibilitySupport("Confirm Order screen")
            binding.btnBack.applyAccessibilitySupport("Navigate back")
            binding.btnEditAddress.applyAccessibilitySupport("Edit shipping address button")
            binding.btnConfirmOrder.applyAccessibilitySupport(
                "Confirm order button, total payment amount ${CurrencyFormatter.format(state.total)}"
            )

            binding.layoutBottomBar.makeLiveRegion()
        } else {
            binding.root.removeAccessibilitySupport()
            binding.btnBack.removeAccessibilitySupport()
            binding.btnEditAddress.removeAccessibilitySupport()
            binding.btnConfirmOrder.removeAccessibilitySupport()
        }
    }
}
