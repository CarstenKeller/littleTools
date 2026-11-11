package io.github.carstenkeller.littletools.logic

import kotlin.math.ceil

/** Ergebnis der Rasterberechnung: Spalten, Zeilen und Größe einer Kachel. */
data class GridLayout(val columns: Int, val rows: Int, val tileWidth: Float, val tileHeight: Float)

object GridFit {
    /** Erlaubtes Seitenverhältnis (Breite/Höhe) einer Kachel; das Bild wird passend beschnitten. */
    const val MIN_ASPECT = 0.7f
    const val MAX_ASPECT = 1.4f

    /**
     * Verteilt [count] Kacheln so auf [width] × [height], dass alle ohne Scrollen sichtbar sind und
     * die Kachelfläche möglichst groß wird. [gap] = Abstand zwischen Kacheln.
     */
    fun best(count: Int, width: Float, height: Float, gap: Float): GridLayout {
        require(count > 0)
        var best = GridLayout(1, count, 0f, 0f)
        for (c in 1..count) {
            val rows = ceil(count / c.toDouble()).toInt()
            var w = (width - (c - 1) * gap) / c
            var h = (height - (rows - 1) * gap) / rows
            if (w <= 0f || h <= 0f) continue
            if (w / h > MAX_ASPECT) w = h * MAX_ASPECT
            if (w / h < MIN_ASPECT) h = w / MIN_ASPECT
            if (w * h > best.tileWidth * best.tileHeight) best = GridLayout(c, rows, w, h)
        }
        return best
    }

    /** Feste Spaltenzahl (Einstellung): quadratische Kacheln über die volle Breite, Scrollen bei Bedarf. */
    fun fixed(count: Int, columns: Int, width: Float, gap: Float): GridLayout {
        val c = columns.coerceIn(1, count)
        val w = (width - (c - 1) * gap) / c
        return GridLayout(c, ceil(count / c.toDouble()).toInt(), w, w)
    }
}
