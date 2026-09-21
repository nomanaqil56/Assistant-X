package com.example.assistantx.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Recreates the central luminous assistant orb from the visual reference with
 * atmospheric blue halo, 3D spherical depth, and flowing inner wave ribbon.
 */
@Composable
fun AssistantOrb(
    modifier: Modifier = Modifier,
    size: Dp = 175.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbPulse")

    // Subtle breathing scale/glow animation
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = -0.05f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveFluctuation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2f) * 0.90f

            // 1. Outer Atmospheric Blue Glow Halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3538BDF8),
                        Color(0x1A60A5FA),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.35f * pulseGlow
                ),
                radius = radius * 1.35f * pulseGlow,
                center = center
            )

            // 2. Base Spherical Dark Core with subtle radial shading
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF141D2B),
                        Color(0xFF090D14),
                        Color(0xFF04060A)
                    ),
                    center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
                    radius = radius * 1.1f
                ),
                radius = radius,
                center = center
            )

            // 3. Inner Fluid Wave Ribbon (Yin-Yang smooth ribbon)
            val wavePath = Path().apply {
                val w = radius * 2f
                val h = radius * 2f
                val left = center.x - radius
                val top = center.y - radius

                // Starting from left center
                moveTo(left + w * 0.12f, top + h * (0.50f + waveOffset))

                // Upper wave crest
                cubicTo(
                    left + w * 0.28f, top + h * (0.28f + waveOffset * 0.5f),
                    left + w * 0.48f, top + h * (0.34f + waveOffset * 0.8f),
                    left + w * 0.50f, top + h * 0.50f
                )

                // Lower wave trough
                cubicTo(
                    left + w * 0.52f, top + h * (0.66f - waveOffset * 0.8f),
                    left + w * 0.72f, top + h * (0.72f - waveOffset * 0.5f),
                    left + w * 0.88f, top + h * (0.50f - waveOffset)
                )

                // Return along smooth back contour
                cubicTo(
                    left + w * 0.76f, top + h * (0.58f - waveOffset * 0.6f),
                    left + w * 0.58f, top + h * (0.56f - waveOffset * 0.4f),
                    left + w * 0.50f, top + h * 0.50f
                )
                cubicTo(
                    left + w * 0.42f, top + h * (0.44f + waveOffset * 0.4f),
                    left + w * 0.24f, top + h * (0.42f + waveOffset * 0.6f),
                    left + w * 0.12f, top + h * (0.50f + waveOffset)
                )
                close()
            }

            // Draw primary fluid wave ribbon body
            drawPath(
                path = wavePath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF475569),
                        Color(0xFFCBD5E1),
                        Color(0xFFFFFFFF),
                        Color(0xFFCBD5E1),
                        Color(0xFF334155)
                    ),
                    start = Offset(center.x - radius, center.y - radius),
                    end = Offset(center.x + radius, center.y + radius)
                ),
                style = Fill
            )

            // Draw upper wave glow plume
            val wavePlume = Path().apply {
                val w = radius * 2f
                val h = radius * 2f
                val left = center.x - radius
                val top = center.y - radius

                moveTo(left + w * 0.15f, top + h * 0.50f)
                cubicTo(
                    left + w * 0.30f, top + h * 0.25f,
                    left + w * 0.50f, top + h * 0.30f,
                    left + w * 0.50f, top + h * 0.50f
                )
                cubicTo(
                    left + w * 0.35f, top + h * 0.45f,
                    left + w * 0.25f, top + h * 0.48f,
                    left + w * 0.15f, top + h * 0.50f
                )
                close()
            }

            drawPath(
                path = wavePlume,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x99FFFFFF),
                        Color(0x3394A3B8),
                        Color.Transparent
                    ),
                    center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
                    radius = radius * 0.65f
                ),
                style = Fill
            )

            // 4. Subtle Inner Vignette & Glass Shadow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x55030712),
                        Color(0xCC030712)
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // 5. Crisp Outer Rim Stroke with Specular Blue/White Glint
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0x5594A3B8),
                        Color(0xFF60A5FA),
                        Color(0xFFFFFFFF),
                        Color(0xFF38BDF8),
                        Color(0x3364748B),
                        Color(0x5594A3B8)
                    ),
                    center = center
                ),
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 6. Extra Right Side Rim Flare Light
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xAA38BDF8),
                        Color(0x4460A5FA),
                        Color.Transparent
                    ),
                    center = Offset(center.x + radius * 0.88f, center.y - radius * 0.15f),
                    radius = radius * 0.45f
                ),
                radius = radius * 0.45f,
                center = Offset(center.x + radius * 0.88f, center.y - radius * 0.15f)
            )
        }
    }
}
