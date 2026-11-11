package io.github.carstenkeller.littletools.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GridFitTest {
    @Test fun typicalPhone() {
        // 328 × 472 dp: 2 Spalten × 4 Zeilen, 158 × 109 -> auf Seitenverhältnis 1,4 begrenzt
        val g = GridFit.best(7, 328f, 472f, 12f)
        assertEquals(2, g.columns); assertEquals(4, g.rows)
        assertEquals(109f, g.tileHeight, 0.01f)
        assertEquals(109f * 1.4f, g.tileWidth, 0.01f)
    }

    @Test fun allTilesFit() {
        for ((w, h) in listOf(328f to 472f, 400f to 800f, 300f to 300f, 900f to 400f)) {
            val g = GridFit.best(7, w, h, 12f)
            assertTrue(g.columns * g.tileWidth + (g.columns - 1) * 12f <= w + 0.01f)
            assertTrue(g.rows * g.tileHeight + (g.rows - 1) * 12f <= h + 0.01f)
            assertTrue(g.columns * g.rows >= 7)
            val aspect = g.tileWidth / g.tileHeight
            assertTrue(aspect in GridFit.MIN_ASPECT - 0.001f..GridFit.MAX_ASPECT + 0.001f)
        }
    }

    @Test fun wideAreaUsesMoreColumns() {
        val g = GridFit.best(7, 1000f, 300f, 0f)
        assertTrue(g.columns >= 4)
    }

    @Test fun fixedColumns() {
        val g = GridFit.fixed(7, 3, 300f, 0f)
        assertEquals(3, g.columns); assertEquals(3, g.rows); assertEquals(100f, g.tileWidth, 0.001f)
        assertEquals(7, GridFit.fixed(7, 99, 700f, 0f).columns)
    }
}
