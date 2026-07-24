package com.example.midterm.ui.unfriendly

import android.content.Intent
import android.os.Bundle
import com.example.midterm.databinding.ActivityUnfriendlyBinding
import com.example.midterm.ui.base.BaseActivity

class UnfriendlyActivity : BaseActivity<ActivityUnfriendlyBinding>(ActivityUnfriendlyBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding.tvPlaceholder.setOnClickListener {
            startActivity(Intent(this, CartUnfriendlyActivity::class.java))
        }
    }
}
