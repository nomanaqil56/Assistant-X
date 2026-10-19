package com.example.assistantx.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.core.state.AssistantState
import com.example.assistantx.ui.models.Message
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary

/**
 * Main Assistant X Conversational Screen.
 * - Minimal top bar with menu & history triggers (centered orb removed when chatting).
 * - Focused empty state with prominent particle orb.
 * - Text-first conversation layout with automatic scrolling and keyboard handling.
 */
@Composable
fun AssistantCard(
    messages: List<Message> = emptyList(),
    assistantState: AssistantState = AssistantState.IDLE,
    modifier: Modifier = Modifier,
    showContainer: Boolean = false,
    historyCount: Int = 0,
    onHistoryClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    onCommandSubmit: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Smoothly scroll to the latest message whenever messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: 2 Unequal Horizontal Lines Menu Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onMenuClick?.invoke() }
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(TextPrimary)
                        )
                        Box(
                            modifier = Modifier
                                .width(13.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(TextPrimary)
                        )
                    }
                }

                // Right: Borderless History Icon (Current Chat Only)
                if (onHistoryClick != null) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onHistoryClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "Current Chat Command History",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        if (historyCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 2.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8))
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }

            // --- CENTRAL SECTION: EMPTY STATE OR ACTIVE CHAT ---
            if (messages.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Iconic Prominent Particle Orb
                    AssistantOrb(
                        size = 175.dp,
                        state = assistantState,
                        isCompact = false
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "Assistant X",
                        color = TextPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "How can I help you today?",
                        color = TextSecondary,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        MessageBubble(
                            message = message,
                            assistantState = assistantState
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // --- BOTTOM SECTION: INPUT BAR ---
            AssistantInputBar(
                text = inputText,
                onTextChanged = { inputText = it },
                onSubmitClicked = { query ->
                    if (query.isNotBlank()) {
                        onCommandSubmit(query)
                        inputText = ""
                    }
                },
                onMicClicked = {
                    Toast.makeText(
                        context,
                        "Voice input engine is currently in development.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}
