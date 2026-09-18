package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChartAnalysis
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeutralGray
import com.example.ui.theme.SellRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalysisCard(
    analysis: ChartAnalysis,
    modifier: Modifier = Modifier,
    timestamp: Long? = null,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val directionColor = when {
        analysis.isBuy -> BuyGreen
        analysis.isSell -> SellRed
        else -> NeutralGray
    }

    val directionIcon = when {
        analysis.isBuy -> Icons.AutoMirrored.Filled.TrendingUp
        analysis.isSell -> Icons.AutoMirrored.Filled.TrendingDown
        else -> Icons.AutoMirrored.Filled.TrendingFlat
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("analysis_card"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberCardBg)
                .border(1.dp, directionColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Pair & Timeframe + Direction & Confidence Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = analysis.pair.uppercase(),
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .background(CyberBlack, RoundedCornerShape(6.dp))
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = analysis.timeframe.uppercase(),
                                color = NeonGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    if (timestamp != null) {
                        val formattedDate = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(timestamp))
                        Text(
                            text = "Scanned $formattedDate",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Direction & Confidence Pill Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .background(directionColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(1.dp, directionColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = directionIcon,
                        contentDescription = "Direction",
                        tint = directionColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = analysis.direction.uppercase(),
                        color = directionColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${analysis.confidence}%",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Strategy Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                NeonGreen.copy(alpha = 0.12f),
                                CyberBlack
                            )
                        )
                    )
                    .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "STRATEGY EXECUTION",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = analysis.strategy,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Phase & Setup Grade Tag Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoTag(label = "PHASE", value = analysis.phase, color = AccentCyan)
                InfoTag(label = "GRADE", value = analysis.setupGrade, color = AccentGold)
                InfoTag(label = "MACRO", value = analysis.macroBias, color = directionColor)
            }

            // Parameters Grid (Callout Boxes for Entry, TP, SL, R:R)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ParameterBox(
                    label = "ENTRY",
                    value = analysis.entryPrice,
                    accentColor = Color(0xFF60A5FA),
                    modifier = Modifier.weight(1f)
                )
                ParameterBox(
                    label = "TAKE PROFIT",
                    value = analysis.takeProfit,
                    accentColor = BuyGreen,
                    modifier = Modifier.weight(1f)
                )
                ParameterBox(
                    label = "STOP LOSS",
                    value = analysis.stopLoss,
                    accentColor = SellRed,
                    modifier = Modifier.weight(1f)
                )
            }

            // Risk to Reward Sub-banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberBlack, RoundedCornerShape(8.dp))
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ESTIMATED RISK/REWARD",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = analysis.riskReward,
                    color = AccentGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Key Levels Section
            if (analysis.keyLevels.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MAJOR KEY LEVELS / S&R",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        analysis.keyLevels.take(3).forEach { level ->
                            Box(
                                modifier = Modifier
                                    .background(CyberBlack, RoundedCornerShape(6.dp))
                                    .border(1.dp, CyberCardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = level,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Confluences Section (Itemized Bulleted List)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "CONFIRMED CONFLUENCES",
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                analysis.confluences.forEach { confluence ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(5.dp)
                                .background(NeonGreen, CircleShape)
                        )
                        Text(
                            text = confluence,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Bottom Actions: Copy Setup + Delete (if in history)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        val setupText = """
                            Generator X Chart Analysis:
                            Pair: ${analysis.pair} (${analysis.timeframe})
                            Signal: ${analysis.direction} (${analysis.confidence}% Conf)
                            Strategy: ${analysis.strategy}
                            Entry: ${analysis.entryPrice}
                            TP: ${analysis.takeProfit}
                            SL: ${analysis.stopLoss}
                            R:R: ${analysis.riskReward}
                            Confluences: ${analysis.confluences.joinToString(", ")}
                        """.trimIndent()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Trade Setup", setupText))
                        Toast.makeText(context, "Trade Setup Copied to Clipboard", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.linearGradient(listOf(NeonGreen, NeonGreen))),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("copy_setup_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Setup",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Setup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_scan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Scan",
                            tint = SellRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JournalTradeCard(
    analysis: ChartAnalysis,
    onStatusChange: (String) -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val directionColor = when {
        analysis.isBuy -> com.example.ui.theme.WaveGreen
        analysis.isSell -> com.example.ui.theme.WaveRed
        else -> NeutralGray
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() }
            .testTag("journal_trade_card"),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B1322))
                .border(1.dp, com.example.ui.theme.WaveTagBorder, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Direction Badge, Pair, Setup Grade, Phase, Status Badge, and Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // SELL / BUY Pill
                    Box(
                        modifier = Modifier
                            .background(directionColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = analysis.direction.uppercase(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Pair Symbol
                    Text(
                        text = analysis.pair,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Grade Badge [A]
                    val gradeText = analysis.setupGrade.removePrefix("Grade ").trim()
                    Box(
                        modifier = Modifier
                            .background(com.example.ui.theme.WavePurpleBg, RoundedCornerShape(4.dp))
                            .border(1.dp, com.example.ui.theme.WavePurple.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = gradeText.ifBlank { "A" },
                            color = com.example.ui.theme.WavePurpleGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Phase Badge [Re-Distribution]
                    Box(
                        modifier = Modifier
                            .background(com.example.ui.theme.WavePurpleBg, RoundedCornerShape(4.dp))
                            .border(1.dp, com.example.ui.theme.WavePurple.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = analysis.phase,
                            color = com.example.ui.theme.WavePurpleGlow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }

                // Status Badge (OPEN, WIN, LOSS)
                val statusBg = when (analysis.status.uppercase()) {
                    "WIN" -> Color(0xFF0F391E)
                    "LOSS" -> Color(0xFF3B1214)
                    else -> Color(0xFF3B2508)
                }
                val statusTextColor = when (analysis.status.uppercase()) {
                    "WIN" -> com.example.ui.theme.WaveGreen
                    "LOSS" -> com.example.ui.theme.WaveRed
                    else -> com.example.ui.theme.WaveOrange
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(4.dp))
                        .border(1.dp, statusTextColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = analysis.status.uppercase(),
                        color = statusTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Row 2: Headline Thesis
            Text(
                text = analysis.headline.ifBlank { "${analysis.pair}: Institutional ${analysis.phase} Setup for ${analysis.direction} Continuation" },
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )

            // Row 3: Key Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                analysis.keyTags.take(3).forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(com.example.ui.theme.WaveTagBg, RoundedCornerShape(4.dp))
                            .border(1.dp, com.example.ui.theme.WaveTagBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Row 4: Metrics Row (Exec, Struct, POI, R:R)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF070C15), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exec: ${analysis.executionQuality}/100",
                    color = com.example.ui.theme.WavePurpleGlow,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "•",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "Struct: ${analysis.structuralConfidence}%",
                    color = AccentCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "•",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "POI: ${analysis.poiScore}%",
                    color = AccentGold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "•",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "R:R ${analysis.riskReward}",
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Row 5: Quick Outcome Status Actions & Detailed View
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status buttons: Open, WIN, LOSS
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val currentStatus = analysis.status.uppercase()
                    // Open button
                    val isOpenSelected = currentStatus == "OPEN"
                    Box(
                        modifier = Modifier
                            .background(
                                if (isOpenSelected) com.example.ui.theme.WaveOrange.copy(alpha = 0.25f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (isOpenSelected) com.example.ui.theme.WaveOrange else TextMuted.copy(alpha = 0.4f),
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onStatusChange("OPEN") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Open",
                            color = if (isOpenSelected) com.example.ui.theme.WaveOrange else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // WIN button
                    val isWinSelected = currentStatus == "WIN"
                    Box(
                        modifier = Modifier
                            .background(
                                if (isWinSelected) com.example.ui.theme.WaveGreen.copy(alpha = 0.25f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (isWinSelected) com.example.ui.theme.WaveGreen else TextMuted.copy(alpha = 0.4f),
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onStatusChange("WIN") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "WIN",
                            color = if (isWinSelected) com.example.ui.theme.WaveGreen else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // LOSS button
                    val isLossSelected = currentStatus == "LOSS"
                    Box(
                        modifier = Modifier
                            .background(
                                if (isLossSelected) com.example.ui.theme.WaveRed.copy(alpha = 0.25f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (isLossSelected) com.example.ui.theme.WaveRed else TextMuted.copy(alpha = 0.4f),
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onStatusChange("LOSS") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LOSS",
                            color = if (isLossSelected) com.example.ui.theme.WaveRed else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Right Actions: Details Button & Delete
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF131D2D), RoundedCornerShape(4.dp))
                            .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .clickable { onOpenDetails() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Details ▸",
                            color = AccentCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Trade",
                                tint = SellRed.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoTag(
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(CyberBlack, RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$label:",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ParameterBox(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(CyberBlack, RoundedCornerShape(8.dp))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}
