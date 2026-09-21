package com.example.assistantx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }

    GlassmorphicContainer(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(26.dp),
        hasEdgeGlow = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title & Local AI Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Assistant X",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Your Private AI Assistant",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Status Badge "● Local AI"
                LocalAiBadge()
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Central Glowing Assistant Orb
            AssistantOrb(
                size = 175.dp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Central Prompt Typography
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

            Spacer(modifier = Modifier.height(34.dp))

            // Input Bar
            AssistantInputBar(
                text = inputText,
                onTextChanged = { inputText = it },
                onAddClicked = { /* Optional attachment hook */ },
                onMicClicked = { /* Voice input hook */ }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer tagline
            Text(
                text = "Fast  •  Private  •  On Device",
                color = TextTertiary,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun LocalAiBadge() {
    val badgeShape = RoundedCornerShape(20.dp)
    val badgeBg = Brush.horizontalGradient(
        colors = listOf(
            Color(0xCC121C29),
            Color(0x99172333)
        )
    )

    Box(
        modifier = Modifier
            .clip(badgeShape)
            .background(badgeBg, badgeShape)
            .border(1.dp, Color(0x33475569), badgeShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Glowing Green Dot
            Box(
                modifier = Modifier
                    .size(6.5.dp)
                    .clip(CircleShape)
                    .background(StatusGreen)
            )

            Text(
                text = "Local AI",
                color = Color(0xFFE2E8F0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
