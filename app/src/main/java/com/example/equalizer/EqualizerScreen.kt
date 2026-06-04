package com.example.equalizer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Simple state-based navigation structure
sealed class AppScreen(val route: String, val icon: ImageVector) {
    object Equalizer : AppScreen("Equalizer", Icons.Default.Speaker)
    object Settings : AppScreen("Settings", Icons.Default.Settings)
    object About : AppScreen("About", Icons.Default.Info)
}

@Composable
fun MainScreenScaffold(audioEq: AudioEqualizer) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Equalizer) }

    val drawerItems = listOf(
        AppScreen.Equalizer,
        AppScreen.Settings,
        AppScreen.About
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header with app name
                Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(text = "Equalizer", style = MaterialTheme.typography.headlineMedium)
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                
                // Navigation items
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.route) },
                        label = { Text(text = item.route) },
                        selected = currentScreen == item,
                        onClick = {
                            currentScreen = item
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // Main Content Scaffold
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.route) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (currentScreen) {
                    AppScreen.Equalizer -> EqualizerScreen(audioEq = audioEq)
                    AppScreen.Settings -> SettingsScreenContent()
                    AppScreen.About -> AboutScreenContent()
                }
            }
        }
    }
}

// Main Equalizer Slider Screen
@Composable
fun EqualizerScreen(audioEq: AudioEqualizer) {
    if (audioEq.bands.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Equalizer not supported on this device/session.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            audioEq.bands.forEach { band ->
                BandSliderVertical(band = band, onLevelChange = { newLevel ->
                    audioEq.setBandLevel(band.index, newLevel)
                })
            }
        }
    }
}

// THIS FIXES THE SLIDER ISSUE
@Composable
fun BandSliderVertical(band: AudioEqualizer.Band, onLevelChange: (Short) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Frequency Label (e.g., 60 Hz) - moved closer to the slider
        Text(
            text = "${band.centerFreq / 1000} Hz",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        // THE FIX IS HERE: Explicitly dimension the Box and the Slider
        // Container defines the final visual vertical space (250dp)
        Box(
            modifier = Modifier
                .height(250.dp) // The intended vertical height
                .width(48.dp),  // Width per band
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = band.level.toFloat(),
                onValueChange = { onLevelChange(it.toInt().toShort()) },
                valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                modifier = Modifier
                    .width(250.dp) // <- This stretches the slider horizontally *before* rotation.
                    .graphicsLayer {
                        rotationZ = 270f
                    },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // Decibel Label (e.g., 0 dB) - moved closer to the slider
        Text(
            text = "${band.level / 100} dB",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ---------------------------------------------------------
// Placeholder and Content Screens for New Menu Sections
// ---------------------------------------------------------

@Composable
fun SettingsScreenContent() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Global Audio Effects", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        ListItem(
            headlineContent = { Text("Active Audio Session") },
            supportingContent = { Text("Session ID 0 (Global Mix)") }
        )
        ListItem(
            headlineContent = { Text("Presets") },
            supportingContent = { Text("Custom (Live Adjustment)") },
            trailingContent = { Switch(checked = true, onCheckedChange = {}) }
        )
        ListItem(
            headlineContent = { Text("Dynamic Theme (Material You)") },
            trailingContent = { Switch(checked = true, onCheckedChange = {}) }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "App-specific effects are in development.", style = MaterialTheme.typography.labelSmall)
    }
}

// THIS SECTION ADDS VERSIONS AND CONTRIBUTORS
@Composable
fun AboutScreenContent() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Equalizer", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "The simplest Material You equalizer for Android.", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Versions Section
        Text(text = "Versions & Compatibility", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        AboutItem("App Version", "1.0 Debug Build")
        AboutItem("Build Tools", "Gradle 8.5 / JDK 17")
        AboutItem("Android Compatibility", "Android 8.0 (API 26) or newer")
        AboutItem("Target SDK", "Android 14 (API 34)")
        AboutItem("System Integration", "Standard Audio Effect API (Session 0)")

        Spacer(modifier = Modifier.height(24.dp))

        // Contributors Section
        Text(text = "Credits", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        AboutItem("Main Developer", "AarushChaurasiya (Lead)")
        AboutItem("Architect & Guide", "The AI Assistant")
        AboutItem("Core Libraries", "Jetpack Compose / Material 3")

    }
}

@Composable
fun AboutItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$label:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
