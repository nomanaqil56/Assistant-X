package com.example.assistantx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Horizontal frosted navigation dock pill placed at the bottom center of the screen.
 */
@Composable
fun NavigationDockPill(
    modifier: Modifier = Modifier,
    selectedItem: NavItem = NavItem.HOME,
    onItemSelected: (NavItem) -> Unit = {}
) {
    val pillShape = RoundedCornerShape(30.dp)

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
            .height(58.dp)
            .clip(pillShape)
            .background(backgroundBrush, pillShape)
            .border(1.dp, borderBrush, pillShape)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            NavItem.values().forEach { item ->
                val isSelected = item == selectedItem
                NavDockIcon(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

@Composable
private fun NavDockIcon(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isSelected) {
        val activeShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .height(46.dp)
                .width(52.dp),
            contentAlignment = Alignment.Center
        ) {
            // Active background with subtle border
            Box(
                modifier = Modifier
                    .size(42.dp)
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

            // Top edge glowing indicator bar
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .height(3.dp)
                    .width(22.dp)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
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
                .size(42.dp)
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
