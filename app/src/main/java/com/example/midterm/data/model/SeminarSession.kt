package com.example.midterm.data.model

/**
 * Tracks the overall seminar session state.
 *
 * This is the global state store for the accessibility demo.
 * It drives whether the app operates in ACCESSIBLE or INACCESSIBLE mode,
 * which task is active, and whether the user has completed the flow.
 *
 * UDF Flow: SeminarRepository -> MainViewModel -> MainActivity observes
 * and toggles AccessibilityHelper functions based on the [accessibilityMode].
 */
data class SeminarSession(
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE,
    val activeTask: SeminarTask? = null,
    val isCompleted: Boolean = false,
    val elapsedTimeMs: Long = 0L
)

enum class AccessibilityMode {
    ACCESSIBLE,
    INACCESSIBLE
}
