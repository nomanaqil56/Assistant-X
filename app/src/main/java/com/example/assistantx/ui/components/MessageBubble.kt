package com.example.assistantx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.core.state.AssistantState
import com.example.assistantx.ui.models.Message
import com.example.assistantx.ui.models.MessageRole
import com.example.assistantx.ui.models.MessageStatus
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import com.example.assistantx.ui.theme.TextTertiary

/**
 * Modern, text-first Conversational Message Bubble for Assistant X.
 * - User messages: Right-aligned, subtle dark surface, restrained padding, no heavy outline.
 * - Assistant responses: Left-aligned, iconic compact AssistantOrb avatar on left,
 *   text sitting directly on canvas with comfortable line spacing.
 */
@Composable
fun MessageBubble(
    message: Message,
    assistantState: AssistantState = AssistantState.IDLE,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER

    if (isUser) {
        // --- USER MESSAGE ---
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            val userShape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
            Box(
                modifier = Modifier
                    .clip(userShape)
                    .background(Color(0x18FFFFFF), userShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .widthIn(max = 290.dp)
            ) {
                Text(
                    text = message.text,
                    color = TextPrimary,
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp,
                    letterSpacing = 0.15.sp
                )
            }
        }
    } else {
        // --- ASSISTANT RESPONSE ---
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            // Compact Assistant X Avatar
            Box(
                modifier = Modifier.padding(top = 1.dp)
            ) {
                AssistantOrb(
                    size = 30.dp,
                    state = if (message.status == MessageStatus.PENDING) assistantState else AssistantState.IDLE,
                    isCompact = true
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text-First Content directly on background
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp)
            ) {
                when (message.status) {
                    MessageStatus.PENDING -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF38BDF8),
                                strokeWidth = 1.5.dp,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Processing...",
                                color = TextTertiary,
                                fontSize = 13.5.sp,
                                letterSpacing = 0.2.sp
                            )
                        }
                    }
                    MessageStatus.ERROR -> {
                        Text(
                            text = message.text,
                            color = Color(0xFFF87171),
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp,
                            letterSpacing = 0.15.sp
                        )
                    }
                    else -> {
                        Text(
                            text = message.text,
                            color = TextPrimary,
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp,
                            letterSpacing = 0.15.sp
                        )
                    }
                }
            }
        }
    }
}
