package com.example.assistantx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assistantx.ui.components.AssistantCard
import com.example.assistantx.ui.components.BottomDockRow
import com.example.assistantx.ui.components.NavItem
import com.example.assistantx.ui.components.NavigationRailPill
import com.example.assistantx.ui.components.TopAppRow
import com.example.assistantx.ui.theme.AssistantXTheme
import com.example.assistantx.ui.theme.BlackBackground

/**
 * Main Assistant X Home Screen reproducing the exact UI layout from the visual reference.
 */
@Composable
fun AssistantHomeScreen(
    modifier: Modifier = Modifier
) {
    var selectedNav by remember { mutableStateOf(NavItem.HOME) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBackground)
            .drawBehind {
                // Subtle ambient atmospheric background nebula glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1838BDF8), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 0.45f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.45f),
                    radius = size.width * 0.8f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1060A5FA), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.35f),
                        radius = size.width * 0.5f
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.35f),
                    radius = size.width * 0.5f
                )
            }
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section: App row (WhatsApp, Snapchat, Instagram)
            Spacer(modifier = Modifier.height(6.dp))
            TopAppRow(modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(14.dp))

            // Center section: Left Navigation Rail + Floating Assistant X Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Vertical Navigation Pill
                NavigationRailPill(
                    selectedItem = selectedNav,
                    onItemSelected = { selectedNav = it }
                )

                // Main Floating Assistant Card
                AssistantCard(
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bottom section: Dock Launcher Icons
            BottomDockRow(
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun AssistantHomeScreenPreview() {
    AssistantXTheme {
        AssistantHomeScreen()
    }
}
