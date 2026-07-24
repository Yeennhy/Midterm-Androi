package com.example.midterm.ui.main

import android.content.Intent
import android.os.Bundle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityMainBinding
import com.example.midterm.ui.accessible.AccessibleActivity
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.unfriendly.CartUnfriendlyActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnAccessibleUi.setOnClickListener {
            startMode(AccessibilityMode.ACCESSIBLE, AccessibleActivity::class.java)
        }
        binding.btnUnfriendlyUi.setOnClickListener {
            startMode(AccessibilityMode.INACCESSIBLE, CartUnfriendlyActivity::class.java)
        }
    }

    private fun startMode(mode: AccessibilityMode, target: Class<*>) {
        ServiceLocator.seminarRepository.setAccessibilityMode(mode)
        startActivity(Intent(this, target))
    }
}
