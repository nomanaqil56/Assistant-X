package com.example.assistantx.core.conversation

import android.content.Context
import android.content.SharedPreferences
import com.example.assistantx.core.history.CommandHistoryEntry
import com.example.assistantx.ui.models.Message
import com.example.assistantx.ui.models.MessageRole
import com.example.assistantx.ui.models.MessageStatus
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Persistent repository for multi-session conversations and their session-specific command histories.
 * Stored locally in sandboxed application preferences.
 */
class ConversationRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "assistantx_conversations_v1"
        private const val KEY_CONVERSATIONS = "conversations_list"
        private const val KEY_ACTIVE_ID = "active_conversation_id"
        private const val MAX_CONVERSATIONS = 50
    }

    /**
     * Retrieve all saved conversations, sorted newest first.
     */
    fun getAllConversations(): List<Conversation> {
        val jsonString = prefs.getString(KEY_CONVERSATIONS, null) ?: return emptyList()
        val list = mutableListOf<Conversation>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(deserializeConversation(obj))
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list.sortedByDescending { it.updatedAt }
    }

    fun getConversation(id: String): Conversation? {
        return getAllConversations().firstOrNull { it.id == id }
    }

    fun getActiveConversationId(): String? {
        return prefs.getString(KEY_ACTIVE_ID, null)
    }

    fun setActiveConversationId(id: String?) {
        prefs.edit().apply {
            if (id != null) {
                putString(KEY_ACTIVE_ID, id)
            } else {
                remove(KEY_ACTIVE_ID)
            }
            apply()
        }
    }

    /**
     * Save or update an existing conversation.
     */
    fun saveConversation(conversation: Conversation) {
        val all = getAllConversations().toMutableList()
        val index = all.indexOfFirst { it.id == conversation.id }
        if (index != -1) {
            all[index] = conversation
        } else {
            all.add(0, conversation)
        }

        if (all.size > MAX_CONVERSATIONS) {
            all.removeAt(all.size - 1)
        }

        saveAll(all)
    }

    /**
     * Create and persist a new conversation session.
     */
    fun createConversation(title: String = "New Conversation"): Conversation {
        val newConv = Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            messages = emptyList(),
            commandHistory = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveConversation(newConv)
        setActiveConversationId(newConv.id)
        return newConv
    }

    /**
     * Delete a conversation by ID.
     */
    fun deleteConversation(id: String) {
        val all = getAllConversations().filterNot { it.id == id }
        saveAll(all)
        if (getActiveConversationId() == id) {
            setActiveConversationId(all.firstOrNull()?.id)
        }
    }

    /**
     * Clear all saved conversations and active session pointer.
     */
    fun clearAll() {
        prefs.edit()
            .remove(KEY_CONVERSATIONS)
            .remove(KEY_ACTIVE_ID)
            .apply()
    }

    private fun saveAll(conversations: List<Conversation>) {
        val array = JSONArray()
        for (conv in conversations) {
            array.put(serializeConversation(conv))
        }
        prefs.edit().putString(KEY_CONVERSATIONS, array.toString()).apply()
    }

    private fun serializeConversation(conv: Conversation): JSONObject {
        val obj = JSONObject()
        obj.put("id", conv.id)
        obj.put("title", conv.title)
        obj.put("createdAt", conv.createdAt)
        obj.put("updatedAt", conv.updatedAt)

        // Messages
        val messagesArray = JSONArray()
        for (msg in conv.messages) {
            val msgObj = JSONObject().apply {
                put("id", msg.id)
                put("role", msg.role.name)
                put("text", msg.text)
                put("status", msg.status.name)
            }
            messagesArray.put(msgObj)
        }
        obj.put("messages", messagesArray)

        // Command History
        val historyArray = JSONArray()
        for (entry in conv.commandHistory) {
            val hObj = JSONObject().apply {
                put("id", entry.id)
                put("command", entry.command)
                put("resultMessage", entry.resultMessage)
                put("isSuccess", entry.isSuccess)
                put("timestamp", entry.timestamp)
            }
            historyArray.put(hObj)
        }
        obj.put("commandHistory", historyArray)

        return obj
    }

    private fun deserializeConversation(obj: JSONObject): Conversation {
        val id = obj.optString("id", UUID.randomUUID().toString())
        val title = obj.optString("title", "New Conversation")
        val createdAt = obj.optLong("createdAt", System.currentTimeMillis())
        val updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())

        val messages = mutableListOf<Message>()
        val messagesArray = obj.optJSONArray("messages")
        if (messagesArray != null) {
            for (i in 0 until messagesArray.length()) {
                val mObj = messagesArray.getJSONObject(i)
                val roleStr = mObj.optString("role", MessageRole.USER.name)
                val statusStr = mObj.optString("status", MessageStatus.NONE.name)
                messages.add(
                    Message(
                        id = mObj.optString("id", UUID.randomUUID().toString()),
                        role = try { MessageRole.valueOf(roleStr) } catch (_: Exception) { MessageRole.USER },
                        text = mObj.optString("text", ""),
                        status = try { MessageStatus.valueOf(statusStr) } catch (_: Exception) { MessageStatus.NONE }
                    )
                )
            }
        }

        val commandHistory = mutableListOf<CommandHistoryEntry>()
        val historyArray = obj.optJSONArray("commandHistory")
        if (historyArray != null) {
            for (i in 0 until historyArray.length()) {
                val hObj = historyArray.getJSONObject(i)
                commandHistory.add(
                    CommandHistoryEntry(
                        id = hObj.optString("id", UUID.randomUUID().toString()),
                        command = hObj.optString("command", ""),
                        resultMessage = hObj.optString("resultMessage", ""),
                        isSuccess = hObj.optBoolean("isSuccess", true),
                        timestamp = hObj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        }

        return Conversation(
            id = id,
            title = title,
            messages = messages,
            commandHistory = commandHistory,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
