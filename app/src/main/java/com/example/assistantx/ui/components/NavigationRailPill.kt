package com.example.assistantx.ui.components

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class NavItem(val icon: ImageVector, val label: String) {
    HOME(Icons.Filled.Home, "Home"),
    HISTORY(Icons.Outlined.Schedule, "History"),
    SETTINGS(Icons.Outlined.Settings, "Settings"),
    POWER(Icons.Outlined.PowerSettingsNew, "Power")
}

/**
 * Vertical frosted navigation pill on the left edge as shown in reference design.
 */
@Composable
fun NavigationRailPill(
    selectedItem: NavItem = NavItem.HOME,
    onItemSelected: (NavItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pillShape = RoundedCornerShape(26.dp)

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x99182332),
            Color(0xB3101824),
            Color(0xCC0D141F)
        )
    )

    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x88FFFFFF),
            Color(0x22FFFFFF),
            Color(0x15FFFFFF),
            Color(0x3360A5FA)
        )
    )

    Box(
        modifier = modifier
            .width(52.dp)
            .clip(pillShape)
            .background(backgroundBrush, pillShape)
            .border(1.dp, borderBrush, pillShape)
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            NavItem.values().forEach { item ->
                val isSelected = item == selectedItem
                NavRailIcon(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

@Composable
private fun NavRailIcon(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isSelected) {
        val activeShape = RoundedCornerShape(14.dp)
        Box(
            modifier = Modifier
                .width(52.dp) // Fill width of rail to allow left indicator
                .height(44.dp),
            contentAlignment = Alignment.Center
        ) {
            // Active background with border
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(activeShape)
                    .background(Color(0x55334155), activeShape)
                    .border(1.dp, Color(0x66FFFFFF), activeShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Left edge glowing white bar
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.White, Color(0x99FFFFFF))
                        )
                    )
            )
        }
    } else {
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(44.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = Color(0xFFE2E8F0),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
