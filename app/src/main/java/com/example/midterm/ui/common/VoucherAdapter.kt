package com.example.midterm.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.R
import com.example.midterm.data.model.Voucher
import com.example.midterm.data.model.VoucherType
import com.example.midterm.databinding.ItemVoucherBinding

class VoucherAdapter(
    private val onItemClick: (Voucher) -> Unit
) : ListAdapter<Voucher, VoucherAdapter.ViewHolder>(DiffCallback()) {

    private var selectedCode: String? = null

    fun setSelectedCode(code: String?) {
        if (selectedCode == code) return
        selectedCode = code
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoucherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).code == selectedCode)
        // TODO: Use AccessibilityHelper to apply/remove labels here
    }

    inner class ViewHolder(
        private val binding: ItemVoucherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher, isSelected: Boolean) {
            binding.ivVoucherIcon.setImageResource(
                if (voucher.type == VoucherType.DELIVERY) R.drawable.ic_truck else R.drawable.ic_ticket
            )
            binding.tvBadge.text = voucher.badgeText
            binding.tvTitle.text = voucher.title
            binding.tvDescription.text = voucher.description
            binding.tvExpiry.text = voucher.expiryLabel
            binding.tvCode.text = voucher.code
            binding.radioDot.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
            binding.root.setOnClickListener { onItemClick(voucher) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(old: Voucher, new: Voucher) = old.code == new.code
        override fun areContentsTheSame(old: Voucher, new: Voucher) = old == new
    }
}
