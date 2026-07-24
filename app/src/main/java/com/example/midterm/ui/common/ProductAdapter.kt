package com.example.midterm.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.Product
import com.example.midterm.databinding.ItemProductBinding
import com.example.midterm.utils.CurrencyFormatter

class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ViewHolder>(DiffCallback()) {

    var accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.root.setOnClickListener { onItemClick(product) }

            if (accessibilityMode == AccessibilityMode.ACCESSIBLE) {
                val label = "${product.name}, price ${CurrencyFormatter.format(product.price)}, category ${product.category}"
                binding.root.applyAccessibilitySupport(label)
            } else {
                binding.root.removeAccessibilitySupport()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(old: Product, new: Product) = old.id == new.id
        override fun areContentsTheSame(old: Product, new: Product) = old == new
    }
}