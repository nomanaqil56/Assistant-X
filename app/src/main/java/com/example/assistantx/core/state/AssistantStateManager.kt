package com.example.assistantx.core.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssistantStateManager {
    private val _currentState = MutableStateFlow(AssistantState.IDLE)
    val currentState: StateFlow<AssistantState> = _currentState.asStateFlow()

    fun updateState(newState: AssistantState) {
        _currentState.value = newState
    }
}
