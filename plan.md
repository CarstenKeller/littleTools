# littleTools – Feature- und Umsetzungsplan

Idee (wörtlich, /i 2026-11-10): "lauter kleine Tools, die man so im Alltag brauchen kann:
Kompass, Wasserwaage wenn ich das Smartphone auf die lange Kante stelle, Wasserwaage (Libelle)
für Flächen wenn das Smartphone flach liegt, Zentimetermaß, Taschenlampe, Entfernungsmesser,
Positionsangabe mit Höhenmesser."

## Technische Grundentscheidungen
- **Native Android-App** (Kotlin + Jetpack Compose), nicht Web-App – Entscheidung des Nutzers
  auf Rückfrage (2026-11-10). Grund: voller Zugriff auf Sensoren, Taschenlampe, Barometer.
- minSdk 29, compileSdk/targetSdk 36 (Kickstart-Standard).
- Navigation ohne Bibliothek: Zustand "aktuelles Tool" + `BackHandler` (A-01 Startraster).
- Sensoren direkt über `SensorManager`, Standort über `LocationManager` (keine Google-Play-Dienste nötig).
- App fest im Hochformat: Sensor-Tools (Wasserwaage auf der Kante!) würden sonst beim Drehen
  den Bildschirm umbauen. Lesbarkeit der Kanten-Wasserwaage durch gedrehte Anzeige.
- Rechenlogik (Winkel, Entfernung, Koordinatenformat) in reinem Kotlin → JVM-Unit-Tests in CI.

## Annahmen (nicht ausdrücklich vom Nutzer entschieden)
- Kompass zeigt **magnetisch Nord**, nur bei flach gehaltenem Gerät. Missweisung (geografisch Nord) später.
- Wasserwaage Kante: nur lange Kante, Winkel der Längsachse zur Horizontalen; Nullpunkt-Kalibrierung per Knopf.
- Libelle: Neigung in zwei Achsen, Nullpunkt-Kalibrierung per Knopf (einfache Offset-Kalibrierung,
  keine 180°-Umschlagmessung).
- Zentimetermaß: Lineal entlang der linken Bildschirmkante aus `ydpi`; da `ydpi` bei manchen Geräten
  ungenau ist, Feinabgleich per Regler (Bankkarte = 85,6 mm als Referenz).
- Taschenlampe: Kamera-Blitz über `CameraManager.setTorchMode` (keine Kamera-Berechtigung nötig);
  ohne Blitz: weißer Bildschirm. Aus beim Verlassen des Tools.
- Entfernungsmesser (Entscheidung Nutzer: **Neigungswinkel**): Gerät in bekannter Höhe halten,
  Fadenkreuz auf den Fußpunkt des Objekts; Entfernung = Halteh. × tan(Winkel zur Senkrechten).
  Haltehöhe einstellbar (Standard 1,50 m). Nur ebener Boden. Kamerabild als Zielhilfe (CameraX).
- Position: Breite/Länge (Dezimal + Grad/Min/Sek), Genauigkeit, GPS-Höhe (über NN, falls das Gerät
  sie liefert, sonst ellipsoidisch und so beschriftet), barometrische Höhe (Standardatmosphäre,
  wetterabhängig; Abgleich mit GPS-Höhe per Knopf). Koordinaten kopierbar.
- Bildschirm bleibt an, solange ein Tool offen ist.
- Package-Name `io.github.carstenkeller.littletools`.

## Offene technische Unsicherheiten
- Kompass-Genauigkeit hängt stark vom Gerät/Magnetfeld ab; Kalibrier-Hinweis bei niedriger Genauigkeit.
- `ydpi` kann vom echten Wert abweichen → Regler.
- `Location.getMslAltitudeMeters()` (ab Android 14) liefert nicht jedes Gerät.
- Viele Geräte haben **kein Barometer** → Hinweis statt Wert.
- CameraX-Vorschau in Compose (AndroidView + PreviewView) – nur Gerätetest zeigt, ob sie läuft.

## Phase 1 – MVP
- [x] Grundgerüst + CI (Kickstart), Design-Skill
- [x] Startraster (A-01), Darstellung Hell/Dunkel/System (D-07), DE/EN (D-06)
- [x] Taschenlampe
- [x] Zentimetermaß
- [x] Wasserwaage Kante
- [x] Libelle Fläche
- [x] Kompass
- [x] Position + Höhenmesser
- [x] Entfernungsmesser (Neigung, mit Kamerabild)

## Phase 1b – Optik (2026-11-11)
- [x] Realitätsnahe Kachelbilder (A-02), Raster füllt Bildschirm / Spalten einstellbar (A-03), Einstellungen (A-04)
- [x] Realitätsnahe Werkzeuge: Kompass (Messing, drehende Rose), Wasserwaage (Alu, Libelle), Dosenlibelle,
      Zollstock-Optik, Taschenlampe (Bild zum Antippen), Entfernungsmesser-Anzeige, analoger Höhenmesser
- Grafikquelle: `art/shapes.py` → `python3 art/render.py` (braucht Node-Playwright + Pillow)

## Phase 2 – Ideen
- [ ] Kompass: geografisch Nord (Missweisung über `GeomagneticField`), Hochkant-Modus
- [ ] Taschenlampe: Helligkeit (ab Android 13), Blinken/SOS
- [ ] Wasserwaage: auch kurze Kante; Zwei-Lagen-Kalibrierung
- [ ] Entfernungsmesser: Objekthöhe

## Fortschritt
- 2026-11-10: Grundgerüst grün (build-fb92a3f). MVP mit allen 7 Tools grün im ersten Anlauf
  (build-505d43d, Unit-Tests ToolMath ausgeführt, lintVital ok, APK 2,9 MB).
  **Alles nur CI-kompiliert, nichts auf dem Gerät geprüft** – Sensorrichtungen (Vorzeichen
  Blase/Kompass), Lineal-Maßstab, Kamera-Vorschau, Torch, Standort sind offen.
