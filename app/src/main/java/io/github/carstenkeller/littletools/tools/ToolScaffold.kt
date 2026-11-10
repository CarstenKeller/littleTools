package io.github.carstenkeller.littletools.tools

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.carstenkeller.littletools.R
import io.github.carstenkeller.littletools.ui.components.IconActionButton

/** Gemeinsamer Rahmen eines Tools: Titel, Zurück-Pfeil, optionale Aktionen (Icons mit Tooltip, D-01). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolScaffold(
    tool: Tool,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(tool.title)) },
                navigationIcon = {
                    IconActionButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        label = stringResource(R.string.back),
                        onClick = onBack,
                    )
                },
                actions = actions,
            )
        },
    ) { padding ->
        content(Modifier.padding(padding).fillMaxSize())
    }
}

/** Hinweis statt leerem Bildschirm (z. B. Sensor fehlt, Berechtigung verweigert; D-05). */
@Composable
fun InfoMessage(text: String, modifier: Modifier = Modifier, extra: @Composable () -> Unit = {}) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            extra()
        }
    }
}
