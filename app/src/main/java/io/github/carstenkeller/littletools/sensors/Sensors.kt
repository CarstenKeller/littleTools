package io.github.carstenkeller.littletools.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import io.github.carstenkeller.littletools.logic.lowPass

/** Ergebnis eines Sensors: [values] null, solange noch kein Wert kam; [available] false, wenn das Gerät ihn nicht hat. */
data class SensorReading(
    val values: FloatArray? = null,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    val available: Boolean = true,
)

/**
 * Meldet den Sensor [type] an, solange die Composable sichtbar ist, und glättet die Werte (Tiefpass).
 * [fallbackType] wird verwendet, wenn [type] fehlt (z. B. Beschleunigung statt Schwerkraft).
 */
@Composable
fun rememberSensor(type: Int, fallbackType: Int? = null, smoothing: Float = 0.15f): State<SensorReading> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(SensorReading()) }
    DisposableEffect(type, fallbackType) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(type) ?: fallbackType?.let { sm.getDefaultSensor(it) }
        if (sensor == null) {
            state.value = SensorReading(available = false)
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val old = state.value
                    state.value = old.copy(values = lowPass(event.values, old.values, smoothing))
                }

                override fun onAccuracyChanged(s: Sensor, accuracy: Int) {
                    state.value = state.value.copy(accuracy = accuracy)
                }
            }
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            onDispose { sm.unregisterListener(listener) }
        }
    }
    return state
}

/** Schwerkraft-Vektor (nach oben zeigend, siehe ToolMath); fällt auf den Beschleunigungssensor zurück. */
@Composable
fun rememberGravity(): State<SensorReading> =
    rememberSensor(Sensor.TYPE_GRAVITY, Sensor.TYPE_ACCELEROMETER)

/** Bildschirm bleibt an, solange die aufrufende Composable sichtbar ist. */
@Composable
fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}
