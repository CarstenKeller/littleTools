package io.github.carstenkeller.littletools.ui.art

/**
 * Lage der beweglichen Teile in den Grafiken (Einheiten = viewBox der SVG-Quellen in art/shapes.py,
 * dort GEOMETRY). Bei Änderungen an den Grafiken beide Stellen anpassen.
 */
object ArtGeometry {
    // Wasserwaage (senkrechtes Bild 300 × 1500)
    const val EDGE_W = 300f
    const val EDGE_H = 1500f
    const val EDGE_VIAL_X = 150f
    const val EDGE_VIAL_TOP = 540f
    const val EDGE_VIAL_BOTTOM = 960f
    const val EDGE_VIAL_W = 112f

    // Dosenlibelle (1000 × 1000)
    const val FLAT_SIZE = 1000f
    const val FLAT_VIAL_R = 392f
    const val FLAT_BUBBLE_R = 64f

    /** Die Blase füllt 88 % ihres Bildes (rx = 0,44 · Bildgröße). */
    const val BUBBLE_FILL = 0.88f
}
