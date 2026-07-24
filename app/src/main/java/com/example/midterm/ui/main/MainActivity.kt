package com.example.midterm.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityMainBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.cart.CartActivity
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.unfriendly.UnfriendlyActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnAccessibleUi.setOnClickListener {
            ServiceLocator.seminarRepository.setAccessibilityMode(AccessibilityMode.ACCESSIBLE)
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.btnUnfriendlyUi.setOnClickListener {
            ServiceLocator.seminarRepository.setAccessibilityMode(AccessibilityMode.INACCESSIBLE)
            startActivity(Intent(this, UnfriendlyActivity::class.java))
        }

        setupAccessibility()
    }

    private fun setupAccessibility() {
        ViewCompat.setAccessibilityHeading(binding.tvLandingTitle, true)

        binding.btnAccessibleUi.applyAccessibilitySupport(
            label = "Accessible UI. Continue with full accessibility support enabled."
        )
        binding.btnUnfriendlyUi.applyAccessibilitySupport(
            label = "Unfriendly UI. Continue with accessibility support intentionally disabled, for demo purposes."
        )
    }
}
