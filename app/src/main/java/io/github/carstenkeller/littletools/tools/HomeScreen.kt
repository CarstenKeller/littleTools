package io.github.carstenkeller.littletools.tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.logic.GridFit
import io.github.carstenkeller.littletools.ui.components.IconActionButton

private val GAP = 12.dp
private val PAD = 16.dp

/**
 * A-01/A-03: Startraster mit Bildkacheln. [gridColumns] = 0: alle Kacheln ohne Scrollen,
 * möglichst groß; sonst feste Spaltenzahl (Einstellung), gescrollt wird bei Bedarf.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(gridColumns: Int, onOpen: (Tool) -> Unit, onSettings: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconActionButton(Icons.Filled.Settings, stringResource(R.string.settings), onSettings)
                },
            )
        },
    ) { padding ->
        BoxWithConstraints(Modifier.padding(padding).fillMaxSize().padding(PAD)) {
            val tools = Tool.entries
            val w = maxWidth.value
            val h = maxHeight.value
            val layout = if (gridColumns <= 0) GridFit.best(tools.size, w, h, GAP.value)
            else GridFit.fixed(tools.size, gridColumns, w, GAP.value)
            val tileW = layout.tileWidth.dp
            val tileH = layout.tileHeight.dp
            val rows = tools.chunked(layout.columns)
            val scroll = if (gridColumns > 0) Modifier.verticalScroll(rememberScrollState()) else Modifier
            Column(
                Modifier.fillMaxSize().then(scroll),
                verticalArrangement = Arrangement.spacedBy(GAP, if (gridColumns > 0) Alignment.Top else Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(GAP)) {
                        row.forEach { t -> ToolTile(t, tileW, tileH) { onOpen(t) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolTile(tool: Tool, width: Dp, height: Dp, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(width, height),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painterResource(tool.tile),
                contentDescription = null, // Name steht als Text auf der Kachel
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Schrift auf dunklem Verlauf: lesbar in Hell und Dunkel, unabhängig vom Bild.
            Box(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000))))
                    .padding(start = 8.dp, end = 8.dp, top = 20.dp, bottom = 10.dp),
            ) {
                Text(
                    stringResource(tool.title),
                    color = Color.White,
                    // Lange Wörter (Entfernungsmesser) an Silbengrenzen trennen statt mitten im Wort.
                    style = (if (minOf(width, height) < 120.dp) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium)
                        .copy(hyphens = Hyphens.Auto, lineBreak = LineBreak.Paragraph),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
