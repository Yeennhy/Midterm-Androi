package com.example.midterm.ui.checkout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.R
import com.example.midterm.data.ServiceLocator
import com.example.midterm.databinding.UnfriendlyCheckoutBinding // rename if you rename the layout file
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

class UnfriendlyCheckoutActivity : AppCompatActivity() {

    private lateinit var binding: UnfriendlyCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UnfriendlyCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { CheckoutViewModel(ServiceLocator.cartRepository) }
        )[CheckoutViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        binding.checkoutBackButton.setOnClickListener { finish() }

        binding.editShippingButton.setOnClickListener {
            binding.shippingAddressEditText.setText(binding.shippingAddressText.text)
            viewModel.startEditingAddress()
        }

        binding.saveAddressButton.setOnClickListener {
            viewModel.saveAddress(binding.shippingAddressEditText.text.toString())
        }

        binding.paymentOptionCard.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.CARD) }
        binding.paymentOptionEwallet.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.EWALLET) }
        binding.paymentOptionBank.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.BANK) }
        binding.paymentOptionCash.setOnClickListener { viewModel.selectPaymentMethod(PaymentMethod.CASH) }

        binding.confirmButton.setOnClickListener { viewModel.confirmOrder() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderAddress(state)
                    renderOrderSummary(state)
                    renderPaymentSelection(state)
                }
            }
        }
    }

    private fun renderAddress(state: CheckoutUiState) {
        binding.shippingAddressText.text = state.shippingAddress
        binding.shippingDisplayGroup.visibility = if (state.isEditingAddress) View.GONE else View.VISIBLE
        binding.shippingEditGroup.visibility = if (state.isEditingAddress) View.VISIBLE else View.GONE
        if (state.isEditingAddress) binding.shippingAddressEditText.requestFocus()
    }

    private fun renderOrderSummary(state: CheckoutUiState) {
        binding.checkoutItemCountText.text = "${state.itemCount} Items"
        binding.itemPriceText.text = CurrencyFormatter.format(state.subtotal + state.shippingFee)

        binding.checkoutShippingVoucherLabel.text = state.voucherShippingCode.orEmpty()
        binding.checkoutVoucherValue.text = "-" + CurrencyFormatter.format(state.voucherShippingDiscount)
        val voucherVisibility1 = if (state.voucherShippingCode.isBlank()) View.GONE else View.VISIBLE
        binding.checkoutShippingVoucherLabel.visibility = voucherVisibility1
        binding.checkoutShippingVoucherValue.visibility = voucherVisibility1

        binding.checkoutProductVoucherLabel.text = state.voucherProductCode.orEmpty()
        binding.checkoutProductVoucherValue.text = "-" + CurrencyFormatter.format(state.voucherProductDiscount)
        val voucherVisibility2 = if (state.voucherProductCode.isBlank()) View.GONE else View.VISIBLE
        binding.checkoutProductVoucherLabel.visibility = voucherVisibility2
        binding.checkoutProductVoucherValue.visibility = voucherVisibility2

        binding.checkoutSubtotalValue.text = CurrencyFormatter.format(state.subtotal)
        binding.checkoutShippingValue.text =
            if (state.shippingFee == 0L) "FREE" else CurrencyFormatter.format(state.shippingFee)

        binding.totalAmountValue.text = CurrencyFormatter.format(state.total)
    }

    private fun renderPaymentSelection(state: CheckoutUiState) {
        fun bg(selected: Boolean) =
            if (selected) R.drawable.bg_payment_selected else R.drawable.bg_payment_option

        binding.paymentOptionCard.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.CARD))
        binding.paymentOptionEwallet.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.EWALLET))
        binding.paymentOptionBank.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.BANK))
        binding.paymentOptionCash.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.CASH))
    }
}