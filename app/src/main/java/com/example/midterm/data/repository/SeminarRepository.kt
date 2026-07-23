package com.example.midterm.data.repository

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.SeminarSession
import com.example.midterm.data.model.SeminarTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global session state repository for the accessibility seminar.
 *
 * UDF Role: This is the single source of truth for whether the app is
 * in ACCESSIBLE or INACCESSIBLE mode, which task is active, and the
 * completion state. Feature ViewModels (Cart, Voucher, Checkout, ...)
 * observe this directly to react to mode changes via shared StateFlows.
 *
 * The toggleAccessibilityMode() method is the central "Break / Fix" trigger
 * that AccessibilityHelper uses to dynamically adjust all UI attributes.
 */
class SeminarRepository {

    private val _session = MutableStateFlow(SeminarSession())
    val session: StateFlow<SeminarSession> = _session.asStateFlow()

    fun setActiveTask(task: SeminarTask) {
        _session.value = _session.value.copy(activeTask = task, isCompleted = false)
    }

    fun toggleAccessibilityMode() {
        val current = _session.value.accessibilityMode
        _session.value = _session.value.copy(
            accessibilityMode = if (current == AccessibilityMode.ACCESSIBLE)
                AccessibilityMode.INACCESSIBLE else AccessibilityMode.ACCESSIBLE
        )
    }

    fun setAccessibilityMode(mode: AccessibilityMode) {
        _session.value = _session.value.copy(accessibilityMode = mode)
    }

    fun markCompleted() {
        _session.value = _session.value.copy(isCompleted = true)
    }

    fun updateElapsedTime(ms: Long) {
        _session.value = _session.value.copy(elapsedTimeMs = ms)
    }
}
