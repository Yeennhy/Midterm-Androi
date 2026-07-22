package com.example.midterm.ui.main

import com.example.midterm.data.model.AccessibilityMode
import com.example.midterm.data.model.SeminarTask

/**
 * UI state for the Main (Control Panel) screen.
 *
 * UDF Contract:
 * - This is an immutable snapshot of everything the Main screen needs.
 * - Created by MainViewModel and emitted via StateFlow.
 * - The Activity reads it and renders accordingly — never modifies it directly.
 *
 * @property accessibilityMode Current seminar mode (ACCESSIBLE or INACCESSIBLE).
 * @property activeTask The currently active challenge task, if any.
 * @property isCompleted Whether the current task flow has been completed.
 * @property elapsedTimeMs Time elapsed in the current session (ms).
 */
data class MainUiState(
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE,
    val activeTask: SeminarTask? = null,
    val isCompleted: Boolean = false,
    val elapsedTimeMs: Long = 0L
)
