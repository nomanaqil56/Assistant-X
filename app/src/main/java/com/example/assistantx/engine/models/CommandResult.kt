package com.example.assistantx.engine.models

sealed class CommandResult {
    abstract val message: String
    
    data class Success(override val message: String) : CommandResult()
    data class Error(override val message: String) : CommandResult()
}
