package com.example.assistantx.engine

import android.content.Context
import com.example.assistantx.core.state.AssistantState
import com.example.assistantx.core.state.AssistantStateManager
import com.example.assistantx.engine.models.CommandResult
import kotlinx.coroutines.delay

class CommandEngine(
    context: Context,
    private val stateManager: AssistantStateManager
) {
    private val parser = CommandParser()
    private val validator = ActionValidator(context)
    private val executor = ActionExecutor(context)
    private val ttsManager = TextToSpeechManager(context)

    suspend fun processCommand(text: String): CommandResult {
        stateManager.updateState(AssistantState.PROCESSING)
        delay(300) // Simulate processing time for UX
        
        stateManager.updateState(AssistantState.PLANNING)
        val intent = parser.parse(text)
        
        stateManager.updateState(AssistantState.EXECUTING)
        val action = validator.validate(intent)
        val result = executor.execute(action)
        
        stateManager.updateState(AssistantState.SPEAKING)
        ttsManager.speak(result.message)
        
        // Return to IDLE after a brief completion state
        stateManager.updateState(AssistantState.COMPLETED)
        delay(500)
        stateManager.updateState(AssistantState.IDLE)
        
        return result
    }
    
    fun shutdown() {
        ttsManager.shutdown()
    }
}
