package com.example.assistantx.engine

import android.content.Context
import com.example.assistantx.engine.models.Action
import com.example.assistantx.engine.models.Intent

class ActionValidator(private val context: Context) {
    
    private val appResolver = AppResolver(context)

    fun validate(intent: Intent): Action {
        return when (intent) {
            is Intent.OpenApp -> {
                val matches = appResolver.resolve(intent.appName)
                if (matches.isEmpty()) {
                    Action.AppNotFound(intent.appName)
                } else if (matches.size == 1) {
                    Action.LaunchApp(matches[0].packageName, matches[0].label)
                } else {
                    Action.MultipleChoices(matches.map { it.label })
                }
            }
            is Intent.GoHome -> Action.SystemNavigate("HOME")
            is Intent.GoBack -> Action.SystemNavigate("BACK")
            is Intent.Unknown -> Action.None
        }
    }
}
