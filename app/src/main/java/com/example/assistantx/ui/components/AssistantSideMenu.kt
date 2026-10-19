package com.example.assistantx.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.assistantx.ui.theme.StatusGreen
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import com.example.assistantx.ui.theme.TextTertiary

// Cinematic entrance & dismissal easing curves
private val EntranceEasing = CubicBezierEasing(0.16f, 1.0f, 0.30f, 1.0f)
private val DismissEasing = FastOutSlowInEasing

/**
 * Premium Cinematic Assistant X Side Menu Control Surface.
 * Features a bespoke coordinated animation entrance, deep near-black surfaces,
 * restrained cyan accents, verified live subsystem inspection, and uncluttered primary navigation.
 */
@Composable
fun AssistantSideMenu(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNewChat: () -> Unit,
    onOpenHistory: () -> Unit,
    assistantState: AssistantState = AssistantState.IDLE,
    isTtsReady: Boolean = false,
    historyCount: Int = 0,
    hasActiveConversation: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // Target panel width: responsive 290-320 dp
    val screenWidth = configuration.screenWidthDp.dp
    val maxPanelWidth = 315.dp
    val proportionalWidth = screenWidth * 0.82f
    val panelWidthDp = if (proportionalWidth < maxPanelWidth) proportionalWidth else maxPanelWidth
    val panelWidthPx = with(density) { panelWidthDp.toPx() }

    // Master Coordinated Animation Controller (0f = closed, 1f = fully presented)
    val entranceProgress = remember { Animatable(if (isOpen) 1f else 0f) }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            entranceProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 380, easing = EntranceEasing)
            )
        } else {
            entranceProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 280, easing = DismissEasing)
            )
        }
    }

    // Intercept hardware / gesture Back to close menu smoothly first
    BackHandler(enabled = isOpen) {
        onDismiss()
    }

    // Modal and subview states
    var showNewChatConfirmDialog by remember { mutableStateOf(false) }
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }
    var infoDialogMessage by remember { mutableStateOf<String?>(null) }
    var isStatusExpanded by remember { mutableStateOf(false) }

    // Detach from composition completely when idle and closed to save CPU/GPU resources
    if (!isOpen && entranceProgress.value == 0f) {
        return
    }

    val progress = entranceProgress.value

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // --- 1. COORDINATED BACKDROP ---
        val backdropAlpha = (progress * 0.68f).coerceIn(0f, 0.68f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF030508).copy(alpha = backdropAlpha))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClickLabel = "Dismiss control surface",
                    onClick = onDismiss
                )
        )

        // --- 2. MAIN CINEMATIC CONTROL PANEL ---
        val panelShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)

        val panelFillBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xF90B1017),
                Color(0xFC080C13),
                Color(0xFF05080D)
            )
        )

        val edgeGlowAlpha = (progress.coerceIn(0.15f, 1f) - 0.15f) / 0.85f
        val panelBorderBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0x2EFFFFFF).copy(alpha = edgeGlowAlpha * 0.35f),
                Color(0x0EFFFFFF),
                Color(0x3838BDF8).copy(alpha = edgeGlowAlpha * 0.45f)
            )
        )

        val slideOffsetX = -((1f - progress) * panelWidthPx).toInt()
        val settleScale = 0.965f + (progress * 0.035f)

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(panelWidthDp)
                .offset { IntOffset(x = slideOffsetX, y = 0) }
                .graphicsLayer {
                    scaleX = settleScale
                    scaleY = settleScale
                    transformOrigin = TransformOrigin(0f, 0.5f)
                }
                .align(Alignment.CenterStart)
                .clip(panelShape)
                .background(panelFillBrush, panelShape)
                .border(1.dp, panelBorderBrush, panelShape)
                .drawWithContent {
                    drawContent()
                    // Refined hairline cyan glint along right edge
                    if (edgeGlowAlpha > 0.05f) {
                        val canvasWidth = this.size.width
                        val canvasHeight = this.size.height
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x2238BDF8).copy(alpha = edgeGlowAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(canvasWidth, canvasHeight * 0.32f),
                                radius = canvasWidth * 0.55f
                            ),
                            radius = canvasWidth * 0.55f,
                            center = Offset(canvasWidth, canvasHeight * 0.32f)
                        )
                    }
                }
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                // --- A. BESPOKE HEADER (Staggered Entrance Stage 0) ---
                val headerAlpha = (progress.coerceIn(0.1f, 0.85f) - 0.1f) / 0.75f
                val headerOffsetY = (1f - headerAlpha) * -12f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = headerAlpha
                            translationY = headerOffsetY
                        }
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        // Refined Precision Luminous Core Emblem
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x1A141E2C))
                                .border(1.dp, Color(0x4038BDF8), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Inner singularity glow
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                        )
                                    )
                            )
                        }

                        Column {
                            Text(
                                text = "Assistant X",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.4.sp
                            )
                            Text(
                                text = "AI CONTROL INTERFACE",
                                color = Color(0xFF38BDF8).copy(alpha = 0.70f),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Minimal Circular Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF))
                            .border(1.dp, Color(0x10FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close control center",
                            tint = TextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // --- B. COMPACT ASSISTANT READINESS STATUS (Staggered Stage 1) ---
                val statusStagger = calculateStagger(progress, index = 0, totalItems = 7)
                RealSystemStatusArea(
                    assistantState = assistantState,
                    isTtsReady = isTtsReady,
                    isExpanded = isStatusExpanded,
                    onToggleExpand = { isStatusExpanded = !isStatusExpanded },
                    modifier = Modifier.graphicsLayer {
                        alpha = statusStagger.first
                        translationY = statusStagger.second
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // --- C. PRIMARY NAVIGATION (Staggered Stages 2 & 3) ---
                val navHeaderStagger = calculateStagger(progress, index = 1, totalItems = 7)
                Text(
                    text = "WORKSPACE",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = navHeaderStagger.first
                            translationY = navHeaderStagger.second
                        }
                        .padding(start = 4.dp, bottom = 6.dp)
                )

                // 1. New Conversation
                val newChatStagger = calculateStagger(progress, index = 2, totalItems = 7)
                CinematicNavRow(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    label = "New Conversation",
                    onClick = {
                        if (hasActiveConversation) {
                            showNewChatConfirmDialog = true
                        } else {
                            onNewChat()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.graphicsLayer {
                        alpha = newChatStagger.first
                        translationY = newChatStagger.second
                    }
                )

                // 2. Command History
                val historyStagger = calculateStagger(progress, index = 3, totalItems = 7)
                CinematicNavRow(
                    icon = Icons.Outlined.History,
                    label = "Command History",
                    badge = if (historyCount > 0) "$historyCount" else null,
                    onClick = {
                        onDismiss()
                        onOpenHistory()
                    },
                    modifier = Modifier.graphicsLayer {
                        alpha = historyStagger.first
                        translationY = historyStagger.second
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(
                    color = Color(0x10FFFFFF),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // --- D. SECONDARY CONTROLS / SUBSYSTEMS (Staggered Stages 4 to 7) ---
                val configHeaderStagger = calculateStagger(progress, index = 4, totalItems = 7)
                Text(
                    text = "SUBSYSTEMS & ACCESS",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = configHeaderStagger.first
                            translationY = configHeaderStagger.second
                        }
                        .padding(start = 4.dp, bottom = 6.dp)
                )

                // 1. Voice & Speech (Native Android TTS Configuration)
                val voiceStagger = calculateStagger(progress, index = 5, totalItems = 7)
                CinematicNavRow(
                    icon = Icons.Outlined.RecordVoiceOver,
                    label = "Voice & Speech Engine",
                    trailingIcon = Icons.AutoMirrored.Outlined.OpenInNew,
                    onClick = { openTtsSettings(context) },
                    modifier = Modifier.graphicsLayer {
                        alpha = voiceStagger.first
                        translationY = voiceStagger.second
                    }
                )

                // 2. Automation & Permissions (Native App Details)
                val permStagger = calculateStagger(progress, index = 6, totalItems = 7)
                CinematicNavRow(
                    icon = Icons.Outlined.Security,
                    label = "Device Permissions",
                    trailingIcon = Icons.AutoMirrored.Outlined.OpenInNew,
                    onClick = { openAppSettings(context) },
                    modifier = Modifier.graphicsLayer {
                        alpha = permStagger.first
                        translationY = permStagger.second
                    }
                )

                // 3. AI Architecture Info (Verified Local Rule Pipeline)
                val aiStagger = calculateStagger(progress, index = 7, totalItems = 7)
                CinematicNavRow(
                    icon = Icons.Outlined.Memory,
                    label = "On-Device Engine Info",
                    statusTag = "Local",
                    onClick = {
                        infoDialogTitle = "On-Device AI Architecture"
                        infoDialogMessage = "Assistant X parses intent and dispatches tasks strictly in local device memory. App launcher resolutions and system navigations execute without cloud transmission or external dependencies."
                    },
                    modifier = Modifier.graphicsLayer {
                        alpha = aiStagger.first
                        translationY = aiStagger.second
                    }
                )

                // 4. Privacy Policy (Sandboxed Storage)
                CinematicNavRow(
                    icon = Icons.Outlined.Shield,
                    label = "Sandbox & Privacy",
                    statusTag = "Isolated",
                    onClick = {
                        infoDialogTitle = "Sandboxed Privacy Model"
                        infoDialogMessage = "Command logs and state caches are stored strictly inside the private application sandbox. No analytics trackers or remote telemetry services are included."
                    },
                    modifier = Modifier.graphicsLayer {
                        alpha = aiStagger.first
                        translationY = aiStagger.second
                    }
                )

                Spacer(modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(26.dp))

                // --- E. MINIMALIST FOOTER ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Assistant X // BUILD 1.0.0",
                        color = TextTertiary.copy(alpha = 0.60f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.6.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL} • SDK ${Build.VERSION.SDK_INT}",
                        color = TextTertiary.copy(alpha = 0.40f),
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }

    // --- DIALOGS (With matching dark glassmorphism) ---
    if (showNewChatConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showNewChatConfirmDialog = false },
            title = {
                Text(
                    text = "Start New Conversation?",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will clear your current conversation view. Your recorded commands remain in Command History.",
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
            containerColor = Color(0xF80E141E),
            shape = RoundedCornerShape(18.dp)
        )
    }

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
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = infoDialogMessage!!,
                    color = TextSecondary,
                    fontSize = 13.5.sp,
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
                    Text("Dismiss", color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color(0xF80E141E),
            shape = RoundedCornerShape(18.dp)
        )
    }
}

/**
 * Calculates deterministic staggered reveal alpha and translation for entrance sequence.
 */
private fun calculateStagger(progress: Float, index: Int, totalItems: Int): Pair<Float, Float> {
    val startThreshold = (index.toFloat() / (totalItems + 2)) * 0.38f
    val itemProgress = ((progress - startThreshold) / 0.45f).coerceIn(0f, 1f)
    val alpha = itemProgress
    val translationY = (1f - itemProgress) * 14f
    return alpha to translationY
}

/**
 * Compact, quiet Assistant Status Area.
 * Visually restrained when collapsed; smoothly reveals real, verified subsystem states when tapped.
 */
@Composable
private fun RealSystemStatusArea(
    assistantState: AssistantState,
    isTtsReady: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0x1F141E2C), shape)
            .border(1.dp, Color(0x1838BDF8), shape)
            .clickable(
                role = Role.Button,
                onClickLabel = if (isExpanded) "Collapse system status" else "Expand system status",
                onClick = onToggleExpand
            )
            .padding(horizontal = 12.dp, vertical = 9.dp)
            .animateContentSize(animationSpec = tween(240, easing = FastOutSlowInEasing))
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
                    // Pulsing micro status dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(StatusGreen)
                    )
                    Text(
                        text = "CORE STATUS: READY",
                        color = Color(0xFF38BDF8),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = assistantState.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse status details" else "Expand status details",
                        tint = TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expanded Subsystem Details
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                RealSubsystemRow(
                    label = "Command Engine",
                    status = "Active (${assistantState.name.lowercase().replaceFirstChar { it.uppercase() }})",
                    isPositive = true
                )
                RealSubsystemRow(
                    label = "Voice Output (TTS)",
                    status = if (isTtsReady) "Ready" else "Initializing",
                    isPositive = isTtsReady
                )
                RealSubsystemRow(
                    label = "Speech Input",
                    status = "Not Connected",
                    isPositive = false
                )
                RealSubsystemRow(
                    label = "Local AI Model",
                    status = "Pattern & Rule Parser",
                    isPositive = true
                )
                RealSubsystemRow(
                    label = "Accessibility Service",
                    status = "Not Configured",
                    isPositive = false
                )
            }
        }
    }
}

@Composable
private fun RealSubsystemRow(
    label: String,
    status: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = status,
            color = if (isPositive) Color(0xFF38BDF8) else TextTertiary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Cinematic Navigation Row with restrained pressed feedback, micro scale transition,
 * consistent icon sizing, and no unnecessary subtitles.
 */
@Composable
private fun CinematicNavRow(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    statusTag: String? = null,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1.0f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "RowScale"
    )

    val backgroundTint = if (isPressed) Color(0x1C38BDF8) else Color.Transparent
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(backgroundTint)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon container with soft cyan ambient tint
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x1438BDF8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // decorative
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(17.dp)
                )
            }

            // Clean, unencumbered Label
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Optional Count Badge
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x2838BDF8))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFF38BDF8),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Optional Status Tag
            if (statusTag != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x1438BDF8))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusTag,
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Optional Trailing Link Icon
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "Opens external settings",
                    tint = TextTertiary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Launches the Android native Text-to-Speech system configuration activity.
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
            // Fail gracefully if settings activity cannot be opened
        }
    }
}

/**
 * Launches the Android native Application Details Settings for Assistant X.
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
