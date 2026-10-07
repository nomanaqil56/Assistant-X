package com.example.assistantx.engine

import android.content.Context
import com.example.assistantx.engine.models.CommandResult
import kotlinx.coroutines.delay

class CommandEngine(context: Context) {
    private val parser = CommandParser()
    private val validator = ActionValidator(context)
    private val executor = ActionExecutor(context)
    private val ttsManager = TextToSpeechManager(context)

    suspend fun processCommand(text: String): CommandResult {
        delay(600) // Simulate processing time for UX
        val intent = parser.parse(text)
        val action = validator.validate(intent)
        val result = executor.execute(action)
        ttsManager.speak(result.message)
        return result
    }
    
    fun shutdown() {
        ttsManager.shutdown()
    }
}
