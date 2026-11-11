package io.github.carstenkeller.littletools.tools

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.logic.ToolMath
import io.github.carstenkeller.littletools.sensors.KeepScreenOn
import io.github.carstenkeller.littletools.sensors.rememberGravity
import io.github.carstenkeller.littletools.settings.Prefs
import java.util.Locale

private const val KEY_HEIGHT = "rangefinder_height"
private val RED = Color(0xFFFF3D3D)

/**
 * Entfernungsmesser per Neigungswinkel: Gerät in Haltehöhe h, Fadenkreuz auf den Fußpunkt
 * des Objekts richten, Entfernung = h · tan(Winkel zur Senkrechten). Nur auf ebenem Boden sinnvoll.
 */
@Composable
fun RangefinderScreen(onBack: () -> Unit) {
    KeepScreenOn()
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    var height by remember { mutableFloatStateOf(prefs.getFloat(KEY_HEIGHT, 1.5f)) }
    var cameraGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var cameraAsked by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        cameraGranted = it; cameraAsked = true
    }
    val reading by rememberGravity()

    ToolScaffold(Tool.RANGEFINDER, onBack) { modifier ->
        Box(modifier) {
            if (cameraGranted) {
                CameraPreview(Modifier.fillMaxSize())
            } else {
                // D-05: ohne Kamera funktioniert die Messung trotzdem, nur ohne Bild.
                Column(Modifier.align(Alignment.TopCenter).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(if (cameraAsked) R.string.camera_denied else R.string.camera_rationale))
                    TextButton(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            }
            // A-02: Anzeige wie in einem Laser-Entfernungsmesser: rote Eckklammern, Punkt in der Bildmitte
            // (Bildmitte ≈ optische Achse der Kamera; Annahme, s. plan.md), Abdunklung am Rand.
            Canvas(Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height / 2)
                drawRect(
                    Brush.radialGradient(
                        0.55f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.75f),
                        center = c, radius = size.maxDimension * 0.6f,
                    ),
                )
                val a = 34.dp.toPx(); val l = 16.dp.toPx(); val w = 3.dp.toPx()
                for (sx in listOf(-1f, 1f)) for (sy in listOf(-1f, 1f)) {
                    val corner = Offset(c.x + sx * a, c.y + sy * a)
                    drawLine(RED, corner, Offset(corner.x - sx * l, corner.y), w)
                    drawLine(RED, corner, Offset(corner.x, corner.y - sy * l), w)
                }
                drawCircle(RED, 3.dp.toPx(), c)
            }
            val v = reading.values
            Column(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when {
                    !reading.available -> Text(stringResource(R.string.sensor_missing))
                    v == null -> Text(stringResource(R.string.waiting_for_sensor))
                    else -> {
                        val angle = ToolMath.cameraAngleFromVertical(v[0], v[1], v[2])
                        val d = ToolMath.distanceOnGround(height.toDouble(), angle)
                        // Digitalanzeige im Stil eines Messgeräts
                        Text(
                            if (d == null) stringResource(R.string.rangefinder_too_flat)
                            else String.format(Locale.getDefault(), "%.1f m", d),
                            style = MaterialTheme.typography.displayMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                            color = Color(0xFFFF5252),
                            modifier = Modifier.background(Color(0xFF101214), RoundedCornerShape(10.dp)).padding(horizontal = 16.dp, vertical = 4.dp),
                        )
                        Text(String.format(Locale.getDefault(), "%.1f°", 90.0 - angle) + " " + stringResource(R.string.rangefinder_below_horizon))
                    }
                }
                Text(
                    stringResource(R.string.rangefinder_height, String.format(Locale.getDefault(), "%.2f", height)),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Slider(
                    value = height,
                    onValueChange = { height = (Math.round(it * 20f) / 20f); prefs.putFloat(KEY_HEIGHT, height) },
                    valueRange = 0.5f..2.5f,
                )
                Text(stringResource(R.string.rangefinder_hint), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/** Live-Bild der Rückkamera (CameraX). Nur auf dem Gerät prüfbar. */
@Composable
private fun CameraPreview(modifier: Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    DisposableEffect(lifecycleOwner) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            runCatching {
                val provider = future.get()
                val preview = Preview.Builder().build().apply { setSurfaceProvider(previewView.surfaceProvider) }
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose { runCatching { future.get().unbindAll() } }
    }
    AndroidView(factory = { previewView }, modifier = modifier)
}
