package com.example.assistantx

import android.graphics.Color as AndroidColor
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.example.assistantx.ui.screens.HomeScreen
import com.example.assistantx.ui.theme.AssistantXTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var commandEngine: com.example.assistantx.engine.CommandEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // 1. Force the Window Format to purely Translucent regardless of the Theme or OS defaults
        window.setFormat(PixelFormat.TRANSLUCENT)

        // 2. Configure Edge-to-Edge manually to not break translucency
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 3. Force window background and decorView to be 100% transparent
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.decorView.setBackgroundColor(AndroidColor.TRANSPARENT)

        // 2. Disable system contrast enforcement so Android doesn't inject dark scrims
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }



        commandEngine = com.example.assistantx.engine.CommandEngine(this)

        setContent {
            androidx.activity.compose.BackHandler {
                moveTaskToBack(true)
            }
            AssistantXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    HomeScreen(
                        commandEngine = commandEngine,
                        onPowerClicked = { finish() }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure transparent backdrop is reapplied whenever the app is reopened
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.decorView.setBackgroundColor(AndroidColor.TRANSPARENT)
    }

    override fun onPause() {
        super.onPause()
        // Set a solid dark background for the Recents snapshot to avoid transparency glitches
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.parseColor("#0B1017")))
        window.decorView.setBackgroundColor(AndroidColor.parseColor("#0B1017"))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::commandEngine.isInitialized) {
            commandEngine.shutdown()
        }
    }
}
