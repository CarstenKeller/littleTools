package io.github.carstenkeller.littletools.tools

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.logic.ToolMath
import io.github.carstenkeller.littletools.sensors.KeepScreenOn
import io.github.carstenkeller.littletools.sensors.rememberSensor
import kotlin.math.min

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

                Column(modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${heading.toInt()}°  ${dirs[ToolMath.cardinalIndex(heading)]}",
                        style = MaterialTheme.typography.displayMedium,
                    )
                    // A-02: realitätsnaher Kompass aus drei Bildebenen; nur die Rose dreht sich.
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Box(Modifier.aspectRatio(1f, matchHeightConstraintsFirst = true)) {
                            Image(painterResource(R.drawable.compass_housing), null, Modifier.fillMaxSize())
                            Image(
                                painterResource(R.drawable.compass_card), null,
                                Modifier.fillMaxSize().rotate(-heading.toFloat()),
                            )
                            Image(painterResource(R.drawable.glass_compass), null, Modifier.fillMaxSize())
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
