package com.example.assistantx.ui.models

import java.util.UUID

enum class MessageRole {
    USER,
    ASSISTANT
}

enum class MessageStatus {
    NONE,
    PENDING,
    SUCCESS,
    ERROR
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String,
    val status: MessageStatus = MessageStatus.NONE
)
