package com.example.assistantx.core.conversation

import com.example.assistantx.core.history.CommandHistoryEntry
import com.example.assistantx.ui.models.Message
import java.util.UUID

/**
 * Represents a discrete conversational session in Assistant X.
 * Encapsulates its messages and session-specific command history.
 */
data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Conversation",
    val messages: List<Message> = emptyList(),
    val commandHistory: List<CommandHistoryEntry> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
