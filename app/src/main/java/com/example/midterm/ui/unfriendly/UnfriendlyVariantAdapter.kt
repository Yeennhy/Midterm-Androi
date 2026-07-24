package com.example.midterm.ui.unfriendly

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.databinding.ItemVariantBinding

class UnfriendlyVariantAdapter(
    private val variants: List<ProductVariant>,
    private val selectedVariantId: String?,
    private val onVariantClick: (ProductVariant) -> Unit
) : RecyclerView.Adapter<UnfriendlyVariantAdapter.UnfriendlyVariantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnfriendlyVariantViewHolder {
        val binding = ItemVariantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UnfriendlyVariantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnfriendlyVariantViewHolder, position: Int) {
        holder.bind(variants[position])
    }

    override fun getItemCount(): Int = variants.size

    inner class UnfriendlyVariantViewHolder(private val binding: ItemVariantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(variant: ProductVariant) {
            binding.txtVariant.text = variant.name
            
            val isSelected = variant.id == selectedVariantId
            // Set the selected state for the background selector
            binding.root.isSelected = isSelected
            
            // Change text color when selected
            if (isSelected) {
                binding.txtVariant.setTextColor(Color.parseColor("#9F4123"))
            } else {
                binding.txtVariant.setTextColor(Color.BLACK)
            }

            binding.root.setOnClickListener {
                onVariantClick(variant)
            }
        }
    }
}
