package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GeminiVisionService
import com.example.ui.MainViewModel
import com.example.ui.components.AnimatedCyberBackground
import com.example.ui.components.AnimatedStarrySkyBackground
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BuyGreen
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
import com.example.ui.theme.WaveTagBorder
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val apiKeyInput by viewModel.apiKeyInput.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val customModelInput by viewModel.customModelInput.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val backgroundMode by viewModel.backgroundMode.collectAsState()
    val apiKeySavedToast by viewModel.apiKeySavedToast.collectAsState()
    val isTestingConnection by viewModel.isTestingConnection.collectAsState()
    val connectionTestResult by viewModel.connectionTestResult.collectAsState()

    val isKeyConfigured = viewModel.repository.hasApiKey()

    var passwordVisible by remember { mutableStateOf(false) }
    var showCustomModelField by remember { mutableStateOf(customModelInput.isNotBlank() && !GeminiVisionService.FREE_TIER_MODELS.contains(selectedModel)) }
    val scrollState = rememberScrollState()

    // Handle feedback toast
    LaunchedEffect(apiKeySavedToast) {
        if (apiKeySavedToast) {
            Toast.makeText(context, "Settings & API Key Saved Successfully", Toast.LENGTH_SHORT).show()
            viewModel.dismissToast()
        }
    }

    @Composable
    fun SettingsBackgroundContainer(content: @Composable () -> Unit) {
        when (backgroundMode) {
            "CYBER" -> AnimatedCyberBackground(modifier = modifier, content = content)
            else -> AnimatedStarrySkyBackground(modifier = modifier, content = content)
        }
    }

    SettingsBackgroundContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Settings Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CONFIGURATION",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Flexible API & Model Parameters",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Key Status Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .background(
                            if (isKeyConfigured) WaveGreen.copy(alpha = 0.15f) else WaveRed.copy(alpha = 0.15f),
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            1.dp,
                            if (isKeyConfigured) WaveGreen else WaveRed,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = if (isKeyConfigured) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isKeyConfigured) WaveGreen else WaveRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isKeyConfigured) "ACTIVE" else "KEY MISSING",
                        color = if (isKeyConfigured) WaveGreen else WaveRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Section 1: API Key Input Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF091424).copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                    .border(1.dp, WaveTagBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = SkyBlueAccent, modifier = Modifier.size(18.dp))
                        Text(
                            text = "GEMINI API KEY",
                            color = SkyBlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Stored locally on your device. Never shared or transmitted to 3rd parties.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { viewModel.updateApiKeyInput(it) },
                        placeholder = { Text("AIzaSy...", color = TextMuted) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            viewModel.saveApiKey()
                        }),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Visibility",
                                    tint = TextSecondary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gemini_api_key_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF060D17),
                            unfocusedContainerColor = Color(0xFF060D17),
                            focusedBorderColor = SkyBlueAccent,
                            unfocusedBorderColor = WaveTagBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.saveApiKey()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyBlueAccent,
                                contentColor = CyberBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("save_api_key_button")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "SAVE KEY", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.testConnection()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WavePurpleGlow),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(WavePurple)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            if (isTestingConnection) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = WavePurpleGlow, strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "PING TEST", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                        }
                    }

                    if (connectionTestResult != null) {
                        val isSuccess = connectionTestResult!!.startsWith("Success")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSuccess) WaveGreen.copy(alpha = 0.15f) else WaveRed.copy(alpha = 0.15f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSuccess) WaveGreen.copy(alpha = 0.5f) else WaveRed.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = connectionTestResult!!,
                                color = if (isSuccess) WaveGreen else WaveRed,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Section 2: Flexible Model Selection (Free Tier Only)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF091424).copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                    .border(1.dp, WaveTagBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = WavePurpleGlow, modifier = Modifier.size(18.dp))
                        Text(
                            text = "ACTIVE VISION MODEL",
                            color = WavePurpleGlow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Select among verified free-tier models with vision support, or specify a custom model name.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    // Free Tier Model Options
                    GeminiVisionService.FREE_TIER_MODELS.forEach { modelName ->
                        val isSelected = selectedModel == modelName && !showCustomModelField
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) WavePurple.copy(alpha = 0.2f) else Color(0xFF060D17),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) WavePurple else WaveTagBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    showCustomModelField = false
                                    viewModel.selectModel(modelName)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = modelName,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (modelName == GeminiVisionService.DEFAULT_MODEL) {
                                    Text(
                                        text = "Recommended • Free tier • Fast vision",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .background(WaveGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "FREE TIER", color = WaveGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Custom Model Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showCustomModelField = !showCustomModelField
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (showCustomModelField) "▼ Custom Model ID" else "▶ Use Custom Model ID",
                            color = AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (showCustomModelField) {
                        OutlinedTextField(
                            value = customModelInput,
                            onValueChange = { viewModel.updateCustomModelInput(it) },
                            placeholder = { Text("e.g. gemini-2.5-pro", color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF060D17),
                                unfocusedContainerColor = Color(0xFF060D17),
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = WaveTagBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            }

            // Section 3: AI Inference Temperature Slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF091424).copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                    .border(1.dp, WaveTagBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = AccentGold, modifier = Modifier.size(18.dp))
                            Text(
                                text = "TEMPERATURE",
                                color = AccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = String.format("%.2f", temperature),
                            color = AccentGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Lower values (0.1 - 0.3) provide strict institutional rule adherence and consistent price level targeting.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Slider(
                        value = temperature,
                        onValueChange = { viewModel.updateTemperature(it) },
                        valueRange = 0.0f..1.0f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentGold,
                            activeTrackColor = AccentGold,
                            inactiveTrackColor = Color(0xFF060D17)
                        )
                    )
                }
            }

            // Section 4: Background Style Mode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF091424).copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                    .border(1.dp, WaveTagBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.DisplaySettings, contentDescription = null, tint = SkyBlueAccent, modifier = Modifier.size(18.dp))
                        Text(
                            text = "ANIMATED BACKGROUND",
                            color = SkyBlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    listOf(
                        "AUTO" to "Auto (Cyber Visor on Scan, Starry Sky on Journal & Settings)",
                        "CYBER" to "Cyberpunk Visor on all screens",
                        "STARRY" to "Dark Sky Blue Moving Stars on all screens"
                    ).forEach { (modeKey, label) ->
                        val isSelected = backgroundMode == modeKey
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) SkyBlueAccent.copy(alpha = 0.15f) else Color(0xFF060D17),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) SkyBlueAccent else WaveTagBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setBackgroundMode(modeKey) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isSelected) "●" else "○",
                                color = if (isSelected) SkyBlueAccent else TextMuted
                            )
                            Text(
                                text = label,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Section 5: Free Tier Link
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(WaveTagBorder)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Get Free Gemini Key (No Credit Card Required)", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
