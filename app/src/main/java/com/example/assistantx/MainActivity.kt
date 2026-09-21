package com.example.assistantx

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.assistantx.ui.screens.AssistantOverlayScreen
import com.example.assistantx.ui.theme.AssistantXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Expand dialog window to fill screen for responsive touch handling
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        // 2. Force window background and decorView to be 100% transparent
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.decorView.setBackgroundColor(AndroidColor.TRANSPARENT)

        // 3. Set soft dim behind the overlay card
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setDimAmount(0.45f)

        // 4. Disable system contrast enforcement so Android doesn't add black scrims
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AssistantXTheme {
                AssistantOverlayScreen(
                    onDismiss = { finish() }
                )
            }
        }
    }
}
