package com.example.assistantx.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.ui.components.AssistantCard
import com.example.assistantx.ui.theme.AssistantXTheme
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import kotlinx.coroutines.launch

import com.example.assistantx.core.state.AssistantState
import com.example.assistantx.core.state.AssistantStateManager
import com.example.assistantx.engine.CommandEngine

/**
 * Assistant X Main Home Screen.
 * Features a slide-in Side Menu Drawer from the left and a slide-up History Bottom Sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    commandEngine: CommandEngine? = null,
    stateManager: AssistantStateManager? = null,
    onPowerClicked: () -> Unit = {}
) {
    var showHistorySheet by remember { mutableStateOf(false) }
    var showMenuDrawer by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    // State Observation
    val assistantState by (stateManager?.currentState ?: kotlinx.coroutines.flow.MutableStateFlow(AssistantState.IDLE)).androidx.compose.runtime.collectAsState()

    // Conversational State
    val messages = remember { androidx.compose.runtime.mutableStateListOf<com.example.assistantx.ui.models.Message>() }

    // Automatically hide keyboard and clear focus whenever menu or history opens
    LaunchedEffect(showMenuDrawer) {
        if (showMenuDrawer) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(showHistorySheet) {
        if (showHistorySheet) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val darkBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0B1017),
            Color(0xFF070A0F),
            Color(0xFF040609)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Borderless full-screen Assistant Content
        AssistantCard(
            messages = messages,
            assistantState = assistantState,
            modifier = Modifier.fillMaxSize(),
            showContainer = false,
            onHistoryClick = { showHistorySheet = true },
            onMenuClick = { showMenuDrawer = true },
            onCommandSubmit = { text ->
                if (commandEngine == null) return@AssistantCard

                // 1. Add User Message
                messages.add(com.example.assistantx.ui.models.Message(
                    role = com.example.assistantx.ui.models.MessageRole.USER,
                    text = text
                ))

                // 2. Add Assistant Pending Message
                val pendingId = java.util.UUID.randomUUID().toString()
                messages.add(com.example.assistantx.ui.models.Message(
                    id = pendingId,
                    role = com.example.assistantx.ui.models.MessageRole.ASSISTANT,
                    text = "Processing...",
                    status = com.example.assistantx.ui.models.MessageStatus.PENDING
                ))

                // 3. Execute Command Async
                coroutineScope.launch {
                    val result = commandEngine.processCommand(text)
                    
                    // 4. Update Pending Message with Result
                    val index = messages.indexOfFirst { it.id == pendingId }
                    if (index != -1) {
                        when (result) {
                            is com.example.assistantx.engine.models.CommandResult.Success -> {
                                messages[index] = messages[index].copy(
                                    text = result.message,
                                    status = com.example.assistantx.ui.models.MessageStatus.SUCCESS
                                )
                            }
                            is com.example.assistantx.engine.models.CommandResult.Error -> {
                                messages[index] = messages[index].copy(
                                    text = result.message,
                                    status = com.example.assistantx.ui.models.MessageStatus.ERROR
                                )
                            }
                        }
                    }
                }
            }
        )

        // Slide-in Side Menu Drawer from Left
        AnimatedVisibility(
            visible = showMenuDrawer,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Dimmed Scrim Backdrop (tap outside to close)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { showMenuDrawer = false }
                        )
                )

                // Frosted Glass Side Drawer (80% width with glassmorphic edge glint and rounded right corners)
                val drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)

                val drawerGlassBrush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xD9121C2B),
                        Color(0xC80F1724),
                        Color(0xB30A0F18)
                    )
                )

                val drawerBorderBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x66FFFFFF),
                        Color(0x22FFFFFF),
                        Color(0x10FFFFFF),
                        Color(0x4438BDF8)
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.80f)
                        .align(Alignment.CenterStart)
                        .clip(drawerShape)
                        .background(drawerGlassBrush, drawerShape)
                        .border(1.dp, drawerBorderBrush, drawerShape)
                        .drawWithContent {
                            drawContent()
                            // Subtle cyan glass flare along the right edge
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x3338BDF8), Color.Transparent),
                                    center = Offset(size.width, size.height * 0.35f),
                                    radius = size.width * 0.6f
                                ),
                                radius = size.width * 0.6f,
                                center = Offset(size.width, size.height * 0.35f)
                            )
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {} // Consume click inside
                        )
                ) {
                    // Blank drawer section for adding items step-by-step
                }
            }
        }

        // Slide-up History Bottom Sheet
        if (showHistorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showHistorySheet = false },
                sheetState = sheetState,
                containerColor = Color(0xF00D1420),
                contentColor = Color.White,
                scrimColor = Color.Black.copy(alpha = 0.5f),
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        color = Color(0x66FFFFFF)
                    )
                }
            ) {
                HistorySheetContent(
                    onItemClick = {
                        showHistorySheet = false
                    }
                )
            }
        }
    }
}

@Composable
private fun HistorySheetContent(
    onItemClick: (String) -> Unit
) {
    val sampleHistory = remember {
        listOf(
            "Explain Quantum Computing in Simple Terms" to "Today, 2:15 PM",
            "Compose App Architecture Best Practices" to "Yesterday, 6:40 PM",
            "Summarize Key Meeting Points" to "3 days ago",
            "Debug Android Translucent Window Issue" to "May 12",
            "Create Glassmorphic Theme in Jetpack Compose" to "May 10",
            "Generate Marketing Copy for Assistant X" to "May 08"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Clear All",
                color = Color(0xFF60A5FA),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* Clear history action */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(sampleHistory) { (title, date) ->
                HistoryRowItem(
                    title = title,
                    date = date,
                    onClick = { onItemClick(title) }
                )
            }
        }
    }
}

@Composable
private fun HistoryRowItem(
    title: String,
    date: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val itemShape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .background(Color(0x331E293B), itemShape)
            .border(1.dp, Color(0x22FFFFFF), itemShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date,
                    color = TextSecondary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    AssistantXTheme {
        HomeScreen()
    }
}
