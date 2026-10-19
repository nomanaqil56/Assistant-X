package com.example.assistantx.core.history

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CommandHistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val command: String,
    val resultMessage: String,
    val isSuccess: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Local persistent command history repository using application private preferences.
 * Stores executed command queries, execution outcomes, and real timestamps.
 */
class CommandHistoryRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "assistantx_command_history"
        private const val KEY_HISTORY = "history_entries"
        private const val MAX_HISTORY_ITEMS = 50
    }

    fun getHistory(): List<CommandHistoryEntry> {
        val jsonString = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<CommandHistoryEntry>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CommandHistoryEntry(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        command = obj.optString("command", ""),
                        resultMessage = obj.optString("resultMessage", ""),
                        isSuccess = obj.optBoolean("isSuccess", true),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    fun addEntry(command: String, resultMessage: String, isSuccess: Boolean): CommandHistoryEntry {
        val entry = CommandHistoryEntry(
            command = command,
            resultMessage = resultMessage,
            isSuccess = isSuccess,
            timestamp = System.currentTimeMillis()
        )

        val current = getHistory().toMutableList()
        // Prepend new entry
        current.add(0, entry)
        if (current.size > MAX_HISTORY_ITEMS) {
            current.removeAt(current.size - 1)
        }

        saveList(current)
        return entry
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveList(entries: List<CommandHistoryEntry>) {
        val jsonArray = JSONArray()
        for (item in entries) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("command", item.command)
                put("resultMessage", item.resultMessage)
                put("isSuccess", item.isSuccess)
                put("timestamp", item.timestamp)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }
}
