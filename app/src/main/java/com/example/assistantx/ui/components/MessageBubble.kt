package com.example.assistantx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.ui.models.Message
import com.example.assistantx.ui.models.MessageRole
import com.example.assistantx.ui.models.MessageStatus
import com.example.assistantx.ui.theme.StatusGreen
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary

@Composable
fun MessageBubble(message: Message) {
    val isUser = message.role == MessageRole.USER
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    val bubbleColor = if (isUser) Color(0x3338BDF8) else Color(0x331E293B)
    val borderColor = if (isUser) Color(0x5538BDF8) else Color(0x22FFFFFF)
    val textColor = TextPrimary

    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Row(
            modifier = Modifier
                .clip(shape)
                .background(bubbleColor, shape)
                .border(1.dp, borderColor, shape)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 280.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isUser && message.status != MessageStatus.NONE) {
                // Status Indicator
                when (message.status) {
                    MessageStatus.PENDING -> {
                        CircularProgressIndicator(
                            color = Color(0xFF38BDF8),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    MessageStatus.SUCCESS -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = StatusGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    MessageStatus.ERROR -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Error",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    else -> {}
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = message.text,
                color = textColor,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}
