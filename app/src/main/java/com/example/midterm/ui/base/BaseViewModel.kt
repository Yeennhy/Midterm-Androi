package com.example.midterm.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T>(initialState: T) : ViewModel() {

    protected val _uiState: MutableStateFlow<T> = MutableStateFlow(initialState)
    val uiState: StateFlow<T> = _uiState.asStateFlow()

    protected fun updateState(transform: (T) -> T) {
        _uiState.value = transform(_uiState.value)
    }
}
