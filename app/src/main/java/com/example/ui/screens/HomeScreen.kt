package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.ScanEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AnalysisCard
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.AnimatedStarrySkyBackground
import com.example.ui.components.JournalTradeCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SellRed
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WaveGreen
import com.example.ui.theme.WaveOrange
import com.example.ui.theme.WavePurple
import com.example.ui.theme.WavePurpleGlow
import com.example.ui.theme.WaveRed
import com.example.ui.theme.WaveTagBg
import com.example.ui.theme.WaveTagBorder

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val historyScans by viewModel.historyScans.collectAsState()
    val rawScans by viewModel.rawAllScans.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val directionFilter by viewModel.directionFilter.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var detailScanToShow by remember { mutableStateOf<ScanEntity?>(null) }

    val filterOptions = listOf("ALL", "BUY", "SELL", "WAIT")

    // Stats calculations for Blue Wave Journal (X SCANS | Y WINS | Z LOSSES | W% WIN RATE)
    val totalScans = rawScans.size
    val totalWins = rawScans.count { it.status.equals("WIN", ignoreCase = true) }
    val totalLosses = rawScans.count { it.status.equals("LOSS", ignoreCase = true) }
    val decidedTrades = totalWins + totalLosses
    val winRatePercent = if (decidedTrades > 0) ((totalWins.toDouble() / decidedTrades) * 100).toInt() else 0

    val backgroundMode by viewModel.backgroundMode.collectAsState()

    @Composable
    fun HomeBackgroundContainer(content: @Composable () -> Unit) {
        when (backgroundMode) {
            "CYBER" -> AnimatedCyberBackground(modifier = modifier, content = content)
            else -> AnimatedStarrySkyBackground(modifier = modifier, content = content)
        }
    }

    HomeBackgroundContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top Header: "Journal" & Subtitle + Clear Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Journal",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "Every scan you save — with outcomes & full analysis.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                if (historyScans.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear History",
                            tint = WaveRed.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Stats Strip (Blue Wave Journal Reference: X SCANS | Y WINS | Z LOSSES | W% WIN RATE)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFF09121F).copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                    .border(1.dp, WaveTagBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$totalScans SCANS",
                        color = SkyBlueAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(text = "|", color = TextMuted, fontSize = 11.sp)
                    Text(
                        text = "$totalWins WINS",
                        color = WaveGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(text = "|", color = TextMuted, fontSize = 11.sp)
                    Text(
                        text = "$totalLosses LOSSES",
                        color = WaveRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(text = "|", color = TextMuted, fontSize = 11.sp)
                    Text(
                        text = "$winRatePercent% WIN RATE",
                        color = WaveOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar ("Search pair, title...")
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search pair, title...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = SkyBlueAccent, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("search_scans_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF091424).copy(alpha = 0.85f),
                    unfocusedContainerColor = Color(0xFF091424).copy(alpha = 0.85f),
                    focusedBorderColor = SkyBlueAccent,
                    unfocusedBorderColor = WaveTagBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Pills (ALL, BUY, SELL, WAIT)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = directionFilter.equals(filter, ignoreCase = true)
                    val activeColor = when (filter) {
                        "BUY" -> WaveGreen
                        "SELL" -> WaveRed
                        "WAIT" -> WaveOrange
                        else -> SkyBlueAccent
                    }
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.setDirectionFilter(filter) }
                            .background(
                                if (isSelected) activeColor.copy(alpha = 0.2f) else Color(0xFF091424).copy(alpha = 0.8f),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) activeColor else WaveTagBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) activeColor else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trade Journal Scans List
            if (historyScans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFF0B1B32), CircleShape)
                                .border(1.dp, SkyBlueAccent.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "No Scans",
                                tint = SkyBlueAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "NO JOURNAL TRADES FOUND",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = if (searchQuery.isNotBlank()) "No scans match your query \"$searchQuery\"." else "Upload your chart screenshots to track setups, POIs, and trade outcomes.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { viewModel.selectTab(1) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyBlueAccent,
                                contentColor = CyberBlack
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("launch_scanner_button")
                        ) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = "SCAN A CHART", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyScans, key = { it.id }) { scan ->
                        val analysis = scan.toChartAnalysis()
                        JournalTradeCard(
                            analysis = analysis,
                            onStatusChange = { newStatus ->
                                viewModel.updateScanStatus(scan.id, newStatus)
                            },
                            onOpenDetails = {
                                detailScanToShow = scan
                            },
                            onDelete = { viewModel.deleteScan(scan) }
                        )
                    }
                }
            }
        }
    }

    // Detailed Breakdown Dialog when tapping "Details ▸"
    if (detailScanToShow != null) {
        val scan = detailScanToShow!!
        val analysis = scan.toChartAnalysis()
        Dialog(
            onDismissRequest = { detailScanToShow = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF081220), RoundedCornerShape(16.dp))
                        .border(1.dp, WavePurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DETAILED SMC BREAKDOWN",
                            color = WavePurpleGlow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(onClick = { detailScanToShow = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AnalysisCard(
                        analysis = analysis,
                        timestamp = scan.timestamp,
                        onDelete = {
                            viewModel.deleteScan(scan)
                            detailScanToShow = null
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "Clear Journal?",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all saved trade records and outcomes from your device.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WaveRed)
                ) {
                    Text("Clear All", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF091424)
        )
    }
}

