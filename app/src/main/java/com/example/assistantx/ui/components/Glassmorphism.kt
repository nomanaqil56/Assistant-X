package com.example.assistantx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A reusable container with frosted glassmorphism effect, subtle edge glow flares, and customizable shape.
 */
@Composable
fun GlassmorphicContainer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    borderWidth: Dp = 1.dp,
    hasEdgeGlow: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val glassFillBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x99182230),
            Color(0xB3101824),
            Color(0xD90C121A)
        )
    )

    val borderGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0x66FFFFFF),
            Color(0x22FFFFFF),
            Color(0x10FFFFFF),
            Color(0x3360A5FA)
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = glassFillBrush, shape = shape)
            .border(
                width = borderWidth,
                brush = borderGradient,
                shape = shape
            )
            .then(
                if (hasEdgeGlow) {
                    Modifier.drawWithContent {
                        drawContent()
                        // Left subtle edge cyan/blue glint
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x4438BDF8), Color.Transparent),
                                center = Offset(0f, size.height * 0.45f),
                                radius = size.width * 0.35f
                            ),
                            radius = size.width * 0.35f,
                            center = Offset(0f, size.height * 0.45f)
                        )
                        // Right subtle edge flare
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x3360A5FA), Color.Transparent),
                                center = Offset(size.width, size.height * 0.5f),
                                radius = size.width * 0.3f
                            ),
                            radius = size.width * 0.3f,
                            center = Offset(size.width, size.height * 0.5f)
                        )
                    }
                } else Modifier
            ),
        content = content
    )
}
