package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SampleCharts
import com.example.ui.MainViewModel
import com.example.ui.components.AnalysisCard
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.AnimatedStarrySkyBackground
import com.example.ui.components.ScannerTerminal
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.SellRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ScanScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val currentAnalysis by viewModel.currentAnalysis.collectAsState()
    val scanError by viewModel.scanError.collectAsState()
    val showMissingKeyDialog by viewModel.showMissingKeyDialog.collectAsState()

    // Photo Picker contract for gallery image selection
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onImageSelected(uri)
            }
        }
    )

    val scrollState = rememberScrollState()

    // Glowing animation for the CTA button
    val infiniteTransition = rememberInfiniteTransition(label = "cta_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.99f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val backgroundMode by viewModel.backgroundMode.collectAsState()

    @Composable
    fun BackgroundContainer(content: @Composable () -> Unit) {
        when (backgroundMode) {
            "STARRY" -> AnimatedStarrySkyBackground(modifier = modifier, content = content)
            else -> AnimatedCyberBackground(modifier = modifier, content = content)
        }
    }

    BackgroundContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Top Cyber Brand Banner (Generator X / AI Smart Intelligence)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(CyberCardBg, CircleShape)
                    .border(1.5.dp, NeonGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Generator X Logo",
                    tint = NeonGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "GENERATOR X",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Text(
                text = "SMC & LIQUIDITY VISION MATRIX",
                color = NeonGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp
            )
        }

        // Quick Pair Selector Bar
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "FAST TEST WITH REAL MARKET BENCHMARKS:",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SampleCharts.samples) { sample ->
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.loadSampleChart(sample.id) }
                            .background(CyberCardBg, RoundedCornerShape(8.dp))
                            .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = "${sample.pair} (${sample.timeframe})",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = sample.type,
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        // Image Preview & Upload Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CyberCardBg)
                .border(1.dp, if (selectedBitmap != null) NeonGreen else CyberCardBorder, RoundedCornerShape(16.dp))
        ) {
            if (selectedBitmap != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberBlack)
                    ) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "Selected Chart",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )

                        // Remove Image Button
                        IconButton(
                            onClick = { viewModel.clearSelectedImage() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Re-select actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHART READY FOR SCAN",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        TextButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Change Screenshot", color = AccentCyan, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                // Empty Image Upload Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(CyberBlack, CircleShape)
                            .border(1.dp, NeonGreen.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Chart",
                            tint = NeonGreen,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ADD CHART SCREENSHOT",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "JPG or PNG from your device gallery or photo library",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = CyberBlack
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("upload_screenshot_button")
                        ) {
                            Text(text = "OPEN GALLERY", fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }

                        OutlinedButton(
                            onClick = { viewModel.loadSampleChart("xauusd_m5") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = Brush.linearGradient(listOf(CyberCardBorder, CyberCardBorder)))
                        ) {
                            Text(text = "LOAD SAMPLE", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // "SCAN CHART" CTA Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = { viewModel.scanChart() },
                enabled = !isScanning && selectedBitmap != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    contentColor = CyberBlack,
                    disabledContainerColor = Color(0xFF1B2332),
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .scale(if (!isScanning && selectedBitmap != null) pulseScale else 1f)
                    .testTag("scan_chart_cta_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isScanning) "ANALYZING MARKET STRUCTURE..." else "SCAN CHART WITH GEMINI VISION",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Live Scanning Terminal Log (dynamic checklist animation)
        AnimatedVisibility(
            visible = isScanning,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScannerTerminal(
                isScanning = isScanning,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Error message banner if any
        if (scanError != null && !isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(SellRed.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(1.dp, SellRed, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = SellRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ANALYSIS INCOMPLETE",
                            color = SellRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = scanError ?: "An unexpected error occurred.",
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Output Result: Dynamic Analysis Card
        if (currentAnalysis != null && !isScanning) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE AI APPRAISAL RESULT",
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(12.dp))
                        Text(text = "SAVED TO VAULT", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                AnalysisCard(
                    analysis = currentAnalysis!!,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }
}

    // Modal Prompt: API Key Missing (Required per spec #2)
    if (showMissingKeyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMissingKeyDialog() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = NeonGreen)
                    Text(
                        text = "Gemini API Key Required",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "To scan and analyze charts with the Gemini Vision API, please enter your free Gemini API key in the Settings screen.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "The app uses free-tier model (gemini-2.5-flash) with zero billing required.",
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissMissingKeyDialog()
                        viewModel.selectTab(2) // Redirect to Settings screen
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = CyberBlack
                    ),
                    modifier = Modifier.testTag("redirect_to_settings_button")
                ) {
                    Text("GO TO SETTINGS", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissMissingKeyDialog() }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CyberCardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
