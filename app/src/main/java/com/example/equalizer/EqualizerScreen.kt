import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(audioEq: AudioEqualizer) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Material You Equalizer") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (audioEq.bands.isEmpty()) {
                Text(
                    text = "Equalizer not supported on this device/session.",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    audioEq.bands.forEach { band ->
                        BandSlider(band = band, onLevelChange = { newLevel ->
                            audioEq.setBandLevel(band.index, newLevel)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun BandSlider(band: AudioEqualizer.Band, onLevelChange: (Short) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        // Display Decibels (dB)
        Text(
            text = "${band.level / 100} dB",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Vertical Slider implementation
        Box(
            modifier = Modifier
                .height(250.dp)
                .width(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = band.level.toFloat(),
                onValueChange = { onLevelChange(it.toInt().toShort()) },
                valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                modifier = Modifier
                    .width(250.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Display Frequency (Hz)
        Text(
            text = "${band.centerFreq / 1000} Hz",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
