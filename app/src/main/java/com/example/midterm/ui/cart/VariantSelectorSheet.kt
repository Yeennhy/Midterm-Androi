package com.example.midterm.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.midterm.databinding.BottomSheetVariantSelectorBinding

class VariantSelectorSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetVariantSelectorBinding? = null
    private val binding get() = _binding!!

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
        // TODO: Bind product variants and handle selection using AccessibilityHelper
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: String): VariantSelectorSheet {
            val args = Bundle().apply {
                putString(ARG_PRODUCT_ID, productId)
            }
            return VariantSelectorSheet().apply {
                arguments = args
            }
        }
    }
}