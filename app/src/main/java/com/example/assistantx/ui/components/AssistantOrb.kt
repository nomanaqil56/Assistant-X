package com.example.assistantx.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
 * A complex, continuous-loop particle sphere that reacts organically to user touch.
 * Features 3D manual rotation, energy core expansion, and particle scattering on press.
 */
@Composable
fun AssistantOrb(
    modifier: Modifier = Modifier,
    size: Dp = 175.dp
) {
    // Generate particle data exactly once
    val particles = remember {
        val list = ArrayList<Particle>(3500)
        val random = Random()

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
                    flowSpeed = 2, // Moves 2x faster than base sphere
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

    // Slow global rotation around the Y-axis to enhance 3D effect natively
    val globalRotY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GlobalRotation"
    )

    // --- INTERACTION STATE ---
    val coroutineScope = rememberCoroutineScope()
    // 0f = Resting, 1f = Fully Charged / Touched
    val energyLevel = remember { Animatable(0f) }
    
    // Manual 3D rotations driven by user drag
    var userRotX by remember { mutableFloatStateOf(0.35f) } // Initial tilt
    var userRotY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        
                        // ON TOUCH DOWN: Surge energy!
                        coroutineScope.launch {
                            energyLevel.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
                        }
                        
                        var previousPosition = down.position
                        
                        // TRACK MOVEMENT
                        do {
                            val event = awaitPointerEvent()
                            val pointer = event.changes.firstOrNull()
                            if (pointer != null && pointer.pressed) {
                                val delta = pointer.position - previousPosition
                                // Apply swipe delta to 3D rotation angles
                                userRotY += delta.x * 0.008f
                                userRotX -= delta.y * 0.008f 
                                previousPosition = pointer.position
                            }
                        } while (event.changes.any { it.pressed })

                        // ON RELEASE: Smoothly dissipate energy back to normal
                        coroutineScope.launch {
                            energyLevel.animateTo(0f, tween(1200, easing = FastOutSlowInEasing))
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2f) * 0.85f
            
            // Orb expands by 15% when fully touched/charged
            val currentBaseRadius = baseRadius * (1f + 0.15f * energyLevel.value)

            // 1. Draw ambient deep space background glow (intensifies on touch)
            val glowIntensity = 1f + energyLevel.value * 0.8f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0055FF).copy(alpha = 0.3f * glowIntensity),
                        Color(0xFF0022AA).copy(alpha = 0.1f * glowIntensity),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentBaseRadius * 1.6f
                ),
                radius = currentBaseRadius * 1.6f,
                center = center
            )

            // 2. Central Super-Bright Core that ignites on touch
            if (energyLevel.value > 0f) {
                val coreAlpha = 0.6f * energyLevel.value
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = coreAlpha),
                            Color(0xFF00E5FF).copy(alpha = coreAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = currentBaseRadius * 0.8f
                    ),
                    radius = currentBaseRadius * 0.8f,
                    center = center,
                    blendMode = BlendMode.Plus
                )
            }

            // 3D Rotation Math prep (combining auto-rotation and user swiping)
            val rotX = userRotX
            val sinX = sin(rotX)
            val cosX = cos(rotX)
            
            val totalRotY = globalRotY + userRotY
            val sinY = sin(totalRotY)
            val cosY = cos(totalRotY)

            // 3. Iterate and draw 3500 particles in true 3D space
            for (i in particles.indices) {
                val p = particles[i]
                
                // Flow the particle endlessly along its path
                val currentTheta = p.initialTheta + time * p.flowSpeed
                val currentPhi = p.initialPhi
                
                // When touched, particles scatter/vibrate chaotically based on energy level
                val chaos = sin(currentTheta * 15f + time * 10f) * cos(currentPhi * 15f) * 0.15f * energyLevel.value
                val r = currentBaseRadius * (p.radiusMult + chaos)

                // Spherical to Cartesian Coordinates
                val x0 = r * cos(currentPhi) * cos(currentTheta)
                val y0 = r * sin(currentPhi)
                val z0 = r * cos(currentPhi) * sin(currentTheta)

                // Apply Global Y-axis Rotation
                val x1 = x0 * cosY - z0 * sinY
                val z1 = x0 * sinY + z0 * cosY
                val y1 = y0

                // Apply Fixed X-axis Tilt
                val x2 = x1
                val y2 = y1 * cosX - z1 * sinX
                val z2 = y1 * sinX + z1 * cosX

                val screenX = center.x + x2
                val screenY = center.y + y2

                // Calculate Depth (z ranges roughly from -r to +r)
                val depthNorm = ((z2 + currentBaseRadius) / (2 * currentBaseRadius)).coerceIn(0f, 1f)

                // Depth & Energy Effects
                val alphaScale = 0.15f + 0.85f * depthNorm
                val energyAlphaBoost = 1f + 1.5f * energyLevel.value
                val finalAlpha = (p.color.alpha * alphaScale * energyAlphaBoost).coerceIn(0f, 1f)
                
                val energySizeBoost = 1f + 0.8f * energyLevel.value
                val finalSize = p.baseSize * (0.5f + 0.5f * depthNorm) * energySizeBoost

                // Pulsating energy effect for the fast moving ribbons
                val energyPulse = if (p.flowSpeed > 1) {
                    0.8f + 0.2f * sin(currentTheta * 5f)
                } else 1f

                // Draw the particle using Additive Blending (BlendMode.Plus) for glowing energy look
                drawCircle(
                    color = p.color.copy(alpha = finalAlpha),
                    radius = finalSize * energyPulse,
                    center = Offset(screenX, screenY),
                    blendMode = BlendMode.Plus
                )
            }
            
            // 4. Subtle inner vignette to give a spherical volume shadow
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
