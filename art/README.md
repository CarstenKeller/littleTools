# Grafiken

Alle Bilder der App sind selbst gezeichnet (SVG in `shapes.py`), keine fremden Fotos.
Erzeugen: `python3 art/render.py` aus dem Repo-Root (schreibt WebP nach
`app/src/main/res/drawable-nodpi/` bzw. `drawable-en-nodpi/`). Mit `--preview <ordner>` zusätzlich PNGs.

Bewegliche Teile (Kompassrose, Blasen, Zeiger) sind eigene Bildebenen. Ihre Lage steht in
`GEOMETRY` (shapes.py) **und** `ArtGeometry.kt` – beide gemeinsam ändern.
