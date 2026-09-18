package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

val SCAN_STEPS = listOf(
    "Looking chart frame & aspect ratio...",
    "Reading right edge live price action...",
    "Mapping accumulation & structure range...",
    "Hunting micro jump & liquidity sweep...",
    "Measuring candle displacement velocity...",
    "Tagging Fair Value Gaps (FVG) & Order Blocks...",
    "Evaluating major Support & Resistance key levels...",
    "Correlating Macro News & Session Order Flow...",
    "Checking setup validity & risk parameters...",
    "Grading high-probability SMC execution..."
)

@Composable
fun ScannerTerminal(
    isScanning: Boolean,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            currentStepIndex = 0
            while (currentStepIndex < SCAN_STEPS.size - 1) {
                delay(650)
                currentStepIndex++
            }
        } else {
            currentStepIndex = 0
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laserOffset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberCardBg, shape = RoundedCornerShape(14.dp))
            .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Terminal Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = NeonGreen,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "SCANNING CHART MATRIX",
                    color = NeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "${((currentStepIndex + 1) * 10).coerceAtMost(100)}%",
                color = NeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Cyber Laser Progress Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFF1E293B), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(laserOffset)
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(NeonGreen.copy(alpha = 0.2f), NeonGreen)
                        ),
                        RoundedCornerShape(2.dp)
                    )
            )
        }

        // Terminal Log Checklist
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            SCAN_STEPS.take((currentStepIndex + 1).coerceAtMost(SCAN_STEPS.size)).forEachIndexed { index, step ->
                val isCompleted = index < currentStepIndex
                val isCurrent = index == currentStepIndex

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Step Complete",
                            tint = NeonGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(NeonGreen, CircleShape)
                            )
                        }
                    }

                    Text(
                        text = step,
                        color = if (isCompleted) TextSecondary else TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.alpha(if (isCompleted) 0.8f else 1f)
                    )
                }
            }
        }
    }
}
