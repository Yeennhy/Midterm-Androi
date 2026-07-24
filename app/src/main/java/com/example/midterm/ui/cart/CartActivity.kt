package com.example.midterm.ui.cart

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.CartItem
import com.example.midterm.data.model.Product
import com.example.midterm.databinding.ActivityCartBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.applyAccessibilitySupport

import com.example.midterm.ui.common.postAnnouncement
import com.example.midterm.ui.checkout.CheckoutActivity
import com.example.midterm.ui.checkout.OrderSuccessActivity

import com.example.midterm.ui.voucher.VoucherActivity

import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch


class CartActivity : BaseActivity<ActivityCartBinding>(ActivityCartBinding::inflate) {

    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter
    private var lastSubtotal: Long = -1L
    private var lastItemCount: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepository = ServiceLocator.cartRepository
        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { CartViewModel(cartRepository, seminarRepository) }
        )[CartViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        cartAdapter = CartAdapter(
            onToggleSelect = { item ->
                viewModel.toggleSelection(item.product.id, item.selectedVariant?.id)
            },
            onQuantityChange = { item, delta ->
                viewModel.onQuantityChanged(item, delta)
            },
            onVariantClick = { item ->
                viewModel.onVariantClicked(item)
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }

        binding.cbSelectAll.setOnClickListener {
            viewModel.toggleSelectAll()
        }

        binding.cardApplyVoucher.setOnClickListener {
            if (viewModel.uiState.value.selectedCount == 0) {
                val message = "Please select at least one item to apply vouchers."
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                binding.root.announceForAccessibility(message)
            } else {
                startActivity(Intent(this, VoucherActivity::class.java))
            }
        }


        binding.btnCheckout.setOnClickListener {
            if (viewModel.uiState.value.isCheckoutEnabled) {
                binding.root.announceForAccessibility("Navigating to Checkout")
                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putExtra(OrderSuccessActivity.EXTRA_TOTAL, viewModel.uiState.value.subtotal)
                }
                startActivity(intent)
            }
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

    private fun renderUi(state: CartUiState) {
        cartAdapter.accessibilityMode = state.accessibilityMode
        cartAdapter.submitList(state.cartItems)

        // Select All Checkbox state without triggering listener loop
        binding.cbSelectAll.setOnCheckedChangeListener(null)
        binding.cbSelectAll.isChecked = state.isAllSelected
        binding.cbSelectAll.setOnCheckedChangeListener { _, _ -> }

        // Subtotal & Items count
        binding.tvSubtotalLabel.text = "Subtotal (${state.selectedCount} items)"
        binding.tvSubtotalAmount.text = CurrencyFormatter.format(state.subtotal)

        // Checkout Button state (Enabled vs Disabled)
        val isEnabled = state.isCheckoutEnabled
        binding.btnCheckout.isEnabled = isEnabled
        val buttonColor = if (isEnabled) 0xFF963B1E.toInt() else 0xFF3E4643.toInt()
        binding.btnCheckout.backgroundTintList = ColorStateList.valueOf(buttonColor)


        // Accessibility Labels & Live Region Announcements
        if (state.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
            binding.root.applyAccessibilitySupport("Shopping Cart Screen")
            binding.cbSelectAll.applyAccessibilitySupport(
                "Select all items, ${if (state.isAllSelected) "Checked" else "Not checked"}"
            )
            binding.cardApplyVoucher.applyAccessibilitySupport("Apply discount code button")

            val checkoutAccessibilityDesc = if (isEnabled) {
                "Checkout button, ${state.selectedCount} items selected, total ${CurrencyFormatter.format(state.subtotal)}"
            } else {
                "Checkout, button disabled"
            }
            binding.btnCheckout.applyAccessibilitySupport(checkoutAccessibilityDesc)

            // Dynamic Live Region Announcement on price or item count change
            if (lastSubtotal != -1L && (lastSubtotal != state.subtotal || lastItemCount != state.cartItems.size)) {
                binding.root.postAnnouncement(
                    "Cart updated. Subtotal is now ${CurrencyFormatter.format(state.subtotal)} for ${state.selectedCount} selected items."
                )
            }

            // Trigger one-time accessibility announcements & error messages
            state.errorMessage?.let { message ->
                binding.root.announceForAccessibility(message)
                viewModel.clearErrorMessage()
            }
            state.accessibilityAnnouncement?.let { message ->
                binding.root.postAnnouncement(message)
                viewModel.onAnnouncementConsumed()
            }

        }

        lastSubtotal = state.subtotal
        lastItemCount = state.cartItems.size

        // Launch VariantSelectorSheet if triggered as a one-time event
        state.selectedCartItemForVariant?.let { item ->
            if (supportFragmentManager.findFragmentByTag("VariantSelectorSheet") == null) {
                showVariantSheet(item)
            }
        }

    }

    private fun showVariantSheet(cartItem: CartItem) {
        val sheet = VariantSelectorSheet.newInstance()
        sheet.setVariants(
            variantList = cartItem.product.variants,
            currentVariantId = cartItem.selectedVariant?.id
        ) { newVariant ->
            viewModel.onVariantSelected(cartItem, newVariant)
        }
        sheet.setOnDismissListener {
            viewModel.onVariantSheetDismissed()
        }
        sheet.show(supportFragmentManager, "VariantSelectorSheet")
    }
}

