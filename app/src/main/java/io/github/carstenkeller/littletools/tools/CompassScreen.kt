package io.github.carstenkeller.littletools.tools

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.logic.ToolMath
import io.github.carstenkeller.littletools.sensors.KeepScreenOn
import io.github.carstenkeller.littletools.sensors.rememberSensor
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Kompass (magnetisch Nord), Gerät flach halten. */
@Composable
fun CompassScreen(onBack: () -> Unit) {
    KeepScreenOn()
    // Rotationsvektor = vom System fusioniert aus Magnetfeld, Beschleunigung, Gyroskop.
    // Ohne Gyroskop bieten manche Geräte nur TYPE_GEOMAGNETIC_ROTATION_VECTOR.
    val reading by rememberSensor(Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, smoothing = 0.5f)
    val dirs = stringArrayResource(R.array.cardinal_directions)

    ToolScaffold(Tool.COMPASS, onBack) { modifier ->
        val v = reading.values
        when {
            !reading.available -> InfoMessage(stringResource(R.string.sensor_missing), modifier)
            v == null -> InfoMessage(stringResource(R.string.waiting_for_sensor), modifier)
            else -> {
                val rot = FloatArray(9)
                // Nur die ersten 4 Werte verwenden; manche Geräte liefern 5 (Genauigkeit), das ist erlaubt.
                SensorManager.getRotationMatrixFromVector(rot, v.copyOf(min(v.size, 4)))
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rot, orientation)
                val heading = ToolMath.normalizeDeg(Math.toDegrees(orientation[0].toDouble()))
                val lowAccuracy = reading.accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW

                val onBg = MaterialTheme.colorScheme.onBackground
                val outline = MaterialTheme.colorScheme.outline
                val north = Color(0xFFD32F2F)
                val measurer = rememberTextMeasurer()
                val labelStyle = MaterialTheme.typography.titleLarge.copy(color = onBg)
                val northStyle = labelStyle.copy(color = north)
                val main = listOf(dirs[0], dirs[2], dirs[4], dirs[6])

                Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${heading.toInt()}°  ${dirs[ToolMath.cardinalIndex(heading)]}",
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Canvas(Modifier.fillMaxWidth().weight(1f)) {
                        val c = Offset(size.width / 2, size.height / 2)
                        val radius = min(size.width, size.height) / 2 * 0.85f
                        // Feste Marke oben = Blickrichtung des Geräts.
                        val tri = Path().apply {
                            moveTo(c.x, c.y - radius - 14.dp.toPx())
                            lineTo(c.x - 10.dp.toPx(), c.y - radius - 30.dp.toPx())
                            lineTo(c.x + 10.dp.toPx(), c.y - radius - 30.dp.toPx())
                            close()
                        }
                        drawPath(tri, onBg)
                        // Rose dreht sich gegen die Blickrichtung, damit N nach Norden zeigt.
                        rotate(-heading.toFloat(), c) {
                            drawCircle(outline, radius, c, style = Stroke(2.dp.toPx()))
                            for (deg in 0 until 360 step 10) {
                                val a = Math.toRadians(deg.toDouble())
                                val len = if (deg % 30 == 0) 18.dp.toPx() else 9.dp.toPx()
                                val sx = sin(a).toFloat(); val sy = -cos(a).toFloat()
                                drawLine(
                                    if (deg == 0) north else outline,
                                    Offset(c.x + sx * radius, c.y + sy * radius),
                                    Offset(c.x + sx * (radius - len), c.y + sy * (radius - len)),
                                    strokeWidth = (if (deg % 30 == 0) 3 else 1).dp.toPx(),
                                )
                            }
                            main.forEachIndexed { i, label ->
                                val a = Math.toRadians(i * 90.0)
                                val layout = measurer.measure(label, if (i == 0) northStyle else labelStyle)
                                val r = radius - 40.dp.toPx()
                                val p = Offset(c.x + sin(a).toFloat() * r, c.y - cos(a).toFloat() * r)
                                drawText(layout, topLeft = Offset(p.x - layout.size.width / 2f, p.y - layout.size.height / 2f))
                            }
                            // Nadel
                            val needle = Path().apply {
                                moveTo(c.x, c.y - radius * 0.55f)
                                lineTo(c.x - 12.dp.toPx(), c.y)
                                lineTo(c.x + 12.dp.toPx(), c.y)
                                close()
                            }
                            drawPath(needle, north)
                            val tail = Path().apply {
                                moveTo(c.x, c.y + radius * 0.55f)
                                lineTo(c.x - 12.dp.toPx(), c.y)
                                lineTo(c.x + 12.dp.toPx(), c.y)
                                close()
                            }
                            drawPath(tail, outline)
                        }
                    }
                    Text(
                        stringResource(if (lowAccuracy) R.string.compass_calibrate_hint else R.string.compass_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = if (lowAccuracy) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
