package com.example.assistantx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.ui.theme.TextPlaceholder
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary

/**
 * Minimalist, dark, borderless input bar for Assistant X.
 * - Restrained dark surface matching side menu theme.
 * - Clean text field with placeholder.
 * - Dynamic right action button: Send (Cyan) when text is typed, Microphone (Subtle) when empty.
 */
@Composable
fun AssistantInputBar(
    text: String = "",
    onTextChanged: (String) -> Unit = {},
    onSubmitClicked: (String) -> Unit = {},
    onAddClicked: (() -> Unit)? = null,
    onMicClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(26.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(pillShape)
            .background(Color(0xFF10151E), pillShape)
            .border(0.5.dp, Color(0x14FFFFFF), pillShape)
            .padding(start = 18.dp, end = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Center Input Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = "Message Assistant X...",
                        color = TextPlaceholder,
                        fontSize = 14.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = TextPrimary,
                        fontSize = 14.5.sp
                    ),
                    cursorBrush = SolidColor(Color(0xFF38BDF8)),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Send
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onSend = {
                            if (text.isNotBlank()) onSubmitClicked(text)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Action Button (Send if text exists, else Mic)
            val hasText = text.isNotBlank()
            val buttonBg = if (hasText) Color(0xFF38BDF8) else Color(0x12FFFFFF)
            val iconTint = if (hasText) Color.Black else TextSecondary

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(buttonBg, CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            if (hasText) {
                                onSubmitClicked(text)
                            } else {
                                onMicClicked()
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (hasText) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Command",
                        tint = iconTint,
                        modifier = Modifier.size(17.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Mic,
                        contentDescription = "Voice Input",
                        tint = iconTint,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
