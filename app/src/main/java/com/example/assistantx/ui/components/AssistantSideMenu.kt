package com.example.assistantx.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.core.state.AssistantState
import com.example.assistantx.ui.theme.DarkBackground
import com.example.assistantx.ui.theme.StatusGreen
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import com.example.assistantx.ui.theme.TextTertiary

// Cinematic slide duration & easing
private const val SLIDE_IN_DURATION = 280
private const val SLIDE_OUT_DURATION = 220
private val SlideEasing = FastOutSlowInEasing

/**
 * Minimal, Borderless, Cinematic Assistant X Side Menu.
 * Pure horizontal slide motion, deep monochrome surface, truthful subsystem status,
 * uncluttered navigation, and seamless system integration.
 */
@Composable
fun AssistantSideMenu(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNewChat: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit = {},
    assistantState: AssistantState = AssistantState.IDLE,
    isTtsReady: Boolean = false,
    historyCount: Int = 0,
    hasActiveConversation: Boolean = false,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // Responsive panel width: target 300dp, capped at 80% screen width
    val screenWidth = configuration.screenWidthDp.dp
    val maxPanelWidth = 300.dp
    val proportionalWidth = screenWidth * 0.80f
    val panelWidthDp = if (proportionalWidth < maxPanelWidth) proportionalWidth else maxPanelWidth
    val panelWidthPx = with(density) { panelWidthDp.toPx() }

    // Pure horizontal slide animation controller (0f = closed, 1f = fully presented)
    val slideProgress = remember { Animatable(if (isOpen) 1f else 0f) }
    val scrollState = rememberScrollState()

    LaunchedEffect(isOpen) {
        if (isOpen) {
            scrollState.scrollTo(0)
            slideProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SLIDE_IN_DURATION, easing = SlideEasing)
            )
        } else {
            slideProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = SLIDE_OUT_DURATION, easing = SlideEasing)
            )
        }
    }

    // Intercept hardware / gesture back to dismiss menu smoothly first
    BackHandler(enabled = isOpen) {
        onDismiss()
    }

    // Modal dialog states
    var showNewChatConfirmDialog by remember { mutableStateOf(false) }
    var isStatusExpanded by remember { mutableStateOf(false) }

    // Complete resource detachment when closed and idle
    if (!isOpen && slideProgress.value == 0f) {
        return
    }

    val progress = slideProgress.value

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // --- 1. SMOOTH MONOCHROME BACKDROP ---
        val backdropAlpha = (progress * 0.65f).coerceIn(0f, 0.65f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = backdropAlpha))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClickLabel = "Dismiss menu",
                    enabled = isOpen,
                    onClick = onDismiss
                )
        )

        // --- 2. BORDERLESS MONOCHROME PANEL ---
        val panelShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
        val slideOffsetX = -((1f - progress) * panelWidthPx).toInt()

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(panelWidthDp)
                .offset { IntOffset(x = slideOffsetX, y = 0) }
                .align(Alignment.CenterStart)
                .clip(panelShape)
                .background(DarkBackground, panelShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* Consume clicks inside panel */ }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // --- A. REFINED HEADER WITH ACTUAL COMPACT ORB ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Iconic Particle-Based Orb in Compact Mode
                        AssistantOrb(
                            size = 32.dp,
                            state = assistantState,
                            isCompact = true
                        )

                        Column {
                            Text(
                                text = "Assistant X",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp
                            )
                            Text(
                                text = "PERSONAL ASSISTANT",
                                color = Color(0xFF38BDF8).copy(alpha = 0.70f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Minimal Circular Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0x0EFFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close menu",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // --- B. TRUTHFUL ASSISTANT STATUS ---
                TruthfulStatusArea(
                    assistantState = assistantState,
                    isTtsReady = isTtsReady,
                    isExpanded = isStatusExpanded,
                    onToggleExpand = { isStatusExpanded = !isStatusExpanded }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // --- C. PRIMARY NAVIGATION ---
                Text(
                    text = "MENU",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                // 1. New Conversation
                MinimalNavRow(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    label = "New Conversation",
                    enabled = isOpen,
                    onClick = {
                        if (hasActiveConversation) {
                            showNewChatConfirmDialog = true
                        } else {
                            onNewChat()
                            onDismiss()
                        }
                    }
                )

                // 2. Command History
                MinimalNavRow(
                    icon = Icons.Outlined.History,
                    label = "Command History",
                    badge = if (historyCount > 0) "$historyCount" else null,
                    enabled = isOpen,
                    onClick = {
                        onDismiss()
                        onOpenHistory()
                    }
                )

                // 3. Settings Destination
                MinimalNavRow(
                    icon = Icons.Outlined.Settings,
                    label = "Settings",
                    enabled = isOpen,
                    onClick = {
                        onDismiss()
                        onOpenSettings()
                    }
                )

                Spacer(modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(28.dp))

                // --- D. SUBTLE MINIMAL FOOTER ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Assistant X // v1.0.0",
                        color = TextTertiary.copy(alpha = 0.55f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL} • SDK ${Build.VERSION.SDK_INT}",
                        color = TextTertiary.copy(alpha = 0.35f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }

    // --- DIALOGS (Matching Dark Theme) ---
    if (showNewChatConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showNewChatConfirmDialog = false },
            title = {
                Text(
                    text = "Start New Conversation?",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "This clears the active conversation view. Saved commands remain stored in Command History.",
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNewChatConfirmDialog = false
                        onNewChat()
                        onDismiss()
                    }
                ) {
                    Text("Clear Chat", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNewChatConfirmDialog = false }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xF00D1420),
            shape = RoundedCornerShape(14.dp)
        )
    }
}

/**
 * Truthful Assistant Status Area.
 * Reflects genuine AssistantState values with matching status indicator colors,
 * avoiding permanent "green" states and displaying verified subsystem info when expanded.
 */
@Composable
private fun TruthfulStatusArea(
    assistantState: AssistantState,
    isTtsReady: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stateColor = when (assistantState) {
        AssistantState.ERROR, AssistantState.UNAVAILABLE -> Color(0xFFF87171)
        AssistantState.PERMISSION_REQUIRED,
        AssistantState.WAITING_FOR_CONFIRMATION -> Color(0xFFFBBF24)
        AssistantState.LISTENING,
        AssistantState.PROCESSING,
        AssistantState.PLANNING,
        AssistantState.EXECUTING,
        AssistantState.OBSERVING,
        AssistantState.VERIFYING,
        AssistantState.SPEAKING -> Color(0xFF38BDF8)
        AssistantState.IDLE, AssistantState.COMPLETED -> StatusGreen
    }

    val stateLabel = when (assistantState) {
        AssistantState.IDLE -> "Ready"
        AssistantState.LISTENING -> "Listening"
        AssistantState.PROCESSING -> "Processing"
        AssistantState.PLANNING -> "Planning"
        AssistantState.EXECUTING -> "Working"
        AssistantState.OBSERVING -> "Observing"
        AssistantState.VERIFYING -> "Verifying"
        AssistantState.SPEAKING -> "Speaking"
        AssistantState.COMPLETED -> "Completed"
        AssistantState.ERROR -> "Needs attention"
        AssistantState.WAITING_FOR_CONFIRMATION -> "Awaiting confirmation"
        AssistantState.PERMISSION_REQUIRED -> "Permission needed"
        AssistantState.UNAVAILABLE -> "Unavailable"
    }

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0x0AFFFFFF), shape)
            .clickable(
                role = Role.Button,
                onClickLabel = if (isExpanded) "Collapse system status" else "Expand system status",
                onClick = onToggleExpand
            )
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .animateContentSize(animationSpec = tween(200, easing = FastOutSlowInEasing))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(stateColor)
                    )
                    Text(
                        text = "STATUS",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.9.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stateLabel,
                        color = stateColor,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse details" else "Expand details",
                        tint = TextTertiary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Expanded Subsystem Breakdown
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                SubsystemInfoRow(
                    label = "Command Engine",
                    status = when (assistantState) {
                        AssistantState.IDLE -> "Ready"
                        AssistantState.LISTENING -> "Listening"
                        AssistantState.PROCESSING -> "Processing"
                        AssistantState.PLANNING -> "Planning"
                        AssistantState.EXECUTING -> "Working"
                        AssistantState.OBSERVING -> "Observing"
                        AssistantState.VERIFYING -> "Verifying"
                        AssistantState.SPEAKING -> "Speaking"
                        AssistantState.COMPLETED -> "Completed"
                        AssistantState.ERROR -> "Error"
                        AssistantState.WAITING_FOR_CONFIRMATION -> "Awaiting confirmation"
                        AssistantState.PERMISSION_REQUIRED -> "Permission needed"
                        AssistantState.UNAVAILABLE -> "Unavailable"
                    },
                    isPositive = assistantState != AssistantState.ERROR &&
                        assistantState != AssistantState.UNAVAILABLE &&
                        assistantState != AssistantState.PERMISSION_REQUIRED
                )
                SubsystemInfoRow(
                    label = "Voice Output",
                    status = if (isTtsReady) "Ready" else "Not ready",
                    isPositive = isTtsReady
                )
                SubsystemInfoRow(
                    label = "Speech Input",
                    status = "Not Connected",
                    isPositive = false
                )
                SubsystemInfoRow(
                    label = "Local Engine",
                    status = "Rule Parser",
                    isPositive = true
                )
                SubsystemInfoRow(
                    label = "Accessibility",
                    status = "Not Configured",
                    isPositive = false
                )
            }
        }
    }
}

@Composable
private fun SubsystemInfoRow(
    label: String,
    status: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = status,
            color = if (isPositive) Color(0xFF38BDF8) else TextTertiary,
            fontSize = 11.sp,
            maxLines = 1,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Minimalist, Borderless Navigation Row.
 * Free of boxed icon containers, excessive pills, or heavy dividers.
 */
@Composable
private fun MinimalNavRow(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundTint = if (isPressed) Color(0x0EFFFFFF) else Color.Transparent
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(shape)
            .background(backgroundTint)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Unboxed, sleek icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isPressed) Color(0xFF38BDF8) else TextSecondary,
                modifier = Modifier.size(19.dp)
            )

            // Direct Label
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            // Functional Count Badge
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x1838BDF8))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFF38BDF8),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Trailing External Link Icon
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "Opens external settings",
                    tint = TextTertiary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

/**
 * Launches Android native Text-to-Speech configuration.
 */
private fun openTtsSettings(context: Context) {
    try {
        val intent = Intent("com.android.settings.TTS_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}

/**
 * Launches Android native Application Details Settings for Assistant X.
 */
private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallback = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(fallback)
    }
}
