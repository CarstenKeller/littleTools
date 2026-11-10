package io.github.carstenkeller.littletools.logic

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Reine Rechenlogik ohne Android-Abhängigkeiten (per JVM-Unit-Test geprüft).
 *
 * Konvention Beschleunigungssensor (Android): Liegt das Gerät ruhig, zeigt der
 * Messvektor (x, y, z) nach OBEN (Gegenkraft zur Schwerkraft). Geräteachsen:
 * x nach rechts, y zur Oberkante, z aus dem Bildschirm heraus.
 */
object ToolMath {

    private fun deg(rad: Double) = Math.toDegrees(rad)

    /**
     * Libelle (Gerät flach): Neigung um beide Achsen in Grad.
     * Positiv bei x, wenn die rechte Kante höher liegt; positiv bei y, wenn die Oberkante höher liegt.
     * Die Blase wandert zur höheren Seite, also in Richtung (x, y).
     */
    fun flatTilt(ax: Float, ay: Float, az: Float): Pair<Double, Double> {
        val x = deg(atan2(ax.toDouble(), az.toDouble()))
        val y = deg(atan2(ay.toDouble(), az.toDouble()))
        return x to y
    }

    /**
     * Wasserwaage auf der langen Kante: Winkel der Längsachse (y) zur Horizontalen in Grad.
     * Positiv, wenn die Oberkante des Geräts höher liegt.
     */
    fun edgeTilt(ax: Float, ay: Float, az: Float): Double =
        deg(atan2(ay.toDouble(), sqrt((ax * ax + az * az).toDouble())))

    /** Steht das Gerät auf einer langen Kante? (x-Achse zeigt annähernd senkrecht) */
    fun isOnLongEdge(ax: Float, ay: Float, az: Float): Boolean {
        val g = sqrt((ax * ax + ay * ay + az * az).toDouble())
        return g > 0 && abs(ax) / g > 0.7
    }

    /** +1: rechte Kante oben (Gerät liegt auf der linken Kante), -1: linke Kante oben. */
    fun edgeUpSide(ax: Float): Int = if (ax >= 0f) 1 else -1

    /**
     * Winkel zwischen Kamerablickrichtung (Geräte-Rückseite, -z) und der Senkrechten nach unten, in Grad.
     * 0° = Kamera zeigt senkrecht nach unten, 90° = waagerecht.
     */
    fun cameraAngleFromVertical(ax: Float, ay: Float, az: Float): Double {
        val g = sqrt((ax * ax + ay * ay + az * az).toDouble())
        if (g == 0.0) return 0.0
        return deg(acos((az / g).coerceIn(-1.0, 1.0)))
    }

    /** Entfernung zum anvisierten Fußpunkt auf ebenem Boden; null, wenn nicht sinnvoll messbar. */
    fun distanceOnGround(holdHeightM: Double, angleFromVerticalDeg: Double, maxAngleDeg: Double = 88.0): Double? {
        if (angleFromVerticalDeg < 0 || angleFromVerticalDeg >= maxAngleDeg) return null
        return holdHeightM * tan(Math.toRadians(angleFromVerticalDeg))
    }

    /** Kompassrichtung 0..360° auf einen Sektor 0..7 (N, NO, O, SO, S, SW, W, NW). */
    fun cardinalIndex(headingDeg: Double): Int {
        val h = normalizeDeg(headingDeg)
        return (floor((h + 22.5) / 45.0).toInt()) % 8
    }

    fun normalizeDeg(d: Double): Double {
        val r = d % 360.0
        return if (r < 0) r + 360.0 else r
    }

    /** Dezimalgrad in Grad/Minuten/Sekunden, z. B. 52°31'12.3"N. */
    fun toDms(value: Double, isLatitude: Boolean): String {
        val hemi = if (isLatitude) (if (value >= 0) "N" else "S") else (if (value >= 0) "E" else "W")
        val a = abs(value)
        var d = floor(a).toInt()
        val mFull = (a - d) * 60.0
        var m = floor(mFull).toInt()
        var s = (mFull - m) * 60.0
        // Rundung auf eine Nachkommastelle darf nicht "60.0"" ergeben.
        if (Math.round(s * 10) >= 600) { s = 0.0; m += 1 }
        if (m >= 60) { m = 0; d += 1 }
        return "%d°%02d'%04.1f\"%s".format(java.util.Locale.ROOT, d, m, s, hemi)
    }

    /** Barometrische Höhe (internationale Standardatmosphäre), wie SensorManager.getAltitude. */
    fun pressureToAltitude(p0hPa: Double, phPa: Double): Double =
        44330.0 * (1.0 - (phPa / p0hPa).pow(1.0 / 5.255))

    /** Umkehrung: Luftdruck auf Meereshöhe, bei dem der gemessene Druck der bekannten Höhe entspricht. */
    fun seaLevelPressure(phPa: Double, altitudeM: Double): Double =
        phPa / (1.0 - altitudeM / 44330.0).pow(5.255)

    /** Pixel pro Millimeter aus der Bildschirmdichte (dpi) und einem Korrekturfaktor. */
    fun pxPerMm(dpi: Float, factor: Float): Float = dpi / 25.4f * factor
}

/** Einfacher Tiefpass zum Glätten der Sensorwerte. */
fun lowPass(input: FloatArray, previous: FloatArray?, alpha: Float = 0.15f): FloatArray {
    if (previous == null || previous.size != input.size) return input.copyOf()
    return FloatArray(input.size) { i -> previous[i] + alpha * (input[i] - previous[i]) }
}
