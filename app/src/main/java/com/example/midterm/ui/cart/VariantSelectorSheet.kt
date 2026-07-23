package com.example.midterm.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midterm.data.ServiceLocator
import com.example.midterm.databinding.BottomSheetVariantBinding
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VariantSelectorSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetVariantBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetVariantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val productId = arguments?.getString(ARG_PRODUCT_ID) ?: return
        val selectedVariantId = arguments?.getString(ARG_SELECTED_VARIANT_ID)
        
        val product = ServiceLocator.productRepository.getProductById(productId) ?: return
        
        binding.root.applyAccessibilitySupport("Select variant for ${product.name}")

        binding.rvVariants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVariants.adapter = VariantAdapter(
            variants = product.variants,
            selectedVariantId = selectedVariantId,
            onVariantClick = { variant ->
                setFragmentResult(REQUEST_KEY, bundleOf(
                    RESULT_PRODUCT_ID to productId,
                    RESULT_VARIANT_ID to variant.id
                ))
                dismiss()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "variant_selector_request"
        const val RESULT_PRODUCT_ID = "result_product_id"
        const val RESULT_VARIANT_ID = "result_variant_id"
        
        private const val ARG_PRODUCT_ID = "product_id"
        private const val ARG_SELECTED_VARIANT_ID = "selected_variant_id"

        fun newInstance(productId: String, selectedVariantId: String? = null): VariantSelectorSheet {
            val args = Bundle().apply {
                putString(ARG_PRODUCT_ID, productId)
                putString(ARG_SELECTED_VARIANT_ID, selectedVariantId)
            }
            return VariantSelectorSheet().apply {
                arguments = args
            }
        }
    }
}