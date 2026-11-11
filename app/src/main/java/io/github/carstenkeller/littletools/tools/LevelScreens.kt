package io.github.carstenkeller.littletools.tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import io.github.carstenkeller.littletools.ui.art.ArtGeometry as G
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.logic.ToolMath
import io.github.carstenkeller.littletools.sensors.KeepScreenOn
import io.github.carstenkeller.littletools.sensors.rememberGravity
import io.github.carstenkeller.littletools.settings.Prefs
import io.github.carstenkeller.littletools.ui.components.IconActionButton
import java.util.Locale
import kotlin.math.abs
import kotlin.math.hypot

/** Grün, wenn innerhalb dieser Abweichung (Grad). */
private const val LEVEL_OK_DEG = 0.5

private fun fmtDeg(d: Double) = String.format(Locale.getDefault(), "%.1f°", d)

@Composable
private fun levelColor(ok: Boolean): Color =
    if (ok) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary

/** Wasserwaage: Gerät steht auf der langen Kante, gemessen wird die Neigung der Längsachse. */
@Composable
fun EdgeLevelScreen(onBack: () -> Unit) {
    KeepScreenOn()
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    var offset by remember { mutableFloatStateOf(prefs.getFloat("edge_offset", 0f)) }
    val reading by rememberGravity()

    ToolScaffold(Tool.EDGE_LEVEL, onBack, actions = {
        val v = reading.values
        IconActionButton(Icons.Filled.Tune, stringResource(R.string.level_calibrate), {
            if (v != null) {
                offset = ToolMath.edgeTilt(v[0], v[1], v[2]).toFloat(); prefs.putFloat("edge_offset", offset)
            }
        })
        IconActionButton(Icons.Filled.RestartAlt, stringResource(R.string.reset), {
            offset = 0f; prefs.putFloat("edge_offset", 0f)
        })
    }) { modifier ->
        val v = reading.values
        when {
            !reading.available -> InfoMessage(stringResource(R.string.sensor_missing), modifier)
            v == null -> InfoMessage(stringResource(R.string.waiting_for_sensor), modifier)
            else -> {
                val onEdge = ToolMath.isOnLongEdge(v[0], v[1], v[2])
                val angle = ToolMath.edgeTilt(v[0], v[1], v[2]) - offset
                val ok = onEdge && abs(angle) <= LEVEL_OK_DEG
                // Text so drehen, dass er bei aufgestelltem Gerät aufrecht steht.
                val textRotation = if (ToolMath.edgeUpSide(v[0]) > 0) 90f else -90f
                val bubble = levelColor(ok)
                Box(modifier.padding(16.dp)) {
                    // A-02: realitätsnahe Wasserwaage; die Blase wandert zur höheren Seite.
                    BoxWithConstraints(
                        Modifier.fillMaxHeight().aspectRatio(G.EDGE_W / G.EDGE_H).align(Alignment.CenterStart),
                    ) {
                        val u = maxHeight / G.EDGE_H // dp je Bild-Einheit
                        val visible = G.EDGE_VIAL_W * 0.9f
                        val img = visible / G.BUBBLE_FILL
                        val mid = (G.EDGE_VIAL_TOP + G.EDGE_VIAL_BOTTOM) / 2
                        val travel = (G.EDGE_VIAL_BOTTOM - G.EDGE_VIAL_TOP) / 2 - visible / 2
                        val maxDeg = 5.0
                        // Oberkante höher (angle > 0) -> Blase nach oben.
                        val cy = mid - (angle.coerceIn(-maxDeg, maxDeg) / maxDeg).toFloat() * travel
                        Image(painterResource(R.drawable.edge_level_bar), null, Modifier.fillMaxSize())
                        Image(
                            painterResource(R.drawable.bubble), null,
                            Modifier.offset(u * (G.EDGE_VIAL_X - img / 2), u * (cy - img / 2)).size(u * img),
                        )
                        Image(painterResource(R.drawable.edge_level_glass), null, Modifier.fillMaxSize())
                    }
                    Column(
                        Modifier.align(Alignment.Center).padding(start = 140.dp).rotate(textRotation),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (onEdge) {
                            Text(fmtDeg(abs(angle)), style = MaterialTheme.typography.displayLarge, color = bubble)
                        } else {
                            Text(stringResource(R.string.edge_level_hint), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

/** Libelle: Gerät liegt flach, Neigung in beiden Achsen. */
@Composable
fun FlatLevelScreen(onBack: () -> Unit) {
    KeepScreenOn()
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    var offX by remember { mutableFloatStateOf(prefs.getFloat("flat_offset_x", 0f)) }
    var offY by remember { mutableFloatStateOf(prefs.getFloat("flat_offset_y", 0f)) }
    val reading by rememberGravity()

    ToolScaffold(Tool.FLAT_LEVEL, onBack, actions = {
        val v = reading.values
        IconActionButton(Icons.Filled.Tune, stringResource(R.string.level_calibrate), {
            if (v != null) {
                val (x, y) = ToolMath.flatTilt(v[0], v[1], v[2])
                offX = x.toFloat(); offY = y.toFloat()
                prefs.putFloat("flat_offset_x", offX); prefs.putFloat("flat_offset_y", offY)
            }
        })
        IconActionButton(Icons.Filled.RestartAlt, stringResource(R.string.reset), {
            offX = 0f; offY = 0f
            prefs.putFloat("flat_offset_x", 0f); prefs.putFloat("flat_offset_y", 0f)
        })
    }) { modifier ->
        val v = reading.values
        when {
            !reading.available -> InfoMessage(stringResource(R.string.sensor_missing), modifier)
            v == null -> InfoMessage(stringResource(R.string.waiting_for_sensor), modifier)
            else -> {
                val (rx, ry) = ToolMath.flatTilt(v[0], v[1], v[2])
                val x = rx - offX
                val y = ry - offY
                val ok = hypot(x, y) <= LEVEL_OK_DEG
                val bubble = levelColor(ok)
                Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        // A-02: realitätsnahe Dosenlibelle aus Bildebenen.
                        BoxWithConstraints(Modifier.aspectRatio(1f, matchHeightConstraintsFirst = true)) {
                            val u = maxWidth / G.FLAT_SIZE
                            val c = G.FLAT_SIZE / 2
                            val maxR = G.FLAT_VIAL_R - G.FLAT_BUBBLE_R
                            val maxDeg = 10.0
                            var dx = (x / maxDeg).toFloat() * maxR
                            var dy = (-y / maxDeg).toFloat() * maxR // Oberkante höher -> Blase nach oben
                            val d = hypot(dx, dy)
                            if (d > maxR) { dx *= maxR / d; dy *= maxR / d }
                            val img = 2 * G.FLAT_BUBBLE_R / G.BUBBLE_FILL
                            Image(painterResource(R.drawable.flat_level_base), null, Modifier.fillMaxSize())
                            Image(
                                painterResource(R.drawable.bubble), null,
                                Modifier.offset(u * (c + dx - img / 2), u * (c + dy - img / 2)).size(u * img),
                            )
                            Image(painterResource(R.drawable.flat_level_glass), null, Modifier.fillMaxSize())
                        }
                    }
                    Text(
                        "↔ ${fmtDeg(abs(x))}    ↕ ${fmtDeg(abs(y))}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = bubble,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }
    }
}
