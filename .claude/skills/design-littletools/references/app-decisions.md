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

## Offene Designfragen dieser App

- [ ] …
