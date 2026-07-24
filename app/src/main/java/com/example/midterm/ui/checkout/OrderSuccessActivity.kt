package com.example.midterm.ui.checkout

import android.content.Intent
import android.os.Bundle
import com.example.midterm.data.ServiceLocator
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.databinding.ActivityOrderSuccessBinding
import com.example.midterm.ui.base.BaseActivity
import com.example.midterm.ui.common.applyAccessibilitySupport
import com.example.midterm.ui.common.postAnnouncement
import com.example.midterm.ui.main.MainActivity
import com.example.midterm.utils.CurrencyFormatter

class OrderSuccessActivity : BaseActivity<ActivityOrderSuccessBinding>(ActivityOrderSuccessBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val totalPaid = intent.getLongExtra(EXTRA_TOTAL_PAID, 0L)
        binding.tvTotalPaidAmount.text = CurrencyFormatter.format(totalPaid)

        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }

        // Accessibility announcement
        val session = ServiceLocator.seminarRepository.session.value
        if (session.accessibilityMode == AccessibilityMode.ACCESSIBLE) {
            binding.root.applyAccessibilitySupport("Order Confirmation Screen")
            binding.btnBackToHome.applyAccessibilitySupport("Back to home screen button")
            binding.root.postAnnouncement("Order successfully placed. Total paid is ${CurrencyFormatter.format(totalPaid)}.")
        }
    }

    companion object {
        const val EXTRA_TOTAL_PAID = "extra_total_paid"
    }
}
