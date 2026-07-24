package com.example.midterm.ui.checkout

import com.example.midterm.ui.main.MainActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.R
import com.example.midterm.databinding.ActivityOrderSuccessBinding
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding
    private lateinit var viewModel: OrderSuccessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val initialState = OrderSuccessUiState(
            itemCount = intent.getIntExtra(EXTRA_ITEM_COUNT, 0),
            subtotal = intent.getLongExtra(EXTRA_SUBTOTAL, 0L),
            shippingFeePostDiscount = intent.getLongExtra(EXTRA_SHIPPING_FEE, 0L),
            total = intent.getLongExtra(EXTRA_TOTAL, 0L),
            orderID = intent.getStringExtra(EXTRA_ORDER_ID) ?: ""
        )

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { OrderSuccessViewModel(initialState) }
        )[OrderSuccessViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        binding.finishDemo.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.orderId.text = "ORDER #${state.orderID}"
                    binding.successSummaryCount.text = "Items (${state.itemCount})"
                    binding.successSummaryCountValue.text = CurrencyFormatter.format(state.subtotal)
                    val isFreeShipping = state.shippingFeePostDiscount == 0L
                    binding.successSummaryShippingValue.text =
                        if (isFreeShipping) "FREE"
                        else CurrencyFormatter.format(state.shippingFeePostDiscount)
                    binding.successSummaryShippingValue.setTextColor(
                        ContextCompat.getColor(
                            this@OrderSuccessActivity,
                            if (isFreeShipping) R.color.themic_green else R.color.black
                        )
                    )
                    binding.successSummaryTotalValue.text = CurrencyFormatter.format(state.total)
                }
            }
        }
    }

    companion object {
        private const val EXTRA_ITEM_COUNT = "extra_item_count"
        private const val EXTRA_SUBTOTAL = "extra_subtotal"
        private const val EXTRA_SHIPPING_FEE = "extra_shipping_fee"
        private const val EXTRA_TOTAL = "extra_total"
        private const val EXTRA_ORDER_ID = "extra_order_id"

        fun buildIntent(
            context: Context,
            itemCount: Int,
            subtotal: Long,
            shippingFeePostDiscount: Long,
            total: Long,
            orderID: String
        ) = Intent(context, OrderSuccessActivity::class.java).apply {
            putExtra(EXTRA_ITEM_COUNT, itemCount)
            putExtra(EXTRA_SUBTOTAL, subtotal)
            putExtra(EXTRA_SHIPPING_FEE, shippingFeePostDiscount)
            putExtra(EXTRA_TOTAL, total)
            putExtra(EXTRA_ORDER_ID, orderID)
        }
    }
}