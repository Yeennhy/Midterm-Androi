package com.example.midterm.ui.cart

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityCartBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.base.ViewModelFactory
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.groupForAccessibility
import com.example.midterm.ui.common.makeLiveRegion
import com.example.midterm.ui.common.postAnnouncement
import com.example.midterm.ui.common.pruneFromAccessibilityTree
import com.example.midterm.ui.common.removeAccessibilitySupport
import com.example.midterm.ui.common.restoreToAccessibilityTree
import com.example.midterm.ui.common.setAccessibilityTraversal
import com.example.midterm.utils.CurrencyFormatter
import kotlinx.coroutines.launch

class CartActivity : BaseActivity<ActivityCartBinding>(ActivityCartBinding::inflate) {

    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepository = ServiceLocator.cartRepository
        val seminarRepository = ServiceLocator.seminarRepository
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { CartViewModel(cartRepository, seminarRepository) }
        )[CartViewModel::class.java]

        setupViews()
        observeState()
    }

    private fun setupViews() {
        // ── Traversal order demo (§3.2) ──────────────────────
        // In a real product card, the XML order might put price before
        // name. This forces a logical sequence: Name → Price → Quantity.
        // The container is grouped (see observeState), so these traversal
        // hints kick in if grouping is ever removed.
        binding.root.makeLiveRegion()

        // ── Live region for cart status announcements (§3.1) ──
        // A hidden status TextView (add to layout if missing) would be
        // wired here. For the demo, we use postAnnouncement directly.
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state.accessibilityMode) {
                        AccessibilityMode.ACCESSIBLE -> applyFixMode()
                        AccessibilityMode.INACCESSIBLE -> applyBreakMode()
                    }
                }
            }
        }
    }

    /**
     * ACCESSIBLE mode — fully semantic UI.
     *
     * Demonstrates:
     * 1. **Focus grouping** — product cards are announced as one unit.
     * 2. **Traversal order** — name → price → quantity (logical sequence).
     * 3. **Live region** — the container announces cart changes.
     * 4. **Root label** — screen description for context.
     */
    private fun applyFixMode() {
        // Label the screen root
        binding.root.applyAccessibilitySupport(label = "Shopping cart screen")

        // Restore any container that was pruned in BREAK mode
        binding.root.restoreToAccessibilityTree()

        // ── Focus grouping on the cart container (§3.2) ───────
        // Instead of swiping through every child (image, name, price,
        // quantity, checkbox), TalkBack reads ONE cohesive announcement
        // for the whole list section.
        binding.root.groupForAccessibility(
            label = "Cart items. ${viewModel.uiState.value.cartItems.size} products."
        )

        // ── Live region for dynamic announcements (§3.1) ──────
        // When items are added or removed, TalkBack automatically
        // reads the total without moving focus.
        val totalText = "Total: ${CurrencyFormatter.format(viewModel.uiState.value.totalPrice)}"
        binding.root.postAnnouncement("Cart updated. $totalText")
    }

    /**
     * INACCESSIBLE mode — deliberately broken UI.
     *
     * Demonstrates:
     * 1. **Tree pruning** — the entire cart list is removed from the
     *    accessibility tree. TalkBack swipes and hears nothing, simulating
     *    a screen-reader that is "lost".
     * 2. **Individual views stripped** — root loses its label and target size.
     */
    private fun applyBreakMode() {
        // Prune the entire cart container → TalkBack skips the whole area
        binding.root.pruneFromAccessibilityTree()

        // Also strip the root's individual accessibility metadata
        binding.root.removeAccessibilitySupport()
    }
}
