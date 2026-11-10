package io.github.carstenkeller.littletools.tools

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.logic.ToolMath
import io.github.carstenkeller.littletools.settings.Prefs
import io.github.carstenkeller.littletools.ui.components.IconActionButton
import java.util.Locale

private const val KEY_FACTOR = "ruler_factor"
private const val CARD_MM = 85.6f // Bankkarte (ISO/IEC 7810 ID-1), lange Seite

/** Lineal entlang der linken Bildschirmkante; Länge aus ydpi, Feinabgleich per Regler. */
@Composable
fun RulerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val ydpi = remember { context.resources.displayMetrics.ydpi }
    var factor by remember { mutableFloatStateOf(prefs.getFloat(KEY_FACTOR, 1f)) }
    var calibrating by rememberSaveable { mutableStateOf(false) }
    val pxPerMm = ToolMath.pxPerMm(ydpi, factor)

    ToolScaffold(Tool.RULER, onBack, actions = {
        IconActionButton(Icons.Filled.Tune, stringResource(R.string.ruler_calibrate), { calibrating = !calibrating })
    }) { modifier ->
        Box(modifier) {
            val lineColor = MaterialTheme.colorScheme.onBackground
            val accent = MaterialTheme.colorScheme.primary
            val measurer = rememberTextMeasurer()
            val labelStyle = MaterialTheme.typography.labelMedium.copy(color = lineColor)
            Canvas(Modifier.fillMaxHeight().width(120.dp)) {
                val totalMm = (size.height / pxPerMm).toInt()
                for (mm in 0..totalMm) {
                    val y = mm * pxPerMm
                    val len = when {
                        mm % 10 == 0 -> 48.dp.toPx()
                        mm % 5 == 0 -> 32.dp.toPx()
                        else -> 18.dp.toPx()
                    }
                    drawLine(lineColor, Offset(0f, y), Offset(len, y), strokeWidth = 1.dp.toPx())
                    if (mm % 10 == 0 && mm > 0) {
                        val text = (mm / 10).toString()
                        val layout = measurer.measure(text, labelStyle)
                        drawText(layout, topLeft = Offset(len + 6.dp.toPx(), y - layout.size.height / 2f))
                    }
                }
                if (calibrating) {
                    // Kartenlänge markieren: Karte an Null anlegen, Regler bis zur Kartenkante.
                    val yc = CARD_MM * pxPerMm
                    drawLine(accent, Offset(0f, yc), Offset(size.width, yc), strokeWidth = 3.dp.toPx())
                    drawRect(accent, topLeft = Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(size.width, yc),
                        style = Stroke(2.dp.toPx()))
                }
            }
            if (calibrating) {
                Column(
                    Modifier.align(Alignment.BottomEnd).padding(start = 130.dp, end = 16.dp, bottom = 16.dp),
                ) {
                    Text(stringResource(R.string.ruler_calibrate_hint), style = MaterialTheme.typography.bodyMedium)
                    Text(String.format(Locale.getDefault(), "%.3f", factor), style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = factor,
                        onValueChange = { factor = it; prefs.putFloat(KEY_FACTOR, it) },
                        valueRange = 0.85f..1.15f,
                    )
                    IconActionButton(Icons.Filled.RestartAlt, stringResource(R.string.reset), {
                        factor = 1f; prefs.putFloat(KEY_FACTOR, 1f)
                    })
                }
            } else {
                Text(
                    stringResource(R.string.ruler_unit),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
    }
}
