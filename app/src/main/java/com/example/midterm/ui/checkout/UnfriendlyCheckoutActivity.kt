package com.example.midterm.ui.checkout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.R
import com.example.midterm.data.ServiceLocator
import com.example.midterm.databinding.UnfriendlyCheckoutBinding
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

class UnfriendlyCheckoutActivity : AppCompatActivity() {

    private lateinit var binding: UnfriendlyCheckoutBinding
    private lateinit var viewModel: UnfriendlyCheckoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UnfriendlyCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cartSummary = CartSummaryExtras(
            itemCount = intent.getIntExtra("itemCount", 0),
            subtotal = intent.getLongExtra("subtotal", 0L),
            shippingFee = intent.getLongExtra("shippingFee", 0L),
            voucherProductCode = intent.getStringExtra("voucherProductCode") ?: "",
            voucherProductDiscount = intent.getLongExtra("voucherProductDiscount", 0L),
            voucherShippingCode = intent.getStringExtra("voucherShippingCode") ?: "",
            voucherShippingDiscount = intent.getLongExtra("voucherShippingDiscount", 0L)
        )

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { UnfriendlyCheckoutViewModel(ServiceLocator.cartRepository, cartSummary) }
        )[UnfriendlyCheckoutViewModel::class.java]

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

        binding.confirmButton.setOnClickListener {
            val state = viewModel.uiState.value
            val orderId = viewModel.confirmOrder()
            startActivity(
                OrderSuccessActivity.buildIntent(
                    context = this,
                    itemCount = state.itemCount,
                    subtotal = state.subtotal-state.voucherProductDiscount,
                    shippingFeePostDiscount = state.shippingFee,
                    total = state.total,
                    orderID = orderId
                )
            )
        }
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

    private fun renderAddress(state: UnfriendlyCheckoutUiState) {
        binding.shippingAddressText.text = state.shippingAddress
        binding.shippingDisplayGroup.visibility = if (state.isEditingAddress) View.GONE else View.VISIBLE
        binding.shippingEditGroup.visibility = if (state.isEditingAddress) View.VISIBLE else View.GONE
        if (state.isEditingAddress) binding.shippingAddressEditText.requestFocus()
    }

    private fun renderOrderSummary(state: UnfriendlyCheckoutUiState) {
        binding.checkoutItemCountText.text = "${state.itemCount} Items"
        binding.itemPriceText.text = CurrencyFormatter.format(state.initTotal)

        binding.checkoutShippingVoucherLabel.text = state.voucherShippingCode
        binding.checkoutShippingVoucherValue.text = "-" + CurrencyFormatter.format(state.voucherShippingDiscount)
        val shippingVoucherVisibility = if (state.voucherShippingCode.isBlank()) View.GONE else View.VISIBLE
        binding.checkoutShippingVoucherLabel.visibility = shippingVoucherVisibility
        binding.checkoutShippingVoucherValue.visibility = shippingVoucherVisibility

        binding.checkoutProductVoucherLabel.text = state.voucherProductCode
        binding.checkoutProductVoucherValue.text = "-" + CurrencyFormatter.format(state.voucherProductDiscount)
        val productVoucherVisibility = if (state.voucherProductCode.isBlank()) View.GONE else View.VISIBLE
        binding.checkoutProductVoucherLabel.visibility = productVoucherVisibility
        binding.checkoutProductVoucherValue.visibility = productVoucherVisibility

        binding.checkoutSubtotalValue.text = CurrencyFormatter.format(state.total-state.shippingFee)
        val isFreeShipping = state.shippingFee == 0L
        binding.checkoutShippingValue.text =
            if (isFreeShipping) "FREE" else CurrencyFormatter.format(state.shippingFee)
        binding.checkoutShippingValue.setTextColor(
            ContextCompat.getColor(
                this,
                if (isFreeShipping) R.color.themic_green else R.color.black
            )
        )
        binding.totalAmountValue.text = CurrencyFormatter.format(state.total)
    }

    private fun renderPaymentSelection(state: UnfriendlyCheckoutUiState) {
        fun bg(selected: Boolean) =
            if (selected) R.drawable.bg_payment_selected else R.drawable.bg_payment_option

        binding.paymentOptionCard.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.CARD))
        binding.paymentOptionEwallet.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.EWALLET))
        binding.paymentOptionBank.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.BANK))
        binding.paymentOptionCash.setBackgroundResource(bg(state.selectedPaymentMethod == PaymentMethod.CASH))
    }
}