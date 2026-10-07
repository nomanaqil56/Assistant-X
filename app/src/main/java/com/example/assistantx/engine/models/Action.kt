package com.example.assistantx.engine.models

sealed class Action {
    data class LaunchApp(val packageName: String, val appLabel: String) : Action()
    data class SystemNavigate(val target: String) : Action()
    data class MultipleChoices(val choices: List<String>) : Action()
    data class AppNotFound(val query: String) : Action()
    object None : Action()
}
