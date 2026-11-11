"""SVG-Bausteine der littleTools-Grafiken (selbst gezeichnet, keine fremden Bilder).

Alle Teile, die sich in der App bewegen (Kompassrose, Blasen, Zeiger), sind eigene Bilder;
ihre Lage im Bild ist in GEOMETRY festgehalten und muss mit dem Kotlin-Code übereinstimmen
(siehe app/.../ui/art/ArtGeometry.kt).
"""
import math

# Geometrie, die die App kennen muss (Einheiten = viewBox-Koordinaten des jeweiligen Bildes)
GEOMETRY = {
    "compass": {"size": 1000, "card_r": 392},
    "edge_level": {"w": 300, "h": 1500, "vial_x": 150, "vial_top": 540, "vial_bottom": 960, "bubble_len": 96, "vial_w": 112},
    "flat_level": {"size": 1000, "vial_r": 392, "bubble_r": 64},
    "altimeter": {"size": 1000},
}


def svg(w, h, body, defs=""):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {w} {h}" width="{w}" height="{h}">'
            f'<defs>{defs}</defs>{body}</svg>')


# ---------------------------------------------------------------- gemeinsame Definitionen
COMMON_DEFS = """
<linearGradient id="steel" x1="0" y1="0" x2="1" y2="1">
  <stop offset="0" stop-color="#f4f6f8"/><stop offset=".22" stop-color="#9aa3ab"/>
  <stop offset=".45" stop-color="#e9edf0"/><stop offset=".62" stop-color="#7d868e"/>
  <stop offset=".8" stop-color="#d5dade"/><stop offset="1" stop-color="#6b737a"/>
</linearGradient>
<linearGradient id="brass" x1="0" y1="0" x2="1" y2="1">
  <stop offset="0" stop-color="#fbe7a1"/><stop offset=".25" stop-color="#b8892f"/>
  <stop offset=".5" stop-color="#f3d27a"/><stop offset=".72" stop-color="#8c6420"/>
  <stop offset="1" stop-color="#e3bd63"/>
</linearGradient>
<linearGradient id="brassRev" x1="1" y1="1" x2="0" y2="0">
  <stop offset="0" stop-color="#fbe7a1"/><stop offset=".3" stop-color="#9c7226"/>
  <stop offset=".6" stop-color="#ecc870"/><stop offset="1" stop-color="#7a561b"/>
</linearGradient>
<radialGradient id="glassHi" cx=".38" cy=".25" r=".7">
  <stop offset="0" stop-color="#fff" stop-opacity=".55"/><stop offset=".45" stop-color="#fff" stop-opacity=".08"/>
  <stop offset="1" stop-color="#fff" stop-opacity="0"/>
</radialGradient>
<radialGradient id="innerShade" cx=".5" cy=".5" r=".5">
  <stop offset=".86" stop-color="#000" stop-opacity="0"/><stop offset="1" stop-color="#000" stop-opacity=".45"/>
</radialGradient>
<filter id="drop" x="-20%" y="-20%" width="140%" height="140%">
  <feGaussianBlur in="SourceAlpha" stdDeviation="14"/><feOffset dx="0" dy="14"/>
  <feComponentTransfer><feFuncA type="linear" slope=".55"/></feComponentTransfer>
  <feMerge><feMergeNode/><feMergeNode in="SourceGraphic"/></feMerge>
</filter>
<filter id="soft" x="-10%" y="-10%" width="120%" height="120%"><feGaussianBlur stdDeviation="3"/></filter>
<filter id="grain" x="0" y="0" width="100%" height="100%">
  <feTurbulence type="fractalNoise" baseFrequency=".9" numOctaves="2" seed="4" result="n"/>
  <feColorMatrix in="n" type="saturate" values="0"/>
  <feComponentTransfer><feFuncA type="linear" slope=".07"/></feComponentTransfer>
  <feComposite in2="SourceGraphic" operator="in"/>
  <feMerge><feMergeNode in="SourceGraphic"/><feMergeNode/></feMerge>
</filter>
"""


def polar(cx, cy, r, deg):
    a = math.radians(deg)
    return cx + r * math.sin(a), cy - r * math.cos(a)


# ---------------------------------------------------------------- Kompass
def compass_housing():
    c = 500
    body = f"""
<g filter="url(#drop)">
  <circle cx="{c}" cy="{c}" r="478" fill="url(#brass)"/>
  <circle cx="{c}" cy="{c}" r="455" fill="url(#brassRev)"/>
  <circle cx="{c}" cy="{c}" r="440" fill="url(#brass)"/>
</g>
<circle cx="{c}" cy="{c}" r="420" fill="#2a2620"/>
<circle cx="{c}" cy="{c}" r="420" fill="none" stroke="#5a4316" stroke-width="6"/>
"""
    # Riffelung am äußeren Rand
    ticks = "".join(
        f'<line x1="{polar(c, c, 462, d)[0]:.1f}" y1="{polar(c, c, 462, d)[1]:.1f}" '
        f'x2="{polar(c, c, 476, d)[0]:.1f}" y2="{polar(c, c, 476, d)[1]:.1f}" stroke="#6e4f17" stroke-opacity=".55" stroke-width="3"/>'
        for d in range(0, 360, 3))
    return svg(1000, 1000, body + ticks, COMMON_DEFS)


def compass_card(lang):
    c, r = 500, 392
    main = ["N", "O", "S", "W"] if lang == "de" else ["N", "E", "S", "W"]
    inter = ["NO", "SO", "SW", "NW"] if lang == "de" else ["NE", "SE", "SW", "NW"]
    defs = COMMON_DEFS + """
<radialGradient id="card" cx=".5" cy=".45" r=".55">
  <stop offset="0" stop-color="#fbf7ec"/><stop offset=".8" stop-color="#efe6cf"/><stop offset="1" stop-color="#d9cdae"/>
</radialGradient>
<linearGradient id="red" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#e53935"/><stop offset=".5" stop-color="#e53935"/><stop offset=".5" stop-color="#9e1b18"/><stop offset="1" stop-color="#9e1b18"/></linearGradient>
<linearGradient id="dark" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#37474f"/><stop offset=".5" stop-color="#37474f"/><stop offset=".5" stop-color="#1c262b"/><stop offset="1" stop-color="#1c262b"/></linearGradient>
<linearGradient id="gold" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#d9b25a"/><stop offset=".5" stop-color="#d9b25a"/><stop offset=".5" stop-color="#9a7428"/><stop offset="1" stop-color="#9a7428"/></linearGradient>
"""
    parts = [f'<circle cx="{c}" cy="{c}" r="{r}" fill="url(#card)" filter="url(#grain)"/>',
             f'<circle cx="{c}" cy="{c}" r="{r - 70}" fill="none" stroke="#3b3326" stroke-width="2"/>',
             f'<circle cx="{c}" cy="{c}" r="{r - 6}" fill="none" stroke="#3b3326" stroke-width="3"/>']
    for d in range(0, 360, 2):
        L = 34 if d % 10 == 0 else 18
        w = 3.2 if d % 10 == 0 else 1.6
        x1, y1 = polar(c, c, r - 8, d); x2, y2 = polar(c, c, r - 8 - L, d)
        col = "#c62828" if d == 0 else "#2b261d"
        parts.append(f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" stroke="{col}" stroke-width="{w}"/>')
    for d in range(30, 360, 30):
        if d % 90 == 0:
            continue
        x, y = polar(c, c, r - 60, d)
        parts.append(f'<text x="{x:.1f}" y="{y:.1f}" transform="rotate({d} {x:.1f} {y:.1f})" font-family="DejaVu Sans, sans-serif" '
                     f'font-size="26" font-weight="bold" fill="#2b261d" text-anchor="middle" dominant-baseline="central">{d}</text>')
    # Rose: 8-strahliger Stern
    def ray(deg, length, width, fill):
        tip = polar(c, c, length, deg); l = polar(c, c, width, deg - 90); rr = polar(c, c, width, deg + 90)
        return f'<polygon points="{tip[0]:.1f},{tip[1]:.1f} {l[0]:.1f},{l[1]:.1f} {c},{c} {rr[0]:.1f},{rr[1]:.1f}" fill="{fill}"/>'
    rose = []
    for d in (45, 135, 225, 315):
        rose.append(f'<g transform="rotate({d} {c} {c})">{ray(0, 200, 30, "url(#gold)")}</g>')
    for i, d in enumerate((0, 90, 180, 270)):
        rose.append(f'<g transform="rotate({d} {c} {c})">{ray(0, 300, 42, "url(#red)" if i == 0 else "url(#dark)")}</g>')
    parts.append('<g opacity=".95">' + "".join(rose) + "</g>")
    parts.append(f'<circle cx="{c}" cy="{c}" r="54" fill="none" stroke="#3b3326" stroke-width="2"/>')
    for i, d in enumerate((0, 90, 180, 270)):
        x, y = polar(c, c, r - 112, d)
        parts.append(f'<text x="{x:.1f}" y="{y:.1f}" transform="rotate({d} {x:.1f} {y:.1f})" font-family="DejaVu Serif, serif" font-size="70" '
                     f'font-weight="bold" fill="{"#c62828" if i == 0 else "#1f1a12"}" text-anchor="middle" dominant-baseline="central">{main[i]}</text>')
    for i, d in enumerate((45, 135, 225, 315)):
        x, y = polar(c, c, r - 112, d)
        parts.append(f'<text x="{x:.1f}" y="{y:.1f}" transform="rotate({d} {x:.1f} {y:.1f})" font-family="DejaVu Serif, serif" font-size="34" '
                     f'font-weight="bold" fill="#4a4030" text-anchor="middle" dominant-baseline="central">{inter[i]}</text>')
    return svg(1000, 1000, "".join(parts), defs)


def glass_overlay(lubber=True, size=1000, r=420):
    c = size / 2
    lub = ""
    if lubber:
        lub = (f'<polygon points="{c},{c - r + 46} {c - 16},{c - r + 2} {c + 16},{c - r + 2}" fill="#d32f2f" stroke="#7f1010" stroke-width="2"/>'
               f'<line x1="{c}" y1="{c - r + 2}" x2="{c}" y2="{c - r + 60}" stroke="#d32f2f" stroke-width="4"/>')
    body = f"""
<circle cx="{c}" cy="{c}" r="{r}" fill="url(#innerShade)"/>
{lub}
<circle cx="{c}" cy="{c}" r="34" fill="url(#brass)" filter="url(#drop)"/>
<circle cx="{c}" cy="{c}" r="14" fill="url(#brassRev)"/>
<ellipse cx="{c - 90}" cy="{c - 170}" rx="{r * .78}" ry="{r * .45}" transform="rotate(-28 {c - 90} {c - 170})" fill="url(#glassHi)"/>
<path d="M {c - r * .80} {c + r * .35} A {r * .88} {r * .88} 0 0 0 {c + r * .35} {c + r * .80}" fill="none" stroke="#fff" stroke-opacity=".18" stroke-width="10" stroke-linecap="round"/>
"""
    return svg(size, size, body, COMMON_DEFS)


# ---------------------------------------------------------------- Wasserwaage (Kante), senkrecht
def edge_level_bar():
    g = GEOMETRY["edge_level"]; W, H = g["w"], g["h"]
    vx, vt, vb, vw = g["vial_x"], g["vial_top"], g["vial_bottom"], g["vial_w"]
    defs = COMMON_DEFS + """
<linearGradient id="alu" x1="0" y1="0" x2="1" y2="0">
  <stop offset="0" stop-color="#8d969d"/><stop offset=".08" stop-color="#e8ecef"/><stop offset=".2" stop-color="#c3c9ce"/>
  <stop offset=".5" stop-color="#dfe3e6"/><stop offset=".8" stop-color="#b3bac0"/><stop offset=".93" stop-color="#eef1f3"/><stop offset="1" stop-color="#7c858c"/>
</linearGradient>
<linearGradient id="cap" x1="0" y1="0" x2="1" y2="0">
  <stop offset="0" stop-color="#7f1d12"/><stop offset=".3" stop-color="#e2492f"/><stop offset=".6" stop-color="#c4341f"/><stop offset="1" stop-color="#6d170e"/>
</linearGradient>
<linearGradient id="liquid" x1="0" y1="0" x2="1" y2="0">
  <stop offset="0" stop-color="#7a9b12"/><stop offset=".35" stop-color="#d6ef4a"/><stop offset=".6" stop-color="#c3e035"/><stop offset="1" stop-color="#6b8a0e"/>
</linearGradient>
<linearGradient id="window" x1="0" y1="0" x2="1" y2="0">
  <stop offset="0" stop-color="#3a3f43"/><stop offset=".5" stop-color="#5d646a"/><stop offset="1" stop-color="#2d3135"/>
</linearGradient>
<filter id="brush" x="0" y="0" width="100%" height="100%">
  <feTurbulence type="fractalNoise" baseFrequency="1.2 .004" numOctaves="2" seed="7" result="n"/>
  <feColorMatrix in="n" type="saturate" values="0"/><feComponentTransfer><feFuncA type="linear" slope=".18"/></feComponentTransfer>
  <feComposite in2="SourceGraphic" operator="in"/><feMerge><feMergeNode in="SourceGraphic"/><feMergeNode/></feMerge>
</filter>
"""
    m = vt + (vb - vt) / 2; bl = g["bubble_len"]
    body = f"""
<rect x="20" y="10" width="{W - 40}" height="{H - 20}" rx="18" fill="url(#alu)" filter="url(#brush)"/>
<rect x="14" y="10" width="{W - 28}" height="110" rx="22" fill="url(#cap)"/>
<rect x="14" y="{H - 120}" width="{W - 28}" height="110" rx="22" fill="url(#cap)"/>
<rect x="24" y="30" width="{W - 48}" height="10" rx="5" fill="#fff" fill-opacity=".25"/>
<rect x="{vx - vw / 2 - 26}" y="{vt - 36}" width="{vw + 52}" height="{vb - vt + 72}" rx="40" fill="url(#window)"/>
<rect x="{vx - vw / 2 - 26}" y="{vt - 36}" width="{vw + 52}" height="{vb - vt + 72}" rx="40" fill="none" stroke="#fff" stroke-opacity=".35" stroke-width="3"/>
<rect x="{vx - vw / 2}" y="{vt}" width="{vw}" height="{vb - vt}" rx="{vw / 2}" fill="url(#liquid)"/>
<line x1="{vx - vw / 2}" y1="{m - bl / 2 - 6}" x2="{vx + vw / 2}" y2="{m - bl / 2 - 6}" stroke="#111" stroke-width="7"/>
<line x1="{vx - vw / 2}" y1="{m + bl / 2 + 6}" x2="{vx + vw / 2}" y2="{m + bl / 2 + 6}" stroke="#111" stroke-width="7"/>
"""
    # Millimeterskala am Rand (Dekor)
    for i, y in enumerate(range(160, H - 160, 10)):
        if vt - 50 < y < vb + 50:
            continue
        L = 30 if i % 10 == 0 else (20 if i % 5 == 0 else 12)
        body += f'<line x1="22" y1="{y}" x2="{22 + L}" y2="{y}" stroke="#2c3135" stroke-width="2"/>'
    return svg(W, H, body, defs)


def edge_level_glass():
    """Glanz über dem Röhrchen (über der Blase gezeichnet)."""
    g = GEOMETRY["edge_level"]; W, H = g["w"], g["h"]
    vx, vt, vb, vw = g["vial_x"], g["vial_top"], g["vial_bottom"], g["vial_w"]
    defs = """<linearGradient id="vialGlass" x1="0" y1="0" x2="1" y2="0">
  <stop offset="0" stop-color="#000" stop-opacity=".25"/><stop offset=".2" stop-color="#fff" stop-opacity=".55"/>
  <stop offset=".32" stop-color="#fff" stop-opacity="0"/><stop offset=".82" stop-color="#fff" stop-opacity=".15"/><stop offset="1" stop-color="#000" stop-opacity=".3"/></linearGradient>"""
    body = f'<rect x="{vx - vw / 2}" y="{vt}" width="{vw}" height="{vb - vt}" rx="{vw / 2}" fill="url(#vialGlass)"/>'
    return svg(W, H, body, defs)


def bubble(size=200, elong=1.0):
    c = size / 2; rx = size * .44; ry = size * .44 * elong
    defs = """<radialGradient id="b" cx=".42" cy=".38" r=".65">
  <stop offset="0" stop-color="#ffffff" stop-opacity=".95"/><stop offset=".5" stop-color="#f6ffd0" stop-opacity=".75"/>
  <stop offset=".85" stop-color="#9fbf20" stop-opacity=".55"/><stop offset="1" stop-color="#4d6608" stop-opacity=".9"/></radialGradient>"""
    body = (f'<ellipse cx="{c}" cy="{c}" rx="{rx}" ry="{ry}" fill="url(#b)"/>'
            f'<ellipse cx="{c - rx * .3}" cy="{c - ry * .38}" rx="{rx * .32}" ry="{ry * .18}" fill="#fff" fill-opacity=".9"/>')
    return svg(size, size, body, defs)


# ---------------------------------------------------------------- Dosenlibelle (flach)
def flat_level_base():
    c = 500; r = GEOMETRY["flat_level"]["vial_r"]
    defs = COMMON_DEFS + """<radialGradient id="liq" cx=".5" cy=".45" r=".55">
  <stop offset="0" stop-color="#e4f75c"/><stop offset=".7" stop-color="#bcd832"/><stop offset="1" stop-color="#6d8c0b"/></radialGradient>"""
    body = f"""
<g filter="url(#drop)"><circle cx="{c}" cy="{c}" r="478" fill="url(#steel)"/></g>
<circle cx="{c}" cy="{c}" r="450" fill="url(#steel)" transform="rotate(180 {c} {c})"/>
<circle cx="{c}" cy="{c}" r="{r + 14}" fill="#2d3236"/>
<circle cx="{c}" cy="{c}" r="{r}" fill="url(#liq)"/>
"""
    return svg(1000, 1000, body, COMMON_DEFS + defs)


def flat_level_glass():
    c = 500; r = GEOMETRY["flat_level"]["vial_r"]; br = GEOMETRY["flat_level"]["bubble_r"]
    body = f"""
<circle cx="{c}" cy="{c}" r="{br + 12}" fill="none" stroke="#151515" stroke-width="6"/>
<circle cx="{c}" cy="{c}" r="{r * .5}" fill="none" stroke="#151515" stroke-opacity=".7" stroke-width="3"/>
<line x1="{c - r}" y1="{c}" x2="{c - br - 24}" y2="{c}" stroke="#151515" stroke-opacity=".6" stroke-width="2"/>
<line x1="{c + br + 24}" y1="{c}" x2="{c + r}" y2="{c}" stroke="#151515" stroke-opacity=".6" stroke-width="2"/>
<line x1="{c}" y1="{c - r}" x2="{c}" y2="{c - br - 24}" stroke="#151515" stroke-opacity=".6" stroke-width="2"/>
<line x1="{c}" y1="{c + br + 24}" x2="{c}" y2="{c + r}" stroke="#151515" stroke-opacity=".6" stroke-width="2"/>
<circle cx="{c}" cy="{c}" r="{r}" fill="url(#innerShade)"/>
<ellipse cx="{c - 80}" cy="{c - 160}" rx="{r * .75}" ry="{r * .42}" transform="rotate(-28 {c - 80} {c - 160})" fill="url(#glassHi)"/>
"""
    return svg(1000, 1000, body, COMMON_DEFS)


# ---------------------------------------------------------------- Taschenlampe
def flashlight(on):
    W, H = 600, 1400
    defs = COMMON_DEFS + """
<linearGradient id="body" x1="0" y1="0" x2="1" y2="0">
  <stop offset="0" stop-color="#0d0f11"/><stop offset=".18" stop-color="#4a5157"/><stop offset=".3" stop-color="#22272b"/>
  <stop offset=".7" stop-color="#15181b"/><stop offset=".85" stop-color="#3a4046"/><stop offset="1" stop-color="#0a0b0c"/>
</linearGradient>
<linearGradient id="head" x1="0" y1="0" x2="1" y2="0">
  <stop offset="0" stop-color="#121417"/><stop offset=".2" stop-color="#5c646b"/><stop offset=".35" stop-color="#262b30"/>
  <stop offset=".75" stop-color="#181b1f"/><stop offset=".9" stop-color="#4b5258"/><stop offset="1" stop-color="#0e1012"/>
</linearGradient>
<pattern id="knurl" width="16" height="16" patternUnits="userSpaceOnUse" patternTransform="rotate(45)">
  <rect width="16" height="16" fill="none"/><rect width="8" height="16" fill="#000" fill-opacity=".45"/>
</pattern>
<radialGradient id="lensOff" cx=".5" cy=".5" r=".5">
  <stop offset="0" stop-color="#d9dde0"/><stop offset=".25" stop-color="#9aa1a7"/><stop offset=".5" stop-color="#e7ebee"/>
  <stop offset=".75" stop-color="#7d858b"/><stop offset="1" stop-color="#c8cdd1"/></radialGradient>
<radialGradient id="lensOn" cx=".5" cy=".5" r=".5">
  <stop offset="0" stop-color="#ffffff"/><stop offset=".35" stop-color="#fffbe6"/><stop offset=".7" stop-color="#ffe9a3"/><stop offset="1" stop-color="#ffd36b"/></radialGradient>
<radialGradient id="glow" cx=".5" cy=".85" r=".6">
  <stop offset="0" stop-color="#fff6cc" stop-opacity=".95"/><stop offset=".35" stop-color="#ffe9a0" stop-opacity=".45"/><stop offset="1" stop-color="#ffe9a0" stop-opacity="0"/></radialGradient>
<radialGradient id="btn" cx=".4" cy=".35" r=".7">
  <stop offset="0" stop-color="#ff8a65"/><stop offset=".6" stop-color="#d84315"/><stop offset="1" stop-color="#7a2408"/></radialGradient>
<radialGradient id="btnOn" cx=".4" cy=".35" r=".7">
  <stop offset="0" stop-color="#b9f6ca"/><stop offset=".6" stop-color="#2e7d32"/><stop offset="1" stop-color="#103d13"/></radialGradient>
"""
    cx = W / 2
    glow = f'<ellipse cx="{cx}" cy="250" rx="300" ry="260" fill="url(#glow)"/>' if on else ""
    body = f"""
{glow}
<g filter="url(#drop)">
  <path d="M {cx - 190} 330 L {cx + 190} 330 L {cx + 150} 560 L {cx - 150} 560 Z" fill="url(#head)"/>
  <rect x="{cx - 200}" y="300" width="400" height="56" rx="14" fill="url(#head)"/>
  <rect x="{cx - 112}" y="550" width="224" height="760" rx="20" fill="url(#body)"/>
  <rect x="{cx - 112}" y="760" width="224" height="380" fill="url(#knurl)"/>
  <rect x="{cx - 120}" y="1290" width="240" height="70" rx="22" fill="url(#head)"/>
</g>
<ellipse cx="{cx}" cy="306" rx="190" ry="30" fill="url({'#lensOn' if on else '#lensOff'})"/>
<ellipse cx="{cx}" cy="306" rx="190" ry="30" fill="none" stroke="#0c0d0f" stroke-width="6"/>
<ellipse cx="{cx - 60}" cy="298" rx="60" ry="8" fill="#fff" fill-opacity=".6"/>
<rect x="{cx - 46}" y="640" width="92" height="92" rx="46" fill="#0b0c0d"/>
<circle cx="{cx}" cy="686" r="38" fill="url({'#btnOn' if on else '#btn'})"/>
<rect x="{cx - 96}" y="600" width="10" height="680" rx="5" fill="#fff" fill-opacity=".12"/>
"""
    return svg(W, H, body, defs)


# ---------------------------------------------------------------- Höhenmesser (Uhr-Stil)
def altimeter_dial():
    c = 500; r = 400
    defs = COMMON_DEFS + """<radialGradient id="face" cx=".5" cy=".42" r=".6">
  <stop offset="0" stop-color="#2b3036"/><stop offset=".8" stop-color="#15181b"/><stop offset="1" stop-color="#0a0b0d"/></radialGradient>"""
    parts = [f'<g filter="url(#drop)"><circle cx="{c}" cy="{c}" r="478" fill="url(#steel)"/></g>',
             f'<circle cx="{c}" cy="{c}" r="452" fill="url(#steel)" transform="rotate(180 {c} {c})"/>',
             f'<circle cx="{c}" cy="{c}" r="{r + 20}" fill="#0b0c0e"/>',
             f'<circle cx="{c}" cy="{c}" r="{r}" fill="url(#face)" filter="url(#grain)"/>']
    for i in range(50):
        d = i * 7.2
        L = 40 if i % 5 == 0 else 18
        w = 6 if i % 5 == 0 else 2.5
        x1, y1 = polar(c, c, r - 10, d); x2, y2 = polar(c, c, r - 10 - L, d)
        parts.append(f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" stroke="#f1f1ea" stroke-width="{w}"/>')
    for n in range(10):
        x, y = polar(c, c, r - 100, n * 36)
        parts.append(f'<text x="{x:.1f}" y="{y:.1f}" font-family="DejaVu Sans, sans-serif" font-size="78" font-weight="bold" '
                     f'fill="#f1f1ea" text-anchor="middle" dominant-baseline="central">{n}</text>')
    parts.append(f'<text x="{c}" y="{c - 150}" font-family="DejaVu Sans, sans-serif" font-size="30" fill="#c9c9bd" text-anchor="middle" letter-spacing="4">ALT</text>')
    parts.append(f'<text x="{c}" y="{c + 160}" font-family="DejaVu Sans, sans-serif" font-size="30" fill="#c9c9bd" text-anchor="middle">×100 m</text>')
    return svg(1000, 1000, "".join(parts), defs)


def needle(kind):
    """Zeiger senkrecht nach oben, Drehpunkt in der Bildmitte (1000×1000)."""
    c = 500
    if kind == "long":
        body = (f'<polygon points="{c},{c - 380} {c - 14},{c - 40} {c - 10},{c + 80} {c + 10},{c + 80} {c + 14},{c - 40}" fill="#f5f5ef" filter="url(#drop)"/>'
                f'<polygon points="{c},{c - 380} {c - 6},{c - 300} {c + 6},{c - 300}" fill="#ff7043"/>')
    else:
        body = f'<polygon points="{c},{c - 230} {c - 30},{c - 60} {c - 16},{c + 50} {c + 16},{c + 50} {c + 30},{c - 60}" fill="#ff8a3d" filter="url(#drop)"/>'
    body += f'<circle cx="{c}" cy="{c}" r="30" fill="url(#steel)"/>' if kind == "long" else ""
    return svg(1000, 1000, body, COMMON_DEFS)


# ---------------------------------------------------------------- Kacheln (Szenen, 512×512)
TILE_BG = """<radialGradient id="studio" cx=".5" cy=".3" r=".9">
  <stop offset="0" stop-color="#46525c"/><stop offset=".6" stop-color="#262d33"/><stop offset="1" stop-color="#14181b"/></radialGradient>"""


def _inner(svgtext):
    """Inhalt (defs + body) eines svg()-Strings zum Einbetten."""
    start = svgtext.index(">") + 1
    return svgtext[start:-len("</svg>")]


def nested(svgtext, x, y, w, h, extra=""):
    vb = svgtext.split('viewBox="')[1].split('"')[0]
    return f'<svg x="{x}" y="{y}" width="{w}" height="{h}" viewBox="{vb}" {extra}>{_inner(svgtext)}</svg>'


def tile(content, extra_defs=""):
    return svg(512, 512, f'<rect width="512" height="512" fill="url(#studio)"/>{content}', TILE_BG + extra_defs)


def tile_compass():
    card = compass_card("de")
    inner = (nested(compass_housing(), 46, 30, 420, 420)
             + f'<g transform="rotate(-24 256 240)">{nested(card, 46, 30, 420, 420)}</g>'
             + nested(glass_overlay(), 46, 30, 420, 420))
    return tile(inner)


def tile_edge_level():
    bar = edge_level_bar(); g = GEOMETRY["edge_level"]
    b = bubble(200, 1.0)
    s = 440 / g["h"]  # Maßstab
    bx = 256 - g["w"] * s / 2; by = 240 - g["h"] * s / 2
    bub_sz = g["vial_w"] * s * 1.0
    m = by + (g["vial_top"] + (g["vial_bottom"] - g["vial_top"]) / 2) * s
    inner = (f'<g transform="rotate(-70 256 240)">'
             + nested(bar, bx, by, g["w"] * s, g["h"] * s)
             + nested(b, bx + g["vial_x"] * s - bub_sz / 2, m - bub_sz / 2 - 6, bub_sz, bub_sz)
             + nested(edge_level_glass(), bx, by, g["w"] * s, g["h"] * s) + "</g>")
    return tile(inner)


def tile_flat_level():
    inner = (nested(flat_level_base(), 66, 40, 380, 380)
             + nested(bubble(200), 66 + 190 - 34 + 40, 40 + 190 - 34 - 30, 68, 68)
             + nested(flat_level_glass(), 66, 40, 380, 380))
    return tile(inner)


def folding_rule_segment(x, y, length, angle, start_cm, ppm=2.2):
    """Ein Glied eines gelben Zollstocks (Breite 46), um (x,y) gedreht. ppm = Pixel pro mm."""
    ticks = []
    n_mm = int(length / ppm)
    for mm in range(0, n_mm + 1):
        X = mm * ppm
        L = 15 if mm % 10 == 0 else (10 if mm % 5 == 0 else 5)
        ticks.append(f'<line x1="{X:.1f}" y1="0" x2="{X:.1f}" y2="{L}" stroke="#1a1a1a" stroke-width="{1.4 if mm % 10 == 0 else .8}"/>')
        if mm % 10 == 0 and 0 < mm < n_mm - 3:
            ticks.append(f'<text x="{X:.1f}" y="33" font-family="DejaVu Sans, sans-serif" font-size="14" font-weight="bold" fill="#1a1a1a" text-anchor="middle">{start_cm + mm // 10}</text>')
    return (f'<g transform="translate({x} {y}) rotate({angle})" filter="url(#drop)">'
            f'<rect x="-4" y="-2" width="{length + 8}" height="46" rx="5" fill="url(#ruleYellow)"/>'
            f'{"".join(ticks)}<circle cx="{length}" cy="22" r="6" fill="url(#steel)"/></g>')


def tile_ruler():
    defs = COMMON_DEFS + """<linearGradient id="ruleYellow" x1="0" y1="0" x2="0" y2="1">
  <stop offset="0" stop-color="#fff2a8"/><stop offset=".15" stop-color="#f7d43a"/><stop offset=".85" stop-color="#e9bb12"/><stop offset="1" stop-color="#a77d05"/></linearGradient>"""
    inner = (folding_rule_segment(80, 410, 264, -14, 0)
             + folding_rule_segment(336, 346, 264, -76, 12))
    return tile(inner, defs)


def tile_flashlight():
    inner = f'<g transform="rotate(38 256 256)">{nested(flashlight(True), 176, 20, 160, 373)}</g>'
    return tile(inner)


def tile_rangefinder():
    defs = """<linearGradient id="sky" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#6fa8dc"/><stop offset="1" stop-color="#d7ecff"/></linearGradient>
<linearGradient id="field" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#7cb342"/><stop offset="1" stop-color="#33691e"/></linearGradient>
<radialGradient id="vig" cx=".5" cy=".5" r=".5"><stop offset=".72" stop-color="#000" stop-opacity="0"/><stop offset="1" stop-color="#000" stop-opacity=".85"/></radialGradient>
<clipPath id="scope"><circle cx="256" cy="220" r="180"/></clipPath>"""
    view = """<g clip-path="url(#scope)">
<rect x="0" y="0" width="512" height="512" fill="url(#sky)"/>
<path d="M0 230 Q 120 180 250 220 T 512 210 V 512 H0 Z" fill="#8bb85a"/>
<path d="M0 270 Q 200 240 512 280 V 512 H0 Z" fill="url(#field)"/>
<rect x="300" y="176" width="10" height="70" fill="#5d4037"/><circle cx="305" cy="170" r="34" fill="#2e7d32"/><circle cx="292" cy="180" r="22" fill="#388e3c"/>
<circle cx="256" cy="220" r="180" fill="url(#vig)"/>
<g fill="none" stroke="#e53935" stroke-width="4">
<path d="M 226 194 h -16 v 16 M 286 194 h 16 v 16 M 226 246 h -16 v -16 M 286 246 h 16 v -16"/></g>
<circle cx="256" cy="220" r="3" fill="#e53935"/>
<text x="256" y="300" font-family="DejaVu Sans Mono, monospace" font-size="38" font-weight="bold" fill="#ff5252" text-anchor="middle">42.5 m</text>
</g>
<circle cx="256" cy="220" r="184" fill="none" stroke="#0b0c0d" stroke-width="16"/>
<circle cx="256" cy="220" r="194" fill="none" stroke="#3b4248" stroke-width="6"/>"""
    return tile(view, defs)


def tile_position():
    defs = """<linearGradient id="paper" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#f3ecd8"/><stop offset="1" stop-color="#ddd1b0"/></linearGradient>
<radialGradient id="pin" cx=".35" cy=".3" r=".8"><stop offset="0" stop-color="#ff8a80"/><stop offset=".5" stop-color="#e53935"/><stop offset="1" stop-color="#8e1612"/></radialGradient>""" + COMMON_DEFS
    contours = "".join(
        f'<path d="M {40 + k * 6} {420 - k * 18} C {150 + k * 10} {300 - k * 22}, {300 - k * 8} {440 - k * 30}, {470 - k * 6} {250 - k * 14}" '
        f'fill="none" stroke="#a1887f" stroke-width="{2.5 if k % 4 == 0 else 1.2}"/>' for k in range(12))
    rings = "".join(f'<ellipse cx="350" cy="190" rx="{110 - k * 18}" ry="{70 - k * 11}" fill="none" stroke="#a1887f" stroke-width="{2.5 if k % 2 == 0 else 1.2}"/>' for k in range(6))
    inner = f"""<g transform="rotate(-8 256 256)" filter="url(#drop)"><rect x="56" y="60" width="400" height="380" rx="6" fill="url(#paper)"/>
<g clip-path="none">{contours}{rings}</g>
<path d="M60 330 C 160 300 220 360 300 320 S 420 280 452 300" stroke="#64b5f6" stroke-width="7" fill="none"/>
<path d="M 120 60 L 170 440" stroke="#ffffff" stroke-width="9"/><path d="M 120 60 L 170 440" stroke="#bdbdbd" stroke-width="2"/></g>
<g filter="url(#drop)"><path d="M 256 330 C 230 280 196 250 196 210 A 60 60 0 1 1 316 210 C 316 250 282 280 256 330 Z" fill="url(#pin)"/>
<circle cx="256" cy="208" r="22" fill="#fff" fill-opacity=".9"/></g>
<ellipse cx="256" cy="336" rx="26" ry="7" fill="#000" fill-opacity=".3"/>"""
    return tile(inner, defs)
