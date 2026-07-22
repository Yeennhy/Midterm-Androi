package com.example.midterm.ui.voucher

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.data.repository.VoucherRepository
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel for the Voucher screen.
 *
 * UDF Flow:
 *   UI Event (select code) → VoucherViewModel → VoucherRepository validation →
 *   StateFlow emission → VoucherUiState update → UI re-render
 *
 * Manual DI: Both repositories are injected via constructor (ViewModelFactory).
 */
class VoucherViewModel(
    private val voucherRepository: VoucherRepository,
    private val seminarRepository: SeminarRepository
) : BaseViewModel<VoucherUiState>(VoucherUiState()) {

    init {
        viewModelScope.launch {
            // Observe session to react to accessibility mode changes
            seminarRepository.session.collect { session ->
                updateState {
                    it.copy(
                        vouchers = voucherRepository.getVouchers(),
                        accessibilityMode = session.accessibilityMode
                    )
                }
            }
        }
    }

    fun applyVoucher(code: String, orderTotal: Long) {
        val validated = voucherRepository.validateVoucher(code, orderTotal)
        updateState { it.copy(appliedVoucher = validated) }
    }

    fun removeVoucher() {
        updateState { it.copy(appliedVoucher = null) }
    }
}
