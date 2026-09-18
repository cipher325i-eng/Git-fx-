package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.SkyBlueDarkBg
import com.example.ui.theme.SkyBlueDeep
import com.example.ui.theme.SkyBlueMidnight
import com.example.ui.theme.StarCyan
import com.example.ui.theme.StarWhite
import kotlin.math.sin
import kotlin.random.Random

private data class Star(
    val initialX: Float, // 0f..1f
    val initialY: Float, // 0f..1f
    val radius: Float,
    val speedX: Float,
    val speedY: Float,
    val color: Color,
    val twinklePeriodMs: Int,
    val phaseOffset: Float
)

@Composable
fun AnimatedStarrySkyBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    // Generate deterministic stars once
    val stars = remember {
        val random = Random(42)
        List(65) {
            val isCyan = random.nextFloat() < 0.4f
            val isAccent = random.nextFloat() < 0.15f
            val starColor = when {
                isAccent -> SkyBlueAccent
                isCyan -> StarCyan
                else -> StarWhite
            }
            Star(
                initialX = random.nextFloat(),
                initialY = random.nextFloat(),
                radius = random.nextFloat() * 2.2f + 0.8f,
                speedX = (random.nextFloat() - 0.5f) * 0.04f, // slow horizontal drift
                speedY = -(random.nextFloat() * 0.07f + 0.02f), // upwards floating drift
                color = starColor,
                twinklePeriodMs = random.nextInt(1800, 4500),
                phaseOffset = random.nextFloat() * 6.28f
            )
        }
    }

    // Infinite time driving the stars and twinkling
    val infiniteTransition = rememberInfiniteTransition(label = "StarryAnimation")
    val timeProgression by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "StarDrift"
    )

    val twinkleProgression by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "StarTwinkle"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Deep Dark Sky Blue Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlueDarkBg,
                        SkyBlueDeep,
                        SkyBlueMidnight,
                        SkyBlueDarkBg
                    ),
                    startY = 0f,
                    endY = size.height
                )
            )

            // Draw Subtle Sky Blue Radial Nebula Glow at top right and center left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SkyBlueAccent.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.25f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.8f, size.height * 0.25f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6366F1).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.75f),
                    radius = size.width * 0.5f
                ),
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.2f, size.height * 0.75f)
            )

            // Draw moving stars
            val w = size.width
            val h = size.height

            stars.forEach { star ->
                // Compute current position with wrap-around
                val rawX = (star.initialX + star.speedX * timeProgression) % 1f
                val currentX = (if (rawX < 0) rawX + 1f else rawX) * w

                val rawY = (star.initialY + star.speedY * timeProgression) % 1f
                val currentY = (if (rawY < 0) rawY + 1f else rawY) * h

                // Twinkle alpha calculation
                val twinkle = (sin((twinkleProgression + star.phaseOffset).toDouble()).toFloat() + 1f) / 2f
                val alpha = 0.25f + twinkle * 0.75f

                // Draw soft glow halo for larger stars
                if (star.radius > 1.8f) {
                    drawCircle(
                        color = star.color.copy(alpha = alpha * 0.25f),
                        radius = star.radius * 3.5f,
                        center = Offset(currentX, currentY)
                    )
                }

                // Core star
                drawCircle(
                    color = star.color.copy(alpha = alpha),
                    radius = star.radius,
                    center = Offset(currentX, currentY)
                )
            }
        }

        content()
    }
}
