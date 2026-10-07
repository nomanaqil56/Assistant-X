package com.example.assistantx.engine

import com.example.assistantx.engine.models.Intent

class CommandParser {
    fun parse(text: String): Intent {
        val lowerText = text.trim().lowercase()

        if (lowerText.startsWith("open ") || lowerText.startsWith("launch ")) {
            val appName = lowerText.removePrefix("open ").removePrefix("launch ").trim()
            if (appName.isNotEmpty()) {
                return Intent.OpenApp(appName)
            }
        }

        if (lowerText == "go home" || lowerText == "go to home") {
            return Intent.GoHome
        }

        if (lowerText == "go back") {
            return Intent.GoBack
        }

        return Intent.Unknown
    }
}
