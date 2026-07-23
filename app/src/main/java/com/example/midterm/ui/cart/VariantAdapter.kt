package com.example.midterm.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.databinding.ItemVariantBinding

class VariantAdapter(
    private val variants: List<ProductVariant>,
    private val selectedVariantId: String?,
    private val onVariantClick: (ProductVariant) -> Unit
) : RecyclerView.Adapter<VariantAdapter.VariantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val binding = ItemVariantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VariantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        holder.bind(variants[position])
    }

    override fun getItemCount(): Int = variants.size

    inner class VariantViewHolder(private val binding: ItemVariantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(variant: ProductVariant) {
            binding.txtVariant.text = variant.name
            
            // Set the selected state for the background selector
            binding.root.isSelected = variant.id == selectedVariantId

            binding.root.setOnClickListener {
                onVariantClick(variant)
            }
        }
    }
}
