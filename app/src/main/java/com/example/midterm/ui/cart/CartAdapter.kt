package com.example.midterm.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.CartItem
import com.example.midterm.databinding.ItemCartProductBinding
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.groupForAccessibility
import com.example.midterm.ui.common.removeAccessibilitySupport
import com.example.midterm.utils.CurrencyFormatter

/**
 * RecyclerView Adapter for Shopping Cart product items.
 */
class CartAdapter(
    private val onToggleSelect: (CartItem) -> Unit,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onVariantClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.ViewHolder>(DiffCallback()) {

    var accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(
        private val binding: ItemCartProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem, position: Int) {
            val product = item.product
            val variant = item.selectedVariant
            val unitPrice = product.price + (variant?.extraPrice ?: 0L)
            val totalPrice = unitPrice * item.quantity

            binding.tvProductName.text = product.name
            binding.tvVariant.text = variant?.name ?: "Select Variant"
            binding.tvProductPrice.text = CurrencyFormatter.format(totalPrice)
            binding.tvQuantity.text = item.quantity.toString()

            // Checkbox binding without triggering listener loop
            binding.cbSelect.setOnCheckedChangeListener(null)
            binding.cbSelect.isChecked = item.isSelected

            binding.cbSelect.setOnClickListener {
                onToggleSelect(item)
            }

            binding.btnDecrease.setOnClickListener {
                onQuantityChange(item, -1)
            }

            binding.btnIncrease.setOnClickListener {
                onQuantityChange(item, 1)
            }

            binding.tvVariant.setOnClickListener {
                onVariantClick(item)
            }

            // Visual dimming for minimum quantity (first item & quantity == 1)
            val isFirstItemAtMin = position == 0 && item.quantity <= 1
            binding.btnDecrease.alpha = if (isFirstItemAtMin) 0.5f else 1.0f

            // ── Accessibility Configuration ──
            val stateAnnouncement = if (item.isSelected) "Checked" else "Not checked"
            val checkboxLabel = "Select ${product.name}, $stateAnnouncement"
            val variantLabel = "Select variant for ${product.name}, currently ${variant?.name ?: "none"}"
            val decLabel = if (isFirstItemAtMin) {
                "Decrease quantity for ${product.name}, minimum quantity reached"
            } else {
                "Decrease quantity for ${product.name}"
            }
            val incLabel = "Increase quantity for ${product.name}"

            if (accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                binding.cbSelect.applyAccessibilitySupport(checkboxLabel)
                binding.tvVariant.applyAccessibilitySupport(variantLabel)
                binding.btnDecrease.applyAccessibilitySupport(decLabel)
                binding.btnIncrease.applyAccessibilitySupport(incLabel)

                val cardSummary = "${product.name}, variant ${variant?.name ?: "standard"}, price ${CurrencyFormatter.format(totalPrice)}, quantity ${item.quantity}, $stateAnnouncement"
                binding.productCard.groupForAccessibility(cardSummary)
            } else {
                binding.cbSelect.removeAccessibilitySupport()
                binding.tvVariant.removeAccessibilitySupport()
                binding.btnDecrease.removeAccessibilitySupport()
                binding.btnIncrease.removeAccessibilitySupport()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.product.id == newItem.product.id &&
                    oldItem.selectedVariant?.id == newItem.selectedVariant?.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}