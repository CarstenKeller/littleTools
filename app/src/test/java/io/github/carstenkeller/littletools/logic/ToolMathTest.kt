package io.github.carstenkeller.littletools.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolMathTest {
    private val g = 9.81f
    private val eps = 1e-6

    @Test fun flatLevelIsZero() {
        val (x, y) = ToolMath.flatTilt(0f, 0f, g)
        assertEquals(0.0, x, eps); assertEquals(0.0, y, eps)
    }

    @Test fun flatRightEdgeRaised() {
        // 45° um die y-Achse: Messvektor (nach oben) hat gleiche x- und z-Anteile.
        val (x, _) = ToolMath.flatTilt(1f, 0f, 1f)
        assertEquals(45.0, x, eps)
    }

    @Test fun edgeTiltLevel() {
        assertEquals(0.0, ToolMath.edgeTilt(g, 0f, 0f), eps)
        assertEquals(0.0, ToolMath.edgeTilt(-g, 0f, 0f), eps)
    }

    @Test fun edgeTiltTopRaised() {
        assertEquals(45.0, ToolMath.edgeTilt(1f, 1f, 0f), eps)
        assertEquals(-45.0, ToolMath.edgeTilt(-1f, -1f, 0f), eps)
    }

    @Test fun longEdgeDetection() {
        assertTrue(ToolMath.isOnLongEdge(g, 0f, 0f))
        assertFalse(ToolMath.isOnLongEdge(0f, 0f, g))
        assertFalse(ToolMath.isOnLongEdge(0f, g, 0f))
    }

    @Test fun cameraAngle() {
        assertEquals(0.0, ToolMath.cameraAngleFromVertical(0f, 0f, g), eps)   // flach, Kamera nach unten
        assertEquals(90.0, ToolMath.cameraAngleFromVertical(0f, g, 0f), eps)  // hochkant, waagerecht
        assertEquals(45.0, ToolMath.cameraAngleFromVertical(0f, 1f, 1f), eps)
    }

    @Test fun distance() {
        assertEquals(1.5, ToolMath.distanceOnGround(1.5, 45.0)!!, 1e-9)
        assertNull(ToolMath.distanceOnGround(1.5, 89.0))
        assertNull(ToolMath.distanceOnGround(1.5, -1.0))
    }

    @Test fun cardinal() {
        assertEquals(0, ToolMath.cardinalIndex(0.0))
        assertEquals(0, ToolMath.cardinalIndex(359.0))
        assertEquals(1, ToolMath.cardinalIndex(45.0))
        assertEquals(2, ToolMath.cardinalIndex(90.0))
        assertEquals(4, ToolMath.cardinalIndex(180.0))
        assertEquals(6, ToolMath.cardinalIndex(-90.0))
    }

    @Test fun dms() {
        assertEquals("52°30'00.0\"N", ToolMath.toDms(52.5, true))
        assertEquals("13°24'00.0\"E", ToolMath.toDms(13.4, false))
        assertEquals("0°30'00.0\"W", ToolMath.toDms(-0.5, false))
        // Rundung an der Minutengrenze
        assertEquals("10°01'00.0\"N", ToolMath.toDms(10.0 + 1.0 / 60.0 - 0.00001 / 3600.0, true))
    }

    @Test fun barometerRoundTrip() {
        val p0 = ToolMath.seaLevelPressure(950.0, 540.0)
        assertEquals(540.0, ToolMath.pressureToAltitude(p0, 950.0), 1e-6)
        assertEquals(0.0, ToolMath.pressureToAltitude(1013.25, 1013.25), 1e-9)
    }

    @Test fun lowPassStartsWithInput() {
        val a = lowPass(floatArrayOf(1f, 2f), null)
        assertEquals(1f, a[0]); assertEquals(2f, a[1])
        val b = lowPass(floatArrayOf(11f, 2f), a, 0.5f)
        assertEquals(6f, b[0])
    }
}
