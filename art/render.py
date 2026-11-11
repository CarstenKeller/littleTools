"""Rendert alle Grafiken als WebP nach app/src/main/res/drawable*-nodpi/.
Aufruf (aus dem Repo-Root): python3 art/render.py [--preview <ordner>]"""
import io, json, os, subprocess, sys, tempfile
sys.path.insert(0, os.path.dirname(__file__))
import shapes as S
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(ROOT, "app/src/main/res")

# name -> (svg, Ausgabegröße in px (Breite), Ordner)
ITEMS = {
    "tile_compass": (S.tile_compass(), 512, "drawable-nodpi"),
    "tile_edge_level": (S.tile_edge_level(), 512, "drawable-nodpi"),
    "tile_flat_level": (S.tile_flat_level(), 512, "drawable-nodpi"),
    "tile_ruler": (S.tile_ruler(), 512, "drawable-nodpi"),
    "tile_flashlight": (S.tile_flashlight(), 512, "drawable-nodpi"),
    "tile_rangefinder": (S.tile_rangefinder(), 512, "drawable-nodpi"),
    "tile_position": (S.tile_position(), 512, "drawable-nodpi"),
    "compass_housing": (S.compass_housing(), 1000, "drawable-nodpi"),
    "compass_card": (S.compass_card("de"), 1000, "drawable-nodpi"),
    "compass_card@en": (S.compass_card("en"), 1000, "drawable-en-nodpi"),
    "glass_compass": (S.glass_overlay(True), 1000, "drawable-nodpi"),
    "glass_round": (S.glass_overlay(False), 1000, "drawable-nodpi"),
    "edge_level_bar": (S.edge_level_bar(), 300, "drawable-nodpi"),
    "edge_level_glass": (S.edge_level_glass(), 300, "drawable-nodpi"),
    "bubble": (S.bubble(200), 200, "drawable-nodpi"),
    "flat_level_base": (S.flat_level_base(), 1000, "drawable-nodpi"),
    "flat_level_glass": (S.flat_level_glass(), 1000, "drawable-nodpi"),
    "flashlight_off": (S.flashlight(False), 600, "drawable-nodpi"),
    "flashlight_on": (S.flashlight(True), 600, "drawable-nodpi"),
    "altimeter_dial": (S.altimeter_dial(), 1000, "drawable-nodpi"),
    "needle_long": (S.needle("long"), 1000, "drawable-nodpi"),
    "needle_short": (S.needle("short"), 1000, "drawable-nodpi"),
}
# Große Rund-Instrumente auf 800 px verkleinern (reicht für Telefone, spart Platz)
TARGET = {1000: 800}

def main():
    preview = sys.argv[2] if len(sys.argv) > 2 and sys.argv[1] == "--preview" else None
    tmp = tempfile.mkdtemp(prefix="art-")
    jobs = []
    for name, (svgtext, w, folder) in ITEMS.items():
        vb = svgtext.split('viewBox="')[1].split('"')[0].split()
        path = os.path.join(tmp, name.replace("@", "_") + ".svg")
        open(path, "w").write(svgtext)
        jobs.append({"svg": path, "png": path[:-4] + ".png", "w": int(float(vb[2])), "h": int(float(vb[3]))})
    json.dump(jobs, open(os.path.join(tmp, "jobs.json"), "w"))
    # Playwright gibt es hier nur für Node (vorinstalliertes Chromium)
    subprocess.run(["node", os.path.join(os.path.dirname(__file__), "shot.js"), os.path.join(tmp, "jobs.json")], check=True)
    for (name, (svgtext, w, folder)), job in zip(ITEMS.items(), jobs):
        img = Image.open(job["png"]).convert("RGBA")
        tw = TARGET.get(w, w)
        if img.width != tw:
            img = img.resize((tw, round(img.height * tw / img.width)), Image.LANCZOS)
        base = name.split("@")[0]
        if preview:
            os.makedirs(preview, exist_ok=True); img.save(os.path.join(preview, name + ".png"))
        out = os.path.join(RES, folder); os.makedirs(out, exist_ok=True)
        img.save(os.path.join(out, base + ".webp"), "WEBP", quality=88, method=6)


if __name__ == "__main__":
    main()
