package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.TickerBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ChartScannerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ChartScannerApp(viewModel: MainViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TickerBar()
        },
        bottomBar = {
            CyberBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = CyberBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(viewModel = viewModel)
                    1 -> ScanScreen(viewModel = viewModel)
                    2 -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CyberBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberBlack)
            .drawBehind {
                drawLine(
                    color = CyberCardBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Journal
            BottomNavItem(
                label = "JOURNAL",
                icon = Icons.Default.History,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                testTag = "nav_home_tab"
            )

            // Tab 1: Scan (Center Glowing Button)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onTabSelected(1) }
                    .testTag("nav_scan_tab")
                    .background(if (selectedTab == 1) NeonGreen else CyberCardBg, CircleShape)
                    .border(
                        2.dp,
                        NeonGreen,
                        CircleShape
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Scan",
                    tint = if (selectedTab == 1) CyberBlack else NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Tab 2: Settings
            BottomNavItem(
                label = "SETTINGS",
                icon = Icons.Default.Settings,
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                testTag = "nav_settings_tab"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) NeonGreen else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = if (isSelected) NeonGreen else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
