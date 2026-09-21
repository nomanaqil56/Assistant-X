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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.ui.theme.TextSecondary

/**
 * Top app icons row (WhatsApp, Snapchat, Instagram) as shown in the reference.
 */
@Composable
fun TopAppRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconItem(
            name = "WhatsApp",
            icon = Icons.Default.Chat,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF25D366), Color(0xFF128C7E))
            )
        )
        AppIconItem(
            name = "Snapchat",
            icon = Icons.Default.Chat,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFC00), Color(0xFFFFD600))
            ),
            iconTint = Color.Black,
            hasBadge = true
        )
        AppIconItem(
            name = "Instagram",
            icon = Icons.Default.CameraAlt,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
            ),
            hasBadge = true
        )
    }
}

/**
 * Bottom dock launcher row (Phone, Contacts, Messages, Camera, Settings).
 */
@Composable
fun BottomDockRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DockIcon(
            icon = Icons.Default.Call,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF00E676), Color(0xFF00C853))
            )
        )
        DockIcon(
            icon = Icons.Default.Person,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF00BFA5), Color(0xFF00897B))
            )
        )
        DockIcon(
            icon = Icons.Default.Chat,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0))
            ),
            iconTint = Color(0xFF2563EB)
        )
        DockIcon(
            icon = Icons.Default.CameraAlt,
            backgroundBrush = Brush.radialGradient(
                colors = listOf(Color(0xFF334155), Color(0xFF0F172A), Color(0xFF020617))
            ),
            border = 2.dp to Color(0xFF94A3B8)
        )
        DockIcon(
            icon = Icons.Default.Settings,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF475569), Color(0xFF1E293B))
            ),
            border = 1.dp to Color(0xFF64748B)
        )
    }
}

@Composable
private fun AppIconItem(
    name: String,
    icon: ImageVector,
    backgroundBrush: Brush,
    iconTint: Color = Color.White,
    hasBadge: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(backgroundBrush),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFFFBBF24))
                        .border(2.dp, Color.Black, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            color = Color(0xFFCBD5E1),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun DockIcon(
    icon: ImageVector,
    backgroundBrush: Brush,
    iconTint: Color = Color.White,
    border: Pair<androidx.compose.ui.unit.Dp, Color>? = null
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(backgroundBrush)
            .then(
                if (border != null) Modifier.border(border.first, border.second, CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(26.dp)
        )
    }
}
