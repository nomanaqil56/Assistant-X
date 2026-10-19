package com.example.assistantx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.core.history.CommandHistoryEntry
import com.example.assistantx.core.history.CommandHistoryRepository
import com.example.assistantx.core.state.AssistantState
import com.example.assistantx.core.state.AssistantStateManager
import com.example.assistantx.engine.CommandEngine
import com.example.assistantx.engine.models.CommandResult
import com.example.assistantx.ui.components.AssistantCard
import com.example.assistantx.ui.components.AssistantSideMenu
import com.example.assistantx.ui.models.Message
import com.example.assistantx.ui.models.MessageRole
import com.example.assistantx.ui.models.MessageStatus
import com.example.assistantx.ui.theme.AssistantXTheme
import com.example.assistantx.ui.theme.DarkBackground
import com.example.assistantx.ui.theme.StatusGreen
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import com.example.assistantx.ui.theme.TextTertiary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Assistant X Main Home Screen.
 * Unifies the entire application around the approved minimal, borderless, cinematic theme.
 * Integrates text-first conversational UI, side menu, dedicated Settings screen,
 * and redesigned persistent Command History bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    commandEngine: CommandEngine? = null,
    stateManager: AssistantStateManager? = null
) {
    val context = LocalContext.current
    val historyRepository = remember { CommandHistoryRepository(context) }
    var historyEntries by remember { mutableStateOf(historyRepository.getHistory()) }

    var showHistorySheet by remember { mutableStateOf(false) }
    var showMenuDrawer by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // State Observation
    val fallbackFlow = remember { MutableStateFlow(AssistantState.IDLE) }
    val currentFlow = stateManager?.currentState ?: fallbackFlow
    val assistantState by currentFlow.collectAsState()

    // Conversational State
    val messages = remember { mutableStateListOf<Message>() }

    // Automatically hide keyboard and clear focus whenever menu, history, or settings opens
    LaunchedEffect(showMenuDrawer, showHistorySheet, showSettingsScreen) {
        if (showMenuDrawer || showHistorySheet || showSettingsScreen) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val executeCommand: (String) -> Unit = { text ->
            if (commandEngine != null && text.isNotBlank()) {
                // 1. Add User Message
                messages.add(
                    Message(
                        role = MessageRole.USER,
                        text = text
                    )
                )

                // 2. Add Assistant Pending Message
                val pendingId = UUID.randomUUID().toString()
                messages.add(
                    Message(
                        id = pendingId,
                        role = MessageRole.ASSISTANT,
                        text = "Processing...",
                        status = MessageStatus.PENDING
                    )
                )

                // 3. Execute Command Async
                coroutineScope.launch {
                    val result = commandEngine.processCommand(text)

                    // 4. Record in persistent local command history
                    historyRepository.addEntry(
                        command = text,
                        resultMessage = result.message,
                        isSuccess = result is CommandResult.Success
                    )
                    historyEntries = historyRepository.getHistory()

                    // 5. Update Pending Message with Result
                    val index = messages.indexOfFirst { it.id == pendingId }
                    if (index != -1) {
                        when (result) {
                            is CommandResult.Success -> {
                                messages[index] = messages[index].copy(
                                    text = result.message,
                                    status = MessageStatus.SUCCESS
                                )
                            }
                            is CommandResult.Error -> {
                                messages[index] = messages[index].copy(
                                    text = result.message,
                                    status = MessageStatus.ERROR
                                )
                            }
                        }
                    }
                }
            }
        }

        // Primary Conversational Experience
        AssistantCard(
            messages = messages,
            assistantState = assistantState,
            modifier = Modifier.fillMaxSize(),
            showContainer = false,
            onHistoryClick = { showHistorySheet = true },
            onMenuClick = { showMenuDrawer = true },
            onCommandSubmit = executeCommand
        )

        // Minimalist Borderless Side Menu Control Center
        AssistantSideMenu(
            isOpen = showMenuDrawer,
            onDismiss = { showMenuDrawer = false },
            onNewChat = {
                messages.clear()
            },
            onOpenHistory = {
                showHistorySheet = true
            },
            onOpenSettings = {
                showSettingsScreen = true
            },
            assistantState = assistantState,
            isTtsReady = commandEngine?.isTtsReady ?: false,
            historyCount = historyEntries.size,
            hasActiveConversation = messages.isNotEmpty()
        )

        // Dedicated In-App Settings Destination
        AnimatedVisibility(
            visible = showSettingsScreen,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            SettingsScreen(
                onBack = { showSettingsScreen = false },
                commandEngine = commandEngine,
                historyCount = historyEntries.size,
                onClearHistory = {
                    historyRepository.clearHistory()
                    historyEntries = emptyList()
                }
            )
        }

        // Redesigned Borderless Command History Bottom Sheet
        if (showHistorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showHistorySheet = false },
                sheetState = sheetState,
                containerColor = DarkBackground,
                contentColor = TextPrimary,
                scrimColor = Color.Black.copy(alpha = 0.65f),
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        color = Color(0x33FFFFFF)
                    )
                }
            ) {
                HistorySheetContent(
                    entries = historyEntries,
                    onClearHistory = {
                        historyRepository.clearHistory()
                        historyEntries = emptyList()
                    },
                    onItemClick = { command ->
                        showHistorySheet = false
                        executeCommand(command)
                    }
                )
            }
        }
    }
}

/**
 * Refined Command History Sheet Content.
 * Borderless, minimal, dark aesthetic matching the side menu.
 */
@Composable
private fun HistorySheetContent(
    entries: List<CommandHistoryEntry>,
    onClearHistory: () -> Unit,
    onItemClick: (String) -> Unit
) {
    var showClearConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Command History",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (entries.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x1838BDF8))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${entries.size}",
                            color = Color(0xFF38BDF8),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (entries.isNotEmpty()) {
                Text(
                    text = "Clear All",
                    color = Color(0xFFEF4444),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { showClearConfirmation = true }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (entries.isEmpty()) {
            // Minimal Empty State
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x0EFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "No Recorded Commands",
                    color = TextPrimary,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Commands you execute on Assistant X are logged locally on this device.",
                    color = TextTertiary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(entries, key = { it.id }) { entry ->
                    HistoryRowItem(
                        entry = entry,
                        onClick = { onItemClick(entry.command) }
                    )
                }
            }
        }
    }

    // Clear All Confirmation Dialog
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = {
                Text(
                    text = "Clear Command History?",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete all recorded command logs? This action cannot be undone.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirmation = false
                        onClearHistory()
                    }
                ) {
                    Text(
                        text = "Delete All",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmation = false }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF10151E),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Clean, borderless history row item.
 */
@Composable
private fun HistoryRowItem(
    entry: CommandHistoryEntry,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val itemShape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .background(Color(0x0CFFFFFF), itemShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 11.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Minimalist Status Dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (entry.isSuccess) StatusGreen else Color(0xFFEF4444))
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.command,
                    color = TextPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (entry.resultMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = entry.resultMessage,
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Real timestamp relative format
            Text(
                text = formatTimestamp(entry.timestamp),
                color = TextTertiary,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun formatTimestamp(timestampMs: Long): String {
    val now = System.currentTimeMillis()
    val diffSec = (now - timestampMs) / 1000
    return when {
        diffSec < 60 -> "Just now"
        diffSec < 3600 -> "${diffSec / 60}m ago"
        diffSec < 86400 -> "${diffSec / 3600}h ago"
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sdf.format(Date(timestampMs))
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
