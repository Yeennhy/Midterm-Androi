package com.example.midterm.ui.main

import androidx.lifecycle.viewModelScope
import com.example.midterm.data.repository.SeminarRepository
import com.example.midterm.ui.base.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel for the Main (Control Panel) screen.
 *
 * UDF Flow:
 *   1. User taps "Toggle" → MainActivity calls toggleAccessibility()
 *   2. MainViewModel → SeminarRepository.toggleAccessibilityMode()
 *   3. SeminarRepository emits new SeminarSession
 *   4. MainViewModel maps it to MainUiState → StateFlow emits
 *   5. MainActivity observes and updates UI + calls AccessibilityHelper
 *
 * Manual DI: SeminarRepository is injected via constructor by ViewModelFactory.
 */
class MainViewModel(
    private val seminarRepository: SeminarRepository
) : BaseViewModel<MainUiState>(MainUiState()) {

    init {
        viewModelScope.launch {
            seminarRepository.session.collect { session ->
                updateState {
                    it.copy(
                        accessibilityMode = session.accessibilityMode,
                        activeTask = session.activeTask,
                        isCompleted = session.isCompleted,
                        elapsedTimeMs = session.elapsedTimeMs
                    )
                }
            }
        }
    }

    fun toggleAccessibility() {
        seminarRepository.toggleAccessibilityMode()
    }
}
