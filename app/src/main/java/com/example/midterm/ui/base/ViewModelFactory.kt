package com.example.midterm.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Manual Dependency Injection factory for ViewModels.
 *
 * ## Why not Hilt/Koin/Dagger?
 * This project deliberately avoids DI frameworks to keep the codebase
 * approachable for students. Manual constructor injection makes dependencies
 * explicit — you can see exactly what every ViewModel needs by looking at
 * its constructor.
 *
 * ## Usage
 * ```kotlin
 * val vm = ViewModelProvider(
 *     this,
 *     ViewModelFactory { CartViewModel(cartRepo, seminarRepo) }
 * )[CartViewModel::class.java]
 * ```
 *
 * @param T The concrete ViewModel type being created.
 * @param creator A lambda that constructs the ViewModel with all its deps.
 */
class ViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return creator() as VM
    }
}
