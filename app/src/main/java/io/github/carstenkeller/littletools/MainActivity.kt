package io.github.carstenkeller.littletools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import io.github.carstenkeller.littletools.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() zeichnet unter Status-/Navigationsleiste -
        // safeDrawingPadding() unten verhindert, dass Inhalte/Controls
        // damit überlappen (siehe references/compose-patterns.md #2).
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    // TODO: durch den eigentlichen App-Einstieg ersetzen.
                    Text("Hallo App")
                }
            }
        }
    }
}
