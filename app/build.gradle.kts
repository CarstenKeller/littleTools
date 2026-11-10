plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    // TODO: an die eigene App anpassen (muss zum Java-Package unter
    // src/main/java/ passen).
    namespace = "io.github.carstenkeller.littletools"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.carstenkeller.littletools"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    // Fester, ins Repo eingecheckter Debug-Keystore statt des von AGP pro
    // Maschine automatisch generierten ~/.android/debug.keystore. Ohne das
    // würde jeder CI-Build (frischer Runner) mit einem anderen Schlüssel
    // signiert, und Android verweigert dann die Installation einer neuen
    // Version über eine bereits installierte ("App nicht installiert").
    // Erzeugen: siehe references/ci-pipeline.md in dieser Skill.
    signingConfigs {
        getByName("debug") {
            storeFile = file("../keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            // R8 + shrinkResources: entfernt v. a. die tausenden ungenutzten Icons aus
            // material-icons-extended. Gemessen (wahlscheiben-telefon, 2026-11-09):
            // Debug-APK 55,7 MB -> Release-APK 1,8 MB.
            isMinifyEnabled = true
            isShrinkResources = true
            // Zum Selbst-Installieren: mit dem festen Debug-Schlüssel signiert, damit
            // Updates über bereits installierte Debug-Builds möglich bleiben.
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.01.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // Für IconActionButton (Icon + Tooltip statt Text-Buttons):
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")

    // Alles Weitere kommt erst dazu, wenn die App es tatsächlich braucht -
    // nichts davon ist Teil des Grundgerüsts. Beispiele, je nach Bedarf
    // (siehe references/tech-stack.md für Details und Begründung):
    //
    // Navigation zwischen mehreren Screens:
    // implementation("androidx.navigation:navigation-compose:2.8.5")
    //
    // Lokale Datenbank:
    // implementation("androidx.room:room-runtime:2.6.1")
    // implementation("androidx.room:room-ktx:2.6.1")
    // ksp("androidx.room:room-compiler:2.6.1")
    //
    // Netzwerk/REST:
    // implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    //
    // Video/Kamera (siehe references/compose-patterns.md Abschnitte 4-7):
    // implementation("androidx.media3:media3-exoplayer:1.5.1")   // Video-Vorschau
    // implementation("androidx.media3:media3-transformer:1.5.1") // Video-Export
    // implementation("androidx.camera:camera-video:1.4.1")       // Kamera-Aufnahme

    debugImplementation("androidx.compose.ui:ui-tooling")

    // JVM-Unit-Tests (laufen in CI vor dem APK-Build)
    testImplementation("junit:junit:4.13.2")
}
