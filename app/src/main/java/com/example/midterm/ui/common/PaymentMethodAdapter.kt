package com.example.midterm.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.PaymentMethod
import com.example.midterm.databinding.ItemPaymentMethodBinding

class PaymentMethodAdapter(
    private val onItemClick: (PaymentMethod) -> Unit
) : ListAdapter<PaymentMethod, PaymentMethodAdapter.ViewHolder>(DiffCallback()) {

    var selectedMethodId: String? = "card"
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, item.id == selectedMethodId, accessibilityMode)
    }

    inner class ViewHolder(
        private val binding: ItemPaymentMethodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(method: PaymentMethod, isSelected: Boolean, mode: AccessibilityMode) {
            binding.tvMethodName.text = method.name
            binding.tvMethodSubtitle.text = method.subtitle
            binding.ivMethodIcon.setImageResource(method.iconResId)
            binding.radioDot.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE

            val cardView = binding.cardPaymentMethod
            if (isSelected) {
                cardView.strokeColor = 0xFFA44222.toInt()
                cardView.strokeWidth = 3
            } else {
                cardView.strokeColor = 0xFFF0EBE1.toInt()
                cardView.strokeWidth = 1
            }

            binding.root.setOnClickListener { onItemClick(method) }

            if (mode == AccessibilityMode.ACCESSIBLE) {
                val stateText = if (isSelected) "Selected" else "Not selected"
                binding.root.applyAccessibilitySupport("${method.name}, ${method.subtitle}, $stateText")
            } else {
                binding.root.removeAccessibilitySupport()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PaymentMethod>() {
        override fun areItemsTheSame(old: PaymentMethod, new: PaymentMethod) = old.id == new.id
        override fun areContentsTheSame(old: PaymentMethod, new: PaymentMethod) = old == new
    }
}