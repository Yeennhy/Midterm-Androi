package com.example.midterm.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.databinding.ItemPaymentMethodBinding

class PaymentMethodAdapter(
    private val onItemClick: (PaymentMethod) -> Unit
) : ListAdapter<PaymentMethod, PaymentMethodAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        // TODO: Use AccessibilityHelper to apply/remove labels here
    }

    inner class ViewHolder(
        private val binding: ItemPaymentMethodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(method: PaymentMethod) {
            binding.root.setOnClickListener { onItemClick(method) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PaymentMethod>() {
        override fun areItemsTheSame(old: PaymentMethod, new: PaymentMethod) = old.id == new.id
        override fun areContentsTheSame(old: PaymentMethod, new: PaymentMethod) = old == new
    }
}