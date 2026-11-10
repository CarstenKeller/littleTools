package io.github.carstenkeller.littletools.tools

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.sensors.KeepScreenOn

/** Kamera mit Blitz (bevorzugt Rückkamera); null, wenn das Gerät keinen hat. */
private fun findTorchCamera(cm: CameraManager): String? = runCatching {
    val withFlash = cm.cameraIdList.filter {
        cm.getCameraCharacteristics(it).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    }
    withFlash.firstOrNull {
        cm.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
    } ?: withFlash.firstOrNull()
}.getOrNull()

@Composable
fun FlashlightScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val cm = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    val cameraId = remember { findTorchCamera(cm) }
    var on by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    // Torch folgt dem Schalter; beim Verlassen des Tools immer aus.
    // setTorchMode braucht keine Kamera-Berechtigung (ab Android 6).
    DisposableEffect(on, cameraId) {
        if (cameraId != null) {
            error = runCatching { cm.setTorchMode(cameraId, on) }.isFailure
        }
        onDispose {
            if (cameraId != null && on) runCatching { cm.setTorchMode(cameraId, false) }
        }
    }

    // Ohne Blitz: heller Bildschirm als Ersatz.
    val screenLight = on && (cameraId == null || error)
    if (screenLight) KeepScreenOn()

    ToolScaffold(Tool.FLASHLIGHT, onBack) { modifier ->
        Box(
            modifier.then(if (screenLight) Modifier.background(Color.White) else Modifier).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val label = stringResource(if (on) R.string.flashlight_off else R.string.flashlight_on)
                FilledIconToggleButton(
                    checked = on,
                    onCheckedChange = { on = it },
                    modifier = Modifier.size(160.dp).semantics { contentDescription = label },
                ) {
                    Icon(
                        if (on) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                    )
                }
                Spacer(Modifier.height(24.dp))
                if (cameraId == null || error) {
                    Text(
                        stringResource(R.string.flashlight_no_flash),
                        textAlign = TextAlign.Center,
                        color = if (screenLight) Color.Black else MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}
