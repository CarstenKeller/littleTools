package io.github.carstenkeller.littletools

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.carstenkeller.littletools.settings.Prefs
import io.github.carstenkeller.littletools.settings.ThemeMode
import io.github.carstenkeller.littletools.tools.CompassScreen
import io.github.carstenkeller.littletools.tools.EdgeLevelScreen
import io.github.carstenkeller.littletools.tools.FlashlightScreen
import io.github.carstenkeller.littletools.tools.FlatLevelScreen
import io.github.carstenkeller.littletools.tools.HomeScreen
import io.github.carstenkeller.littletools.tools.PositionScreen
import io.github.carstenkeller.littletools.tools.RangefinderScreen
import io.github.carstenkeller.littletools.tools.RulerScreen
import io.github.carstenkeller.littletools.tools.SettingsScreen
import io.github.carstenkeller.littletools.tools.Tool
import io.github.carstenkeller.littletools.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = Prefs(this)
        setContent {
            var themeMode by remember { mutableStateOf(prefs.themeMode) }
            var gridColumns by remember { mutableIntStateOf(prefs.gridColumns) }
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            // D-07: Systemleisten passend zum gewählten Modus (Muster wie "Now in Android").
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(LIGHT_SCRIM, DARK_SCRIM) { darkTheme },
                )
                onDispose { }
            }
            AppTheme(darkTheme = darkTheme) {
                Surface(
                    // D-02: randlos, aber ohne Überlappung mit den Systemleisten
                    modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppRoot(
                        themeMode = themeMode,
                        onThemeMode = { themeMode = it; prefs.themeMode = it },
                        gridColumns = gridColumns,
                        onGridColumns = { gridColumns = it; prefs.gridColumns = it },
                    )
                }
            }
        }
    }

    private companion object {
        // Werte wie in androidx.activity EdgeToEdge (Standard-Scrims)
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}

@Composable
private fun AppRoot(
    themeMode: ThemeMode,
    onThemeMode: (ThemeMode) -> Unit,
    gridColumns: Int,
    onGridColumns: (Int) -> Unit,
) {
    // A-01: Startraster; ein Tool (oder die Einstellungen) füllt den Bildschirm, Zurück führt zum Raster.
    var current by rememberSaveable { mutableStateOf<Tool?>(null) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    val back = { current = null; showSettings = false }
    BackHandler(enabled = current != null || showSettings) { back() }

    if (showSettings) {
        SettingsScreen(themeMode, onThemeMode, gridColumns, onGridColumns, back)
        return
    }
    when (current) {
        null -> HomeScreen(gridColumns, onOpen = { current = it }, onSettings = { showSettings = true })
        Tool.COMPASS -> CompassScreen(back)
        Tool.EDGE_LEVEL -> EdgeLevelScreen(back)
        Tool.FLAT_LEVEL -> FlatLevelScreen(back)
        Tool.RULER -> RulerScreen(back)
        Tool.FLASHLIGHT -> FlashlightScreen(back)
        Tool.RANGEFINDER -> RangefinderScreen(back)
        Tool.POSITION -> PositionScreen(back)
    }
}
