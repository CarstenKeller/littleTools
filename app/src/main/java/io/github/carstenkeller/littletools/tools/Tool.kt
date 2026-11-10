package io.github.carstenkeller.littletools.tools

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.carstenkeller.littletools.R

/** Alle Tools in der Reihenfolge des Startrasters (A-01). */
enum class Tool(@StringRes val title: Int, val icon: ImageVector) {
    COMPASS(R.string.tool_compass, Icons.Filled.Explore),
    EDGE_LEVEL(R.string.tool_edge_level, Icons.Filled.LinearScale),
    FLAT_LEVEL(R.string.tool_flat_level, Icons.Filled.Adjust),
    RULER(R.string.tool_ruler, Icons.Filled.Straighten),
    FLASHLIGHT(R.string.tool_flashlight, Icons.Filled.FlashlightOn),
    RANGEFINDER(R.string.tool_rangefinder, Icons.Filled.CenterFocusStrong),
    POSITION(R.string.tool_position, Icons.Filled.MyLocation),
}
