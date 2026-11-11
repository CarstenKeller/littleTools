package io.github.carstenkeller.littletools.tools

import androidx.annotation.DrawableRes
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
/** [tile] = selbst gezeichnete, realitätsnahe Kachelgrafik (A-02, Quelle art/shapes.py). */
enum class Tool(@StringRes val title: Int, val icon: ImageVector, @DrawableRes val tile: Int) {
    COMPASS(R.string.tool_compass, Icons.Filled.Explore, R.drawable.tile_compass),
    EDGE_LEVEL(R.string.tool_edge_level, Icons.Filled.LinearScale, R.drawable.tile_edge_level),
    FLAT_LEVEL(R.string.tool_flat_level, Icons.Filled.Adjust, R.drawable.tile_flat_level),
    RULER(R.string.tool_ruler, Icons.Filled.Straighten, R.drawable.tile_ruler),
    FLASHLIGHT(R.string.tool_flashlight, Icons.Filled.FlashlightOn, R.drawable.tile_flashlight),
    RANGEFINDER(R.string.tool_rangefinder, Icons.Filled.CenterFocusStrong, R.drawable.tile_rangefinder),
    POSITION(R.string.tool_position, Icons.Filled.MyLocation, R.drawable.tile_position),
}
