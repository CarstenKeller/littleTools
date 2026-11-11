package io.github.carstenkeller.littletools.settings

import android.content.Context
import android.content.SharedPreferences

/** D-07: Darstellung Hell / Dunkel / System (Standard System). */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Kleine Einstellungen der App (SharedPreferences). */
class Prefs(context: Context) {
    private val sp: SharedPreferences =
        context.applicationContext.getSharedPreferences("littletools", Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() = runCatching { ThemeMode.valueOf(sp.getString("theme_mode", null) ?: "SYSTEM") }
            .getOrDefault(ThemeMode.SYSTEM)
        set(v) = sp.edit().putString("theme_mode", v.name).apply()

    /** Spalten im Startraster; 0 = automatisch (alle Kacheln ohne Scrollen, möglichst groß). */
    var gridColumns: Int
        get() = sp.getInt("grid_columns", 0)
        set(v) = sp.edit().putInt("grid_columns", v).apply()

    fun getFloat(key: String, default: Float): Float = sp.getFloat(key, default)
    fun putFloat(key: String, value: Float) = sp.edit().putFloat(key, value).apply()
}
