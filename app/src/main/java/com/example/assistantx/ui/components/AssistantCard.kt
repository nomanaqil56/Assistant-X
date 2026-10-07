package com.example.assistantx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.ui.theme.StatusGreen
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import com.example.assistantx.ui.theme.TextTertiary

/**
 * Main floating frosted glassmorphic Assistant X card.
 */
@Composable
fun AssistantCard(
    messages: List<com.example.assistantx.ui.models.Message> = emptyList(),
    modifier: Modifier = Modifier,
    showContainer: Boolean = true,
    onHistoryClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    onCommandSubmit: (String) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }

    val content = @Composable {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: 2 Unequal Lines Menu Icon (Left) & Borderless History Icon (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
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

                // Right: Borderless History Icon
                if (onHistoryClick != null) {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onHistoryClick
                            ),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "History",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }

            // Central Section: Conversation or Empty State
            if (messages.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Assistant X",
                        color = TextPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Your Private AI Assistant",
                        color = TextSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    AssistantOrb(
                        size = 175.dp
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Text(
                        text = "How can I help you today?",
                        color = TextPrimary,
                        fontSize = 19.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Think. Assist. On Your Device.",
                        color = TextSecondary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    reverseLayout = false,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(message = message)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Section: Input Bar
            AssistantInputBar(
                text = inputText,
                onTextChanged = { inputText = it },
                onSubmitClicked = { 
                    if (it.isNotBlank()) {
                        onCommandSubmit(it)
                        inputText = ""
                    }
                },
                onAddClicked = { /* Optional attachment hook */ },
                onMicClicked = { /* Voice input hook */ }
            )
        }
    }

    if (showContainer) {
        GlassmorphicContainer(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(26.dp),
            hasEdgeGlow = true
        ) {
            content()
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            content()
        }
    }
}
