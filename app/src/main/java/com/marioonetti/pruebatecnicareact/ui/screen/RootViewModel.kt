package com.marioonetti.pruebatecnicareact.ui.screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class RootViewModel<S: ViewState, E: ViewEvent>(
    initialState: S
): ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    abstract fun onEvent(event: E)

    protected fun updateState(reducer: S.() -> S) {
        _uiState.value = uiState.value.reducer()
    }
}

open class ViewState
open class ViewEvent