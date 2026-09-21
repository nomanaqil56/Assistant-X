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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.ui.theme.TextPlaceholder
import com.example.assistantx.ui.theme.TextPrimary

/**
 * Recreates the pill-shaped input bar with '+' button, text field, and mic button.
 */
@Composable
fun AssistantInputBar(
    text: String = "",
    onTextChanged: (String) -> Unit = {},
    onAddClicked: () -> Unit = {},
    onMicClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(32.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val barBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xEE111722),
            Color(0xF00A0F16)
        )
    )

    val barBorder = Brush.verticalGradient(
        colors = listOf(
            Color(0x44FFFFFF),
            Color(0x15FFFFFF),
            Color(0x1038BDF8)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(pillShape)
            .background(barBackground, pillShape)
            .border(1.dp, barBorder, pillShape)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left '+' Add Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x44263345), CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onAddClicked
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Attach / Add",
                    tint = Color(0xFFE2E8F0),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

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
                        fontSize = 14.5.sp
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
                    cursorBrush = SolidColor(Color(0xFF60A5FA)),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Mic Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x44263345), CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onMicClicked
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = Color(0xFFE2E8F0),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
