package com.example.midterm.ui.common

import android.content.res.ColorStateList
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.R
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.databinding.ItemVoucherBinding

class VoucherAdapter(
    private val onItemClick: (Voucher) -> Unit
) : ListAdapter<Voucher, VoucherAdapter.ViewHolder>(DiffCallback()) {

    var selectedVoucherCode: String? = null
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoucherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemVoucherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher) {
            val isSelected = voucher.code == selectedVoucherCode

            binding.tvBadgeText.text = voucher.discountBadge.ifEmpty { "${voucher.value}% OFF" }
            binding.tvVoucherTitle.text = voucher.title.ifEmpty { voucher.code }
            binding.tvVoucherDescription.text = voucher.description
            binding.tvVoucherExpiry.text = voucher.expiryText
            binding.tvVoucherCode.text = voucher.code

            // Dynamic color & icon: Green (#2E7D32) + Truck for DELIVERY, Red (#A44222) + Tag for PRODUCT
            val isDelivery = voucher.type == VoucherType.DELIVERY
            val badgeColor = if (isDelivery) 0xFF2E7D32.toInt() else 0xFFA44222.toInt()
            val iconRes = if (isDelivery) R.drawable.ic_truck else R.drawable.ic_voucher_tag

            binding.layoutLeftBadge.backgroundTintList = ColorStateList.valueOf(badgeColor)
            binding.ivVoucherIcon.setImageResource(iconRes)
            binding.ivVoucherIcon.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            binding.ivVoucherIcon.contentDescription = null

            // RadioButton selection state & listener
            binding.rbSelectVoucher.setOnCheckedChangeListener(null)
            binding.rbSelectVoucher.isChecked = isSelected

            // Card highlight stroke on selection
            val cardView = binding.root as? com.google.android.material.card.MaterialCardView
            if (isSelected) {
                cardView?.strokeColor = 0xFFA44222.toInt()
                cardView?.strokeWidth = 4
            } else {
                cardView?.strokeColor = 0xFFE5DEC9.toInt()
                cardView?.strokeWidth = 2
            }

            binding.root.setOnClickListener {
                onItemClick(voucher)
            }

            binding.rbSelectVoucher.setOnClickListener {
                onItemClick(voucher)
            }

            if (accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                val stateText = if (isSelected) "Selected" else "Not selected"
                val label = "${voucher.title}, ${binding.tvBadgeText.text}, ${voucher.description}, code ${voucher.code}, $stateText"
                binding.root.applyAccessibilitySupport(label)
            } else {
                binding.root.removeAccessibilitySupport()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(old: Voucher, new: Voucher) = old.code == new.code
        override fun areContentsTheSame(old: Voucher, new: Voucher) = old == new
    }
}