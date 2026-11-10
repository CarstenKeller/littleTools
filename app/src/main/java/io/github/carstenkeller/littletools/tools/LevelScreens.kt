package io.github.carstenkeller.littletools.tools

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.min

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
                val tubeColor = MaterialTheme.colorScheme.outline
                val bubble = levelColor(ok)
                Box(modifier.padding(16.dp)) {
                    // Röhre entlang der Längsachse (= Bildschirmhöhe), Blase wandert zur höheren Seite.
                    Canvas(Modifier.fillMaxHeight().width(72.dp).align(Alignment.CenterStart)) {
                        val w = size.width
                        val h = size.height
                        drawRoundRect(tubeColor, size = Size(w, h), cornerRadius = CornerRadius(w / 2), style = Stroke(3.dp.toPx()))
                        val maxDeg = 10.0
                        val r = w * 0.38f
                        val travel = h / 2 - w / 2
                        val pos = (angle.coerceIn(-maxDeg, maxDeg) / maxDeg).toFloat() * travel
                        // Oberkante höher (angle > 0) -> Blase nach oben (Bildschirm-y kleiner).
                        drawCircle(bubble, radius = r, center = Offset(w / 2, h / 2 - pos))
                        val mark = r + 4.dp.toPx()
                        drawLine(tubeColor, Offset(0f, h / 2 - mark), Offset(w, h / 2 - mark), 2.dp.toPx())
                        drawLine(tubeColor, Offset(0f, h / 2 + mark), Offset(w, h / 2 + mark), 2.dp.toPx())
                    }
                    Column(
                        Modifier.align(Alignment.Center).padding(start = 72.dp).rotate(textRotation),
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
                val ringColor = MaterialTheme.colorScheme.outline
                val bubble = levelColor(ok)
                Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Canvas(Modifier.fillMaxWidth().weight(1f)) {
                        val c = Offset(size.width / 2, size.height / 2)
                        val radius = min(size.width, size.height) / 2 * 0.9f
                        drawCircle(ringColor, radius, c, style = Stroke(3.dp.toPx()))
                        drawCircle(ringColor, radius / 3, c, style = Stroke(1.dp.toPx()))
                        drawLine(ringColor, Offset(c.x - radius, c.y), Offset(c.x + radius, c.y), 1.dp.toPx())
                        drawLine(ringColor, Offset(c.x, c.y - radius), Offset(c.x, c.y + radius), 1.dp.toPx())
                        val maxDeg = 15.0
                        val br = radius * 0.15f
                        var dx = (x / maxDeg).toFloat() * (radius - br)
                        var dy = (-y / maxDeg).toFloat() * (radius - br) // Oberkante höher -> Blase nach oben
                        val d = hypot(dx, dy)
                        if (d > radius - br) { dx *= (radius - br) / d; dy *= (radius - br) / d }
                        drawCircle(bubble, br, Offset(c.x + dx, c.y + dy))
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
