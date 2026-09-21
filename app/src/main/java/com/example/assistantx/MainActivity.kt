package com.example.assistantx

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.assistantx.ui.screens.AssistantOverlayScreen
import com.example.assistantx.ui.theme.AssistantXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure the Activity window is fully transparent
        window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        enableEdgeToEdge()

        setContent {
            AssistantXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    AssistantOverlayScreen(
                        onDismiss = { finish() }
                    )
                }
            }
        }
    }
}
