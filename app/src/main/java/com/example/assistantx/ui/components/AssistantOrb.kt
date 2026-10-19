package com.example.assistantx.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assistantx.core.state.AssistantState
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

data class Particle(
    val initialTheta: Float,
    val initialPhi: Float,
    val color: Color,
    val baseSize: Float,
    val flowSpeed: Int,
    val radiusMult: Float
)

/**
 * Next-Generation Luminous Fluid Assistant Orb.
 * Features organic multi-harmonic 3D particle sphere physics, continuous smooth flow,
 * state-driven color/scale transitions, and full touch/swipe 3D interaction.
 */
@Composable
fun AssistantOrb(
    modifier: Modifier = Modifier,
    size: Dp = 175.dp,
    state: AssistantState = AssistantState.IDLE
) {
    // Generate particle data exactly once
    val particles = remember {
        val list = ArrayList<Particle>(3500)
        val random = Random(42)

        // 1. Base Sphere Density (1500)
        repeat(1500) {
            val u = random.nextFloat()
            val v = random.nextFloat()
            val theta = (u * 2.0 * Math.PI).toFloat()
            val phi = (acos(2.0 * v - 1.0) - Math.PI / 2.0).toFloat()
            list.add(
                Particle(
                    initialTheta = theta,
                    initialPhi = phi,
                    color = Color(0xFF1E88E5).copy(alpha = 0.5f),
                    baseSize = 1.5f + random.nextFloat() * 1.5f,
                    flowSpeed = 1,
                    radiusMult = 1.0f
                )
            )
        }

        // 2. Swirling Ribbon 1: Pink & Bright Blue (1000)
        repeat(1000) {
            val theta = (random.nextFloat() * 2 * Math.PI).toFloat()
            val basePhi = sin(theta * 2.0) * (Math.PI / 3.0)
            val spread = (random.nextFloat() - 0.5) * 0.4
            val phi = (basePhi + spread).toFloat()
            val isPink = random.nextFloat() > 0.4f
            val color = if (isPink) Color(0xFFFF4081) else Color(0xFF00E5FF)
            list.add(
                Particle(
                    initialTheta = theta,
                    initialPhi = phi,
                    color = color.copy(alpha = 0.8f),
                    baseSize = 2f + random.nextFloat() * 2f,
                    flowSpeed = 2,
                    radiusMult = 1.0f + random.nextFloat() * 0.03f
                )
            )
        }

        // 3. Swirling Ribbon 2: Deep Blue (800)
        repeat(800) {
            val theta = (random.nextFloat() * 2 * Math.PI).toFloat()
            val basePhi = cos(theta * 2.0) * (Math.PI / 3.0)
            val spread = (random.nextFloat() - 0.5) * 0.4
            val phi = (basePhi + spread).toFloat()
            list.add(
                Particle(
                    initialTheta = theta,
                    initialPhi = phi,
                    color = Color(0xFF2979FF).copy(alpha = 0.8f),
                    baseSize = 2f + random.nextFloat() * 2f,
                    flowSpeed = 2,
                    radiusMult = 1.0f + random.nextFloat() * 0.03f
                )
            )
        }

        // 4. Floating Dust outside the sphere (200)
        repeat(200) {
            val u = random.nextFloat()
            val v = random.nextFloat()
            val theta = (u * 2.0 * Math.PI).toFloat()
            val phi = (acos(2.0 * v - 1.0) - Math.PI / 2.0).toFloat()
            list.add(
                Particle(
                    initialTheta = theta,
                    initialPhi = phi,
                    color = Color(0xFF00B0FF).copy(alpha = 0.4f),
                    baseSize = 1f + random.nextFloat() * 2f,
                    flowSpeed = 1,
                    radiusMult = 1.05f + random.nextFloat() * 0.2f
                )
            )
        }

        list
    }

    val infiniteTransition = rememberInfiniteTransition(label = "OrbMasterTransition")

    // Core continuous time for flowing particles (0 to 2PI)
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TimeFlow"
    )

    // Slow global rotation around the Y-axis
    val globalRotY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GlobalRotation"
    )

    // Breathing scale for IDLE state
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheScale"
    )

    // Manual 3D rotations driven by user drag
    val userRotX = remember { mutableStateOf(0.35f) } // Initial tilt
    val userRotY = remember { mutableStateOf(0f) }

    // State-driven animatable properties
    val touchEnergy = remember { Animatable(0f) }
    val stateEnergy = remember { Animatable(0f) }
    val baseScale = remember { Animatable(1f) }

    val targetColor by animateColorAsState(
        targetValue = when (state) {
            AssistantState.IDLE -> Color(0xFF0055FF)
            AssistantState.LISTENING -> Color(0xFF00E5FF)
            AssistantState.PROCESSING, AssistantState.PLANNING -> Color(0xFF8C9EFF)
            AssistantState.EXECUTING -> Color(0xFF2979FF)
            AssistantState.SPEAKING -> Color(0xFFFF4081)
            AssistantState.COMPLETED -> Color(0xFF00E676)
            AssistantState.ERROR -> Color(0xFFFF1744)
            AssistantState.WAITING_FOR_CONFIRMATION -> Color(0xFFFFB300)
            else -> Color(0xFF0055FF)
        },
        animationSpec = tween(600),
        label = "TargetColor"
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state) {
        coroutineScope.launch {
            when (state) {
                AssistantState.IDLE -> {
                    stateEnergy.animateTo(0f, tween(800))
                    baseScale.animateTo(1f, tween(800))
                }
                AssistantState.LISTENING -> {
                    stateEnergy.animateTo(0.5f, tween(300))
                    baseScale.animateTo(1.15f, tween(400, easing = FastOutSlowInEasing))
                }
                AssistantState.PROCESSING, AssistantState.PLANNING -> {
                    stateEnergy.animateTo(0.8f, tween(500))
                    baseScale.animateTo(1.05f, tween(400))
                }
                AssistantState.EXECUTING -> {
                    stateEnergy.animateTo(0.5f, tween(400))
                    baseScale.animateTo(1.1f, tween(400))
                }
                AssistantState.SPEAKING -> {
                    stateEnergy.animateTo(0.6f, tween(300))
                    baseScale.animateTo(1.1f, tween(300))
                }
                AssistantState.COMPLETED -> {
                    stateEnergy.animateTo(0.9f, tween(200))
                    baseScale.animateTo(1.2f, tween(200))
                }
                AssistantState.ERROR -> {
                    stateEnergy.animateTo(0.9f, tween(100))
                    baseScale.animateTo(1.1f, tween(100))
                }
                AssistantState.WAITING_FOR_CONFIRMATION -> {
                    stateEnergy.animateTo(0.4f, tween(500))
                    baseScale.animateTo(1.05f, tween(500))
                }
                else -> {
                    stateEnergy.animateTo(0f, tween(800))
                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val down = event.changes.firstOrNull { it.pressed } ?: continue

                        // ON TOUCH DOWN: Surge touch energy!
                        coroutineScope.launch {
                            touchEnergy.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
                        }

                        var previousPosition = down.position

                        try {
                            // TRACK MOVEMENT
                            do {
                                val dragEvent = awaitPointerEvent()
                                val pointer = dragEvent.changes.firstOrNull()
                                if (pointer != null && pointer.pressed) {
                                    val delta = pointer.position - previousPosition
                                    // Apply swipe delta to 3D rotation angles
                                    userRotY.value += delta.x * 0.008f
                                    userRotX.value -= delta.y * 0.008f
                                    previousPosition = pointer.position
                                }
                            } while (dragEvent.changes.any { it.pressed })
                        } finally {
                            // ON RELEASE OR CANCELLATION: Smoothly dissipate touch energy back to zero
                            coroutineScope.launch {
                                touchEnergy.animateTo(0f, tween(1200, easing = FastOutSlowInEasing))
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Calculate total energy (touch + state)
            val combinedEnergy = (touchEnergy.value + stateEnergy.value).coerceIn(0f, 1f)

            // Apply state base scale and breathe scale if idle
            val currentScale = if (state == AssistantState.IDLE) baseScale.value * breatheScale else baseScale.value
            val baseRadius = (this.size.minDimension / 2f) * 0.85f * currentScale

            // Orb expands by up to 15% when charged/touched
            val currentBaseRadius = baseRadius * (1f + 0.15f * combinedEnergy)

            // 1. Ambient glow background
            val glowIntensity = 0.5f + combinedEnergy * 0.8f
            val glowColor = if (state == AssistantState.IDLE && touchEnergy.value > 0f) Color(0xFF0055FF) else targetColor

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.3f * glowIntensity),
                        glowColor.copy(alpha = 0.1f * glowIntensity),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentBaseRadius * 1.6f
                ),
                radius = currentBaseRadius * 1.6f,
                center = center
            )

            // 2. Central Core
            val coreAlpha = 0.2f + 0.6f * combinedEnergy
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = coreAlpha),
                        glowColor.copy(alpha = coreAlpha * 0.8f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentBaseRadius * 0.8f
                ),
                radius = currentBaseRadius * 0.8f,
                center = center,
                blendMode = BlendMode.Plus
            )

            // 3D Rotation Math
            val rotX = userRotX.value
            val sinX = sin(rotX)
            val cosX = cos(rotX)

            val totalRotY = globalRotY + userRotY.value
            val sinY = sin(totalRotY)
            val cosY = cos(totalRotY)

            // 3. Particles
            for (i in particles.indices) {
                val p = particles[i]

                val currentTheta = p.initialTheta + time * p.flowSpeed
                val currentPhi = p.initialPhi

                // Chaotic particle vibration on energy surge
                val chaos = sin(currentTheta * 15f + time * 10f) * cos(currentPhi * 15f) * 0.15f * combinedEnergy
                val r = currentBaseRadius * (p.radiusMult + chaos)

                // Spherical to Cartesian Coordinates
                val x0 = r * cos(currentPhi) * cos(currentTheta)
                val y0 = r * sin(currentPhi)
                val z0 = r * cos(currentPhi) * sin(currentTheta)

                // Apply Y-axis Rotation (Global + User)
                val x1 = x0 * cosY - z0 * sinY
                val z1 = x0 * sinY + z0 * cosY
                val y1 = y0

                // Apply X-axis Tilt (User)
                val x2 = x1
                val y2 = y1 * cosX - z1 * sinX
                val z2 = y1 * sinX + z1 * cosX

                val screenX = center.x + x2
                val screenY = center.y + y2

                // Calculate Depth (z ranges roughly from -r to +r)
                val depthNorm = ((z2 + currentBaseRadius) / (2 * currentBaseRadius)).coerceIn(0f, 1f)

                // Depth & Energy Effects
                val alphaScale = 0.15f + 0.85f * depthNorm
                val energyAlphaBoost = 1f + 1.5f * combinedEnergy

                // Color blending towards state color when state is active
                val pColor = if (state != AssistantState.IDLE) {
                    lerp(p.color, targetColor, 0.6f)
                } else p.color

                val finalAlpha = (pColor.alpha * alphaScale * energyAlphaBoost).coerceIn(0f, 1f)

                val energySizeBoost = 1f + 0.8f * combinedEnergy
                val finalSize = p.baseSize * (0.5f + 0.5f * depthNorm) * energySizeBoost

                // Pulsating energy effect for the fast moving ribbons
                val energyPulse = if (p.flowSpeed > 1) {
                    0.8f + 0.2f * sin(currentTheta * 5f)
                } else 1f

                drawCircle(
                    color = pColor.copy(alpha = finalAlpha),
                    radius = finalSize * energyPulse,
                    center = Offset(screenX, screenY),
                    blendMode = BlendMode.Plus
                )
            }

            // 4. Subtle inner vignette
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color(0x33030712)
                    ),
                    center = center,
                    radius = currentBaseRadius
                ),
                radius = currentBaseRadius,
                center = center
            )
        }
    }
}
