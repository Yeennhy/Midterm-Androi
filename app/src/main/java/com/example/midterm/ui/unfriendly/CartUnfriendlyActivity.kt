package com.example.midterm.ui.unfriendly

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.R
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.CartItem
import com.example.midterm.databinding.ActivityUnfriendlyCartBinding
import com.example.midterm.ui.base.ViewModelFactory
import kotlinx.coroutines.launch
import com.example.midterm.utils.CurrencyFormatter
import java.util.Locale

class CartUnfriendlyActivity : AppCompatActivity(), UnfriendlyCartAdapter.UnfriendlyCartItemListener {

    private lateinit var binding: ActivityUnfriendlyCartBinding
    private lateinit var viewModel: UnfriendlyCartViewModel
    private lateinit var adapter: UnfriendlyCartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnfriendlyCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { UnfriendlyCartViewModel(ServiceLocator.unfriendlyCartRepository) }
        )[UnfriendlyCartViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        adapter = UnfriendlyCartAdapter(this)
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        binding.btnNext.setOnClickListener {
            val state = viewModel.uiState.value
            if (state.selectedCount == 0) {
                Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, com.example.midterm.ui.checkout.UnfriendlyCheckoutActivity::class.java).apply {
                putExtra("itemCount", state.selectedCount)
                putExtra("subtotal", state.subtotal)
                putExtra("shippingFee", state.shippingFee)
                putExtra("voucherProductCode", state.voucherProductCode)
                putExtra("voucherProductDiscount", state.voucherProductDiscount)
                putExtra("voucherShippingCode", state.voucherShippingCode)
                putExtra("voucherShippingDiscount", state.voucherShippingDiscount)
            }
            startActivity(intent)
        }

        binding.voucherSelect.setOnClickListener {
            startActivity(Intent(this, UnfriendlyVoucherActivity::class.java))
        }

        binding.btnSelectAll.setOnClickListener {
            val currentState = viewModel.uiState.value
            val nextSelectAll = !currentState.isSelectAll
            viewModel.selectAll(nextSelectAll)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.cartItems)
                    
                    binding.tvSubtotal.text = String.format(
                        Locale.getDefault(), 
                        "Subtotal (%d items)", 
                        state.selectedCount
                    )
                    
                    if (state.discount > 0) {
                        binding.tvDiscount.visibility = android.view.View.VISIBLE
                        binding.tvDiscount.text = "Discount: -" + CurrencyFormatter.format(state.discount)
                    } else {
                        binding.tvDiscount.visibility = android.view.View.GONE
                    }

                    binding.tvTotalPrice.text = CurrencyFormatter.format(state.totalPrice)


                    val selectAllRes = if (state.isSelectAll) {
                        R.drawable.enter_voucher
                    } else {
                        R.drawable.select_all
                    }
                    binding.btnSelectAll.setImageResource(selectAllRes)
                }
            }
        }
    }

    override fun onIncreaseQuantity(item: CartItem) {
        viewModel.increaseQuantity(
            item.product.id,
            item.selectedVariant?.id
        )
    }

    override fun onDecreaseQuantity(item: CartItem) {
        viewModel.decreaseQuantity(
            item.product.id,
            item.selectedVariant?.id
        )
    }

    override fun onVariantClick(item: CartItem) {
        val sheet = UnfriendlyVariantSelectorSheet.newInstance(
            item.product.id,
            item.selectedVariant?.id
        )

        supportFragmentManager.setFragmentResultListener(
            UnfriendlyVariantSelectorSheet.REQUEST_KEY,
            this
        ) { _, result ->
            val productId = result.getString(UnfriendlyVariantSelectorSheet.RESULT_PRODUCT_ID)
            val variantId = result.getString(UnfriendlyVariantSelectorSheet.RESULT_VARIANT_ID)

            val product = ServiceLocator.productRepository.getProductById(productId ?: "")
            product?.variants?.find { it.id == variantId }?.let { variant ->
                viewModel.changeVariant(productId!!, item.selectedVariant?.id, variant)
            }
        }

        sheet.show(supportFragmentManager, "VariantSelector")
    }

    override fun onItemClick(item: CartItem) {
        viewModel.toggleSelection(
            item.product.id,
            item.selectedVariant?.id
        )
    }
}
