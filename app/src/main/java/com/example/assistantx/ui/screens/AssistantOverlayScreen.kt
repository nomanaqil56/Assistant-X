package com.example.assistantx.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assistantx.ui.components.AssistantCard
import com.example.assistantx.ui.components.NavItem
import com.example.assistantx.ui.components.NavigationRailPill
import com.example.assistantx.ui.theme.AssistantXTheme

/**
 * Assistant X Floating Overlay Screen.
 * Perfectly centered on the screen with a 100% transparent backdrop over your underlying apps/homescreen.
 */
@Composable
fun AssistantOverlayScreen(
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedNav by remember { mutableStateOf(NavItem.HOME) }
    val backdropInteraction = remember { MutableInteractionSource() }
    val contentInteraction = remember { MutableInteractionSource() }

    // Outer full screen box with tap outside to dismiss
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = backdropInteraction,
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        // Centered Floating glass layout: Navigation Rail + Assistant Card
        Row(
            modifier = Modifier
                .widthIn(max = 430.dp)
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                // Consume clicks on the card so tapping inside doesn't dismiss
                .clickable(
                    interactionSource = contentInteraction,
                    indication = null,
                    onClick = {}
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Vertical Navigation Pill
            NavigationRailPill(
                selectedItem = selectedNav,
                onItemSelected = { item ->
                    if (item == NavItem.POWER) {
                        onDismiss()
                    } else {
                        selectedNav = item
                    }
                }
            )

            // Main Floating Glassmorphic Assistant Card
            AssistantCard(
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = false, device = "spec:width=411dp,height=891dp")
@Composable
fun AssistantOverlayScreenPreview() {
    AssistantXTheme {
        AssistantOverlayScreen()
    }
}
