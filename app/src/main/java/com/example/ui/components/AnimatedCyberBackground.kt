package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.NeonGreen
import kotlin.math.sin
import kotlin.random.Random

private data class CyberParticle(
    val initialX: Float,
    val initialY: Float,
    val radius: Float,
    val speedY: Float,
    val color: Color
)

@Composable
fun AnimatedCyberBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CyberBgAnimation")

    // Breathing scale animation for the background image
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1.02f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CyberBreathingScale"
    )

    // Vertical holographic scanner beam progress (0f to 1f)
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CyberScanline"
    )

    // Particle drift time
    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CyberParticles"
    )

    // Glowing cyber particles
    val particles = remember {
        val random = Random(99)
        List(28) {
            val isGreen = random.nextBoolean()
            CyberParticle(
                initialX = random.nextFloat(),
                initialY = random.nextFloat(),
                radius = random.nextFloat() * 2f + 1f,
                speedY = -(random.nextFloat() * 0.08f + 0.03f),
                color = if (isGreen) NeonGreen else AccentCyan
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Base image with animated breathing scale
        Image(
            painter = painterResource(id = R.drawable.img_cyber_bg),
            contentDescription = "Cyberpunk Trader Background",
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleAnim),
            contentScale = ContentScale.Crop
        )

        // Dark gradient scrim overlay to preserve high UI contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CyberBlack.copy(alpha = 0.55f),
                            Color(0xFF060B12).copy(alpha = 0.65f),
                            CyberBlack.copy(alpha = 0.90f)
                        )
                    )
                )
        )

        // Animated laser scanline & rising holographic particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Draw glowing holographic scanline
            val lineY = scanlineY * h
            val beamHeight = 40f
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        AccentCyan.copy(alpha = 0.28f),
                        AccentCyan.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = lineY - beamHeight,
                    endY = lineY + beamHeight
                ),
                topLeft = Offset(0f, lineY - beamHeight),
                size = androidx.compose.ui.geometry.Size(w, beamHeight * 2)
            )

            // Sharp center beam line
            drawLine(
                color = AccentCyan.copy(alpha = 0.5f),
                start = Offset(0f, lineY),
                end = Offset(w, lineY),
                strokeWidth = 1.5f
            )

            // 2. Draw rising cyber particles
            particles.forEach { p ->
                val rawY = (p.initialY + p.speedY * particleTime) % 1f
                val cy = (if (rawY < 0) rawY + 1f else rawY) * h
                val cx = p.initialX * w

                // Pulsing glow
                drawCircle(
                    color = p.color.copy(alpha = 0.2f),
                    radius = p.radius * 3.5f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = p.color.copy(alpha = 0.75f),
                    radius = p.radius,
                    center = Offset(cx, cy)
                )
            }
        }

        content()
    }
}
