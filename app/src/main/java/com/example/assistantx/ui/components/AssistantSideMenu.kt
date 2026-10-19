package com.example.assistantx.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.ui.theme.StatusGreen
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import com.example.assistantx.ui.theme.TextTertiary

/**
 * Re-designed Assistant X Side Menu.
 * Adheres strictly to the Assistant X dark glassmorphic identity,
 * featuring translucent surfaces, subtle cyan glow accents, system status,
 * and intuitive controls for sessions, local AI info, voice, and permissions.
 */
@Composable
fun AssistantSideMenu(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNewChat: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }
    var infoDialogMessage by remember { mutableStateOf<String?>(null) }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Semi-transparent backdrop scrim (tap to dismiss)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )

            // 2. Translucent Glass Panel
            val panelShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)

            val panelGlassBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xF00D1522),
                    Color(0xE60A101A),
                    Color(0xD9060B12)
                )
            )

            val panelBorderBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0x55FFFFFF),
                    Color(0x20FFFFFF),
                    Color(0x1038BDF8),
                    Color(0x4038BDF8)
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.80f)
                    .widthIn(max = 330.dp)
                    .align(Alignment.CenterStart)
                    .clip(panelShape)
                    .background(panelGlassBrush, panelShape)
                    .border(1.dp, panelBorderBrush, panelShape)
                    .drawWithContent {
                        drawContent()
                        // Subtle cyan glass flare along right rim
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x2A38BDF8), Color.Transparent),
                                center = Offset(size.width, size.height * 0.35f),
                                radius = size.width * 0.7f
                            ),
                            radius = size.width * 0.7f,
                            center = Offset(size.width, size.height * 0.35f)
                        )
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Consume click inside */ }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    // Top Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Mini Glowing Emblem
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFF38BDF8), Color(0xFF0F172A))
                                        )
                                    )
                                    .border(1.dp, Color(0x6638BDF8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }

                            Column {
                                Text(
                                    text = "Assistant X",
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                                Text(
                                    text = "Private On-Device AI",
                                    color = TextSecondary,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        // Close Button
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onDismiss
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Close Menu",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Local Status Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x331E293B))
                            .border(1.dp, Color(0x2638BDF8), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen)
                            )
                            Column {
                                Text(
                                    text = "LOCAL NEURAL ENGINE",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "100% Offline • Zero Cloud Telemetry",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Section: Navigation & Chat
                    MenuSectionHeader(title = "CHAT CONTROLS")
                    Spacer(modifier = Modifier.height(8.dp))

                    SideMenuItem(
                        icon = Icons.Outlined.Refresh,
                        title = "New Conversation",
                        subtitle = "Clear chat & start fresh",
                        onClick = {
                            onNewChat()
                            onDismiss()
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SideMenuItem(
                        icon = Icons.Outlined.History,
                        title = "History",
                        subtitle = "View past commands & sessions",
                        onClick = {
                            onDismiss()
                            onOpenHistory()
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Section: System & Settings
                    MenuSectionHeader(title = "SYSTEM & PRIVACY")
                    Spacer(modifier = Modifier.height(8.dp))

                    SideMenuItem(
                        icon = Icons.Outlined.Memory,
                        title = "Local AI Engine",
                        subtitle = "Offline intent parsing & execution",
                        statusText = "Active",
                        onClick = {
                            infoDialogTitle = "Local AI Engine"
                            infoDialogMessage = "Assistant X executes intent recognition, task planning, and app resolution purely on your device. No personal queries or text inputs leave your phone."
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SideMenuItem(
                        icon = Icons.Outlined.GraphicEq,
                        title = "Voice & Audio",
                        subtitle = "Text-to-Speech & Voice control",
                        statusText = "Ready",
                        onClick = {
                            infoDialogTitle = "Voice & Speech"
                            infoDialogMessage = "Assistant X utilizes on-device Text-to-Speech synthesis for natural voice feedback. Speak commands or receive spoken confirmations instantly."
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SideMenuItem(
                        icon = Icons.Outlined.Security,
                        title = "Permissions",
                        subtitle = "App launcher & system overlays",
                        statusText = "Configured",
                        onClick = {
                            infoDialogTitle = "Permissions Status"
                            infoDialogMessage = "Assistant X requires Query All Packages permission to open installed apps, and system window overlay permissions to float above active apps seamlessly."
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f, fill = false))
                    Spacer(modifier = Modifier.height(30.dp))

                    // Bottom App Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Assistant X v1.0.0",
                            color = TextTertiary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Think. Assist. On Your Device.",
                            color = TextTertiary.copy(alpha = 0.7f),
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }
    }

    // Informational Dialog
    if (infoDialogTitle != null && infoDialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                infoDialogTitle = null
                infoDialogMessage = null
            },
            title = {
                Text(
                    text = infoDialogTitle!!,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = infoDialogMessage!!,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        infoDialogTitle = null
                        infoDialogMessage = null
                    }
                ) {
                    Text(text = "OK", color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color(0xF20F1724),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun MenuSectionHeader(title: String) {
    Text(
        text = title,
        color = TextTertiary,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun SideMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    statusText: String? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0x221E293B), shape)
            .border(1.dp, Color(0x18FFFFFF), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x3338BDF8))
                    .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            if (statusText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x2638BDF8))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusText,
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
