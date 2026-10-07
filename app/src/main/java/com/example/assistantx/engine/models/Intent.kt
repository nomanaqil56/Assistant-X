package com.example.assistantx.engine.models

sealed class Intent {
    data class OpenApp(val appName: String) : Intent()
    object GoHome : Intent()
    object GoBack : Intent()
    object Unknown : Intent()
}
