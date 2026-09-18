package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SellRed
import com.example.ui.theme.TextMuted

@Composable
fun TickerBar(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ticker_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberBlack)
            .drawBehind {
                drawLine(
                    color = CyberCardBorder,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1f
                )
            }
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(CyberCardBg, shape = CircleShape)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .alpha(pulseAlpha)
                        .background(NeonGreen, shape = CircleShape)
                )
                Text(
                    text = "AI ENGINE ONLINE",
                    color = NeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            TickerItem(symbol = "XAUUSD", price = "2,348.50", change = "+0.45%", isUp = true)
            TickerSeparator()
            TickerItem(symbol = "BTCUSD", price = "68,410.00", change = "+1.82%", isUp = true)
            TickerSeparator()
            TickerItem(symbol = "EURUSD", price = "1.0845", change = "-0.12%", isUp = false)
            TickerSeparator()
            TickerItem(symbol = "NAS100", price = "18,340.20", change = "+0.65%", isUp = true)
            TickerSeparator()
            TickerItem(symbol = "US30", price = "39,120.00", change = "-0.08%", isUp = false)
        }
    }
}

@Composable
private fun TickerItem(
    symbol: String,
    price: String,
    change: String,
    isUp: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = symbol,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = price,
            color = Color(0xFFD1D5DB),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = change,
            color = if (isUp) NeonGreen else SellRed,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun TickerSeparator() {
    Text(
        text = "•",
        color = TextMuted,
        fontSize = 10.sp
    )
}
