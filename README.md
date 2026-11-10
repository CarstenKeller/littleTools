# littleTools

Sammlung kleiner Alltagswerkzeuge: Kompass, Wasserwaage (Kante), Libelle (Fläche), Zentimetermaß, Taschenlampe, Entfernungsmesser, Position mit Höhenmesser.

Android-App, entwickelt mit Kotlin + Jetpack Compose. Funktionsumfang und
Reihenfolge der Umsetzung stehen in [`plan.md`](plan.md).

## Wichtiger Hinweis zur Entwicklungsumgebung

Diese Codebasis wird (zumindest teilweise) in einer Cloud-Sandbox ohne
Android-SDK und ohne Netzwerkzugriff auf Googles Maven-Repository
entwickelt. Das heißt:

- **CI ist der einzige echte Compiler.** Jeder Push auf einen passenden
  Branch (siehe `.github/workflows/build-apk.yml`) löst einen echten
  Kompilier-Versuch aus. Ein Commit ohne grünen CI-Lauf ist unverifiziert.
- **Das Gerät ist der einzige echte Laufzeit-Test.** CI kann keine
  Kamera-, GPU- oder Sensor-Funktionen prüfen.
- Dependency-Versionen sind nach bestem Wissen gewählt, nicht gegen die
  tatsächlich verfügbaren Versionen geprüft. Ein Update-Vorschlag für
  AGP/Gradle beim ersten Android-Studio-Sync ist normal — annehmen.

## APK herunterladen

Alle Releases (automatisch pro Commit gebaut, Dateiname enthält den
Commit-Hash gegen Browser-Caching):
`https://github.com/CarstenKeller/littleTools/releases`

Alle Workflow-Läufe (Build-Log bei Fehlern):
`https://github.com/CarstenKeller/littleTools/actions/workflows/build-apk.yml`

Vor der Installation muss auf dem Smartphone „Installation aus unbekannten
Quellen" für den verwendeten Browser/Dateimanager erlaubt werden, da die
APK nicht aus dem Play Store kommt.
