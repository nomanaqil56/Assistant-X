package com.example.assistantx.core.state

enum class AssistantState {
    IDLE,
    LISTENING,
    PROCESSING,
    PLANNING,
    EXECUTING,
    OBSERVING,
    VERIFYING,
    SPEAKING,
    COMPLETED,
    ERROR,
    WAITING_FOR_CONFIRMATION,
    PERMISSION_REQUIRED,
    UNAVAILABLE
}
