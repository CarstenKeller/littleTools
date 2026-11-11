package io.github.carstenkeller.littletools.tools

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.logic.ToolMath
import io.github.carstenkeller.littletools.sensors.rememberSensor
import io.github.carstenkeller.littletools.ui.components.IconActionButton
import java.util.Locale

private const val STANDARD_PRESSURE = 1013.25

private fun hasLocationPermission(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

/** Höhe über dem Meeresspiegel, falls das Gerät sie liefert (ab Android 14), sonst null. */
private fun mslAltitude(l: Location): Double? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && l.hasMslAltitude()) l.mslAltitudeMeters else null

@Composable
fun PositionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasLocationPermission(context)) }
    var asked by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        granted = result.values.any { it }
        asked = true
    }

    ToolScaffold(Tool.POSITION, onBack) { modifier ->
        if (!granted) {
            // D-05: erklären statt still scheitern
            InfoMessage(
                stringResource(if (asked) R.string.location_denied else R.string.location_rationale),
                modifier,
            ) {
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }) { Text(stringResource(R.string.grant_permission)) }
            }
        } else {
            PositionContent(modifier)
        }
    }
}

@SuppressLint("MissingPermission") // nur aufgerufen, wenn die Berechtigung erteilt ist
@Composable
private fun PositionContent(modifier: Modifier) {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }

    DisposableEffect(Unit) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { runCatching { lm.isProviderEnabled(it) }.getOrDefault(false) }
        location = providers.mapNotNull { runCatching { lm.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        // Alle Methoden überschreiben: auf Android 10 sind die übrigen noch nicht "default"
        // und würden sonst zur Laufzeit fehlen (AbstractMethodError).
        val listener = object : LocationListener {
            override fun onLocationChanged(l: Location) {
                val old = location
                // GPS bevorzugen; Netzwerk nur nehmen, wenn kein frischer GPS-Wert da ist.
                if (old == null || l.provider == LocationManager.GPS_PROVIDER || old.provider != LocationManager.GPS_PROVIDER ||
                    l.time - old.time > 10_000
                ) location = l
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        providers.forEach { runCatching { lm.requestLocationUpdates(it, 1000L, 0f, listener, Looper.getMainLooper()) } }
        onDispose { lm.removeUpdates(listener) }
    }

    val pressure by rememberSensor(Sensor.TYPE_PRESSURE, smoothing = 0.1f)
    // Bezugsdruck auf Meereshöhe; Standard 1013,25 hPa, per Abgleich mit GPS-Höhe änderbar (nicht gespeichert, wetterabhängig).
    var p0 by rememberSaveable { mutableStateOf(STANDARD_PRESSURE) }
    val loc = location
    val msl = loc?.let { mslAltitude(it) }
    val f = { fmt: String, v: Double -> String.format(Locale.getDefault(), fmt, v) }

    Column(
        modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // A-02: analoger Höhenmesser. Quelle: Luftdruck (falls vorhanden), sonst GPS.
        val baroAlt = pressure.values?.let { ToolMath.pressureToAltitude(p0, it[0].toDouble()) }
        val gpsAlt = msl ?: loc?.takeIf { it.hasAltitude() }?.altitude
        val shownAlt = baroAlt ?: gpsAlt
        Altimeter(shownAlt, Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally))
        Text(
            when {
                shownAlt == null -> stringResource(R.string.position_searching)
                baroAlt != null -> stringResource(R.string.altimeter_source_baro, f("%.0f", shownAlt))
                else -> stringResource(R.string.altimeter_source_gps, f("%.0f", shownAlt))
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Card {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.position_coordinates), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (loc != null) {
                        val label = stringResource(R.string.copy)
                        IconActionButton(Icons.Filled.ContentCopy, label, {
                            val text = String.format(Locale.ROOT, "%.6f, %.6f", loc.latitude, loc.longitude)
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("position", text))
                        })
                    }
                }
                if (loc == null) {
                    Text(stringResource(R.string.position_searching))
                } else {
                    Text(String.format(Locale.ROOT, "%.6f, %.6f", loc.latitude, loc.longitude), style = MaterialTheme.typography.headlineSmall)
                    Text("${ToolMath.toDms(loc.latitude, true)}  ${ToolMath.toDms(loc.longitude, false)}")
                    if (loc.hasAccuracy()) Text(stringResource(R.string.position_accuracy, f("%.0f", loc.accuracy.toDouble())))
                    Text(stringResource(R.string.position_source, loc.provider ?: "?"), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Card {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.altitude_gps), style = MaterialTheme.typography.titleMedium)
                when {
                    loc == null || !loc.hasAltitude() -> Text(stringResource(R.string.position_searching))
                    msl != null -> Text(stringResource(R.string.altitude_msl, f("%.0f", msl)), style = MaterialTheme.typography.headlineSmall)
                    else -> {
                        Text(stringResource(R.string.altitude_ellipsoid, f("%.0f", loc.altitude)), style = MaterialTheme.typography.headlineSmall)
                        Text(stringResource(R.string.altitude_ellipsoid_hint), style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (loc != null && loc.hasVerticalAccuracy()) {
                    Text(stringResource(R.string.position_accuracy, f("%.0f", loc.verticalAccuracyMeters.toDouble())), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Card {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.altitude_baro), style = MaterialTheme.typography.titleMedium)
                val pv = pressure.values
                when {
                    !pressure.available -> Text(stringResource(R.string.barometer_missing))
                    pv == null -> Text(stringResource(R.string.waiting_for_sensor))
                    else -> {
                        val p = pv[0].toDouble()
                        Text(stringResource(R.string.altitude_msl, f("%.0f", ToolMath.pressureToAltitude(p0, p))), style = MaterialTheme.typography.headlineSmall)
                        Text(stringResource(R.string.barometer_values, f("%.1f", p), f("%.1f", p0)))
                        Text(stringResource(R.string.barometer_hint), style = MaterialTheme.typography.bodySmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (msl != null) {
                                IconActionButton(Icons.Filled.GpsFixed, stringResource(R.string.barometer_from_gps), {
                                    p0 = ToolMath.seaLevelPressure(p, msl)
                                })
                            }
                            IconActionButton(Icons.Filled.RestartAlt, stringResource(R.string.reset), { p0 = STANDARD_PRESSURE })
                        }
                    }
                }
            }
        }
    }
}

/** Analoger Höhenmesser: langer Zeiger 1000 m je Umdrehung, kurzer Zeiger 10 000 m je Umdrehung. */
@Composable
private fun Altimeter(altitudeM: Double?, modifier: Modifier) {
    val a = altitudeM ?: 0.0
    val long = (((a % 1000.0) + 1000.0) % 1000.0 / 1000.0 * 360.0).toFloat()
    val short = (((a % 10000.0) + 10000.0) % 10000.0 / 10000.0 * 360.0).toFloat()
    Box(modifier.aspectRatio(1f)) {
        Image(painterResource(R.drawable.altimeter_dial), null, Modifier.fillMaxSize())
        if (altitudeM != null) {
            Image(painterResource(R.drawable.needle_short), null, Modifier.fillMaxSize().rotate(short))
            Image(painterResource(R.drawable.needle_long), null, Modifier.fillMaxSize().rotate(long))
        }
        Image(painterResource(R.drawable.glass_round), null, Modifier.fillMaxSize())
    }
}
