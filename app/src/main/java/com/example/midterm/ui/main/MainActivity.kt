package com.example.midterm.ui.main

import android.content.Intent
import android.os.Bundle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityMainBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.cart.CartActivity
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
    }
}
