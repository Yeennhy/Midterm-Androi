package com.example.midterm.ui.unfriendly;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.midterm.data.ServiceLocator;
import com.example.midterm.data.model.CartItem;
import com.example.midterm.databinding.ActivityUnfriendlyCartBinding;
import com.example.midterm.ui.base.ViewModelFactory;
import com.example.midterm.ui.cart.CartAdapter;
import com.example.midterm.ui.cart.CartUiState;
import com.example.midterm.ui.cart.CartViewModel;
import com.example.midterm.ui.cart.VariantSelectorSheet;

import java.util.Locale;

/**
 * An Activity that demonstrates a poorly accessible (unfriendly) shopping cart UI.
 * 
 * Features of "unfriendliness":
 * 1. Missing or unhelpful content descriptions on interactive elements.
 * 2. Silent failures or generic feedback for complex actions.
 * 3. Lack of focus grouping or logical traversal order for screen readers.
 * 4. Hard-to-touch targets or ambiguous icons.
 * 
 * Note: This implementation is in Java as part of a mixed-language project demonstration.
 */
public class CartUnfriendlyActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private ActivityUnfriendlyCartBinding binding;
    private CartViewModel viewModel;
    private CartAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUnfriendlyCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel using the manual Factory and ServiceLocator
        // In this project, dependencies are injected manually to keep it approachable.
        viewModel = new ViewModelProvider(
                this,
                new ViewModelFactory<>(() -> new CartViewModel(ServiceLocator.INSTANCE.getCartRepository()))
        ).get(CartViewModel.class);

        setupViews();
        updateUi();
    }

    private void setupViews() {
        // Setup RecyclerView with the existing CartAdapter
        adapter = new CartAdapter(this);
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(adapter);

        // Next Button Navigation
        binding.btnNext.setOnClickListener(v -> {
            // In a real application, this would navigate to a CheckoutActivity
            Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
        });

        // Voucher Selection (Intentional "unfriendly" behavior: generic error)
        binding.voucherSelect.setOnClickListener(v -> {
            Toast.makeText(this, "Error: Action not supported in this view.", Toast.LENGTH_SHORT).show();
        });

        // --- Intentional Accessibility Issues (Unfriendly Mode) ---
        
        // Remove content description from the main action button (Next button)
        // This makes the button read as just "Button" or "Unlabelled" by TalkBack.
        binding.btnNext.setContentDescription(null); 
        
        // The progress icons in 'activity_unfriendly_cart.xml' already lack content descriptions, 
        // making them "invisible" to screen readers as meaningful progress indicators.
    }

    private void updateUi() {
        if (viewModel != null && adapter != null) {
            CartUiState state = viewModel.getUiState().getValue();
            adapter.submitList(state.getCartItems());

            // Update subtotal and total price
            binding.tvSubtotal.setText(String.format(Locale.getDefault(), "Subtotal (%d items)", state.getSelectedCount()));
            binding.tvTotalPrice.setText(String.format(Locale.getDefault(), "%dđ", state.getTotalPrice()));
        }
    }

    // --- CartItemListener Implementation ---
    // These methods handle interactions from the CartAdapter's items.

    @Override
    public void onIncreaseQuantity(CartItem item) {
        viewModel.increaseQuantity(
            item.getProduct().getId(), 
            item.getSelectedVariant() != null ? item.getSelectedVariant().getId() : null
        );
        updateUi();
    }

    @Override
    public void onDecreaseQuantity(CartItem item) {
        viewModel.decreaseQuantity(
            item.getProduct().getId(), 
            item.getSelectedVariant() != null ? item.getSelectedVariant().getId() : null
        );
        updateUi();
    }

    @Override
    public void onVariantClick(CartItem item) {
        VariantSelectorSheet sheet = VariantSelectorSheet.Companion.newInstance(
            item.getProduct().getId(),
            item.getSelectedVariant() != null ? item.getSelectedVariant().getId() : null
        );
        
        getSupportFragmentManager().setFragmentResultListener(
            VariantSelectorSheet.REQUEST_KEY,
            this,
            (requestKey, result) -> {
                String productId = result.getString(VariantSelectorSheet.RESULT_PRODUCT_ID);
                String variantId = result.getString(VariantSelectorSheet.RESULT_VARIANT_ID);
                
                // Find the new variant from the product
                com.example.midterm.data.model.Product product = ServiceLocator.INSTANCE.getProductRepository().getProductById(productId);
                if (product != null) {
                    for (com.example.midterm.data.model.ProductVariant v : product.getVariants()) {
                        if (v.getId().equals(variantId)) {
                            viewModel.changeVariant(productId, item.getSelectedVariant() != null ? item.getSelectedVariant().getId() : null, v);
                            break;
                        }
                    }
                }
            }
        );
        
        sheet.show(getSupportFragmentManager(), "VariantSelector");
    }

    @Override
    public void onItemClick(CartItem item) {
        // Toggle selection state in the repository
        viewModel.toggleSelection(
            item.getProduct().getId(), 
            item.getSelectedVariant() != null ? item.getSelectedVariant().getId() : null
        );
        updateUi();
    }
}
