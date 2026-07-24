package com.example.midterm.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midterm.data.model.ProductVariant
import com.example.midterm.databinding.BottomSheetVariantSelectorBinding
import com.example.midterm.databinding.ItemProductVariantBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VariantSelectorSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetVariantSelectorBinding? = null
    private val binding get() = _binding!!

    private var variants: List<ProductVariant> = emptyList()
    private var selectedVariantId: String? = null
    private var onVariantSelectedListener: ((ProductVariant) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetVariantSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCloseSheet.setOnClickListener {
            dismiss()
        }

        val adapter = VariantAdapter(variants, selectedVariantId) { variant ->
            selectedVariantId = variant.id
        }

        binding.rvVariants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVariants.adapter = adapter

        binding.btnConfirmVariant.setOnClickListener {
            val chosen = variants.find { it.id == selectedVariantId } ?: variants.firstOrNull()
            if (chosen != null) {
                onVariantSelectedListener?.invoke(chosen)
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var onDismissListener: (() -> Unit)? = null

    fun setVariants(
        variantList: List<ProductVariant>,
        currentVariantId: String?,
        onSelected: (ProductVariant) -> Unit
    ) {
        this.variants = variantList
        this.selectedVariantId = currentVariantId
        this.onVariantSelectedListener = onSelected
    }

    fun setOnDismissListener(listener: () -> Unit) {
        this.onDismissListener = listener
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }


    private class VariantAdapter(
        private val items: List<ProductVariant>,
        private var selectedId: String?,
        private val onSelect: (ProductVariant) -> Unit
    ) : RecyclerView.Adapter<VariantAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemProductVariantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(
            private val binding: ItemProductVariantBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(variant: ProductVariant) {
                binding.tvVariantName.text = variant.name
                val isSelected = variant.id == selectedId
                binding.cbVariantSelect.isChecked = isSelected
                binding.cardVariantContainer.setCardBackgroundColor(
                    if (isSelected) 0xFFF5EFE4.toInt() else 0xFFFAF6F0.toInt()
                )



                binding.root.setOnClickListener {
                    selectedId = variant.id
                    notifyDataSetChanged()
                    onSelect(variant)
                }
            }
        }
    }

    companion object {
        fun newInstance(): VariantSelectorSheet = VariantSelectorSheet()
    }
}