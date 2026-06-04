import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    private val audioEqualizer = AudioEqualizer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Boot up the audio engine
        audioEqualizer.init()

        setContent {
            MaterialYouTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EqualizerScreen(audioEq = audioEqualizer)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent memory and audio session leaks
        audioEqualizer.release()
    }
}
