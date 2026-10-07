package com.example.assistantx.engine

import android.content.Context
import android.content.Intent as AndroidIntent
import android.content.ActivityNotFoundException
import com.example.assistantx.engine.models.Action
import com.example.assistantx.engine.models.CommandResult

class ActionExecutor(private val context: Context) {
    
    fun execute(action: Action): CommandResult {
        return when (action) {
            is Action.LaunchApp -> {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(action.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(AndroidIntent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(launchIntent)
                        CommandResult.Success("Opening ${action.appLabel}...")
                    } catch (e: ActivityNotFoundException) {
                        CommandResult.Error("Failed to open ${action.appLabel}. Activity not found.")
                    } catch (e: SecurityException) {
                        CommandResult.Error("Permission denied to open ${action.appLabel}.")
                    } catch (e: Exception) {
                        CommandResult.Error("Failed to open ${action.appLabel}.")
                    }
                } else {
                    CommandResult.Error("App not installed or is disabled.")
                }
            }
            is Action.MultipleChoices -> {
                val list = action.choices.joinToString("\n- ", prefix = "\n- ")
                CommandResult.Error("I found multiple apps matching that:$list\nPlease specify which one.")
            }
            is Action.AppNotFound -> {
                CommandResult.Error("I couldn't find that app installed.")
            }
            is Action.SystemNavigate -> {
                if (action.target == "HOME") {
                    val homeIntent = AndroidIntent(AndroidIntent.ACTION_MAIN).apply {
                        addCategory(AndroidIntent.CATEGORY_HOME)
                        flags = AndroidIntent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(homeIntent)
                    CommandResult.Success("Going home...")
                } else if (action.target == "BACK") {
                    // Simulating global back requires AccessibilityService.
                    // For now, no-op or close the Assistant app.
                    CommandResult.Error("Global 'Go back' requires Accessibility permissions (coming soon).")
                } else {
                    CommandResult.Error("Unknown navigation target.")
                }
            }
            is Action.None -> {
                CommandResult.Error("I didn't understand that command.")
            }
        }
    }
}
