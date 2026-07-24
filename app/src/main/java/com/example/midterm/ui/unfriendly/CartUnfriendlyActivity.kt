package com.example.midterm.ui.unfriendly

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.CartItem
import com.example.midterm.databinding.ActivityUnfriendlyCartBinding
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.cart.CartAdapter
import com.example.midterm.ui.cart.CartViewModel
import com.example.midterm.ui.cart.VariantSelectorSheet
import kotlinx.coroutines.launch
import java.util.Locale

class CartUnfriendlyActivity : AppCompatActivity(), CartAdapter.CartItemListener {

    private lateinit var binding: ActivityUnfriendlyCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnfriendlyCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { CartViewModel(ServiceLocator.cartRepository) }
        )[CartViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        adapter = CartAdapter(this)
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter

        binding.btnNext.setOnClickListener {
            Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show()
        }

        binding.voucherSelect.setOnClickListener {
            Toast.makeText(this, "Error: Action not supported in this view.", Toast.LENGTH_SHORT).show()
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
                    binding.tvTotalPrice.text = String.format(
                        Locale.getDefault(), 
                        "%dđ", 
                        state.totalPrice
                    )
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
        val sheet = VariantSelectorSheet.newInstance(
            item.product.id,
            item.selectedVariant?.id
        )

        supportFragmentManager.setFragmentResultListener(
            VariantSelectorSheet.REQUEST_KEY,
            this
        ) { _, result ->
            val productId = result.getString(VariantSelectorSheet.RESULT_PRODUCT_ID)
            val variantId = result.getString(VariantSelectorSheet.RESULT_VARIANT_ID)

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
