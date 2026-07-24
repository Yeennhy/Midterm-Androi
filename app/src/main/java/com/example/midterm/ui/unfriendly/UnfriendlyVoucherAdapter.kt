package com.example.midterm.ui.unfriendly

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.databinding.ItemUnfriendlyVoucherBinding

class UnfriendlyVoucherAdapter(
    private val onVoucherClick: (Voucher) -> Unit
) : ListAdapter<Voucher, UnfriendlyVoucherAdapter.ViewHolder>(DiffCallback()) {

    private var selectedProductVoucherCode: String? = null
    private var selectedDeliveryVoucherCode: String? = null

    fun setSelectedVouchers(productVoucher: Voucher?, deliveryVoucher: Voucher?) {
        selectedProductVoucherCode = productVoucher?.code
        selectedDeliveryVoucherCode = deliveryVoucher?.code
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUnfriendlyVoucherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemUnfriendlyVoucherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher) {
            binding.tvVoucherValue.text = voucher.badgeText
            binding.tvVoucherTitle.text = voucher.description
            binding.tvVoucherSubtitle.text = voucher.expiryLabel
            binding.tvVoucherCode.text = voucher.code

            val isSelected = if (voucher.type == VoucherType.PRODUCT) {
                voucher.code == selectedProductVoucherCode
            } else {
                voucher.code == selectedDeliveryVoucherCode
            }

            binding.rbVoucher.isChecked = isSelected

            // Set color based on type
            val themeColor = if (voucher.type == VoucherType.PRODUCT) {
                Color.parseColor("#2D4B3D") // Dark Green
            } else {
                Color.parseColor("#9F4123") // Brownish Red
            }
            binding.leftSection.setBackgroundColor(themeColor)

            binding.root.setOnClickListener {
                onVoucherClick(voucher)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(oldItem: Voucher, newItem: Voucher): Boolean =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: Voucher, newItem: Voucher): Boolean =
            oldItem == newItem
    }
}
