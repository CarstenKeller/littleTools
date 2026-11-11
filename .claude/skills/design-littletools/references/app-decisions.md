# littletools – App-spezifische Designentscheidungen

Gilt **nur für diese App** und wird **nicht** ins allgemeine Register
übernommen. Ergänzt per Kommando `/da`. Allgemeine Entscheidungen stehen
in `design-decisions.md` (Kopie, Stand siehe `../SYNC.md`); bei
Widerspruch gilt in dieser App der Eintrag hier.

## Leitidee

"lauter kleine Tools, die man so im Alltag brauchen kann" – schnell erreichbar, ein Tool pro Bildschirm.

## Einträge

<!-- Format:
### A-xx: <Kurztitel>
- **Entscheidung:**
- **Begründung:**
- **Abweichung von:** <D-xx | – >
- **Quelle:** <wörtliche Vorgabe (/da) | abgeleitet aus …>, <Datum>
- **Status:** <bestätigt (Datum) | abgeleitet | ersetzt durch A-yy>
-->

### A-01: Startraster mit Kacheln als Navigation
- **Entscheidung:** Die Startseite zeigt ein Raster mit einer Kachel (Icon + Name) je Tool. Ein Tool füllt den ganzen Bildschirm; Zurück (Systemgeste oder Pfeil oben links) führt zum Raster.
- **Begründung:** 7 Tools – Bottom-Navigation passt laut Material nur für 3–5 Ziele.
- **Abweichung von:** – (beantwortet die allgemeine offene Frage F-05 nur für diese App; ob das für alle Apps gelten soll, ist offen → ggf. `/d`)
- **Quelle:** Auswahl des Nutzers per Rückfrage bei `/i`, 2026-11-10
- **Status:** bestätigt (2026-11-10)

### A-02: Realitätsnahe Grafiken für Kacheln und Werkzeuge
- **Entscheidung:** Kacheln und Werkzeug-Ansichten zeigen realitätsnahe Darstellungen der echten Geräte (Messingkompass, Alu-Wasserwaage mit Libelle, Dosenlibelle, gelber Zollstock, Taschenlampe, Entfernungsmesser-Anzeige, analoger Höhenmesser) statt flacher Icons.
- **Begründung:** "Kacheln mit einem schicken idealerweise fotorealischen Bild"; "Die Grafiken für die Funktionen hätte ich auch gerne realitätsnah dargestellt."
- **Umsetzung:** Selbst gezeichnet als SVG mit Verläufen/Glanz/Schatten (`art/shapes.py`), per `art/render.py` zu WebP gerendert (`drawable-nodpi`). Bewegliche Teile (Rose, Blasen, Zeiger) sind eigene Bildebenen; ihre Lage steht in `ArtGeometry.kt` und `GEOMETRY` in `shapes.py`. Gerätefarben sind bewusst fest (nicht `colorScheme`). Beschriftungen in Bildern nur sprachneutral oder je Sprache (`drawable-en-nodpi`, z. B. Kompassrose O/E, D-06). Keine fremden Fotos (Lizenz) – echte Fotorealistik ist damit **nicht** erreicht, sondern ein realitätsnaher Illustrationsstil.
- **Abweichung von:** – (ergänzt D-03; Gerätegrafiken nutzen keine dynamischen Farben)
- **Quelle:** wörtliche Vorgabe, 2026-11-11; Umsetzung als Illustration ist Interpretation von Claude
- **Status:** bestätigt (2026-11-11), Umsetzung abgeleitet

### A-03: Startraster füllt den Bildschirm, Spaltenzahl einstellbar
- **Entscheidung:** Standard "Automatisch": alle Kacheln ohne Scrollen sichtbar und möglichst groß; Kacheln dürfen dafür zwischen 0,7 und 1,4 Seitenverhältnis haben (Bild wird beschnitten). In den Einstellungen wählbar: Automatisch oder feste 1–4 Spalten (quadratische Kacheln, ggf. Scrollen). Name des Tools als weiße Schrift auf dunklem Verlauf unten auf der Kachel.
- **Begründung:** "Standardmäßig versucht die app die Kacheln optimal in einem Griff auf dem Bildschirm zu verteilen. In den Einstellungen kann ich die Gridgröße festlegen."
- **Abweichung von:** ergänzt A-01 (Kachel = Bild + Name statt Icon + Name)
- **Quelle:** wörtliche Vorgabe, 2026-11-11; "Gridgröße" als Spaltenzahl verstanden und Seitenverhältnis-Spielraum = Interpretation von Claude
- **Status:** bestätigt (2026-11-11), Details abgeleitet

### A-04: Einstellungen als eigener Bildschirm
- **Entscheidung:** Zahnrad-Icon oben rechts auf der Startseite öffnet "Einstellungen" mit Darstellung (Hell/Dunkel/System) und Raster.
- **Abweichung von:** D-07, Umsetzungsdetail (dort: Icon öffnet Dialog nur für die Darstellung) – nötig, weil es jetzt mehrere Einstellungen gibt.
- **Quelle:** abgeleitet aus A-03, 2026-11-11
- **Status:** abgeleitet

## Offene Designfragen dieser App

- [ ] …
