package com.studytracker.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** Built-in color palettes the user can pick from, on top of light/dark. */
enum class ThemePreset(val label: String) {
    OCEAN("Ocean Blue"),
    LAVENDER("Lavender"),
    FOREST("Forest Green"),
    SUNSET("Sunset Orange"),
    MONOCHROME("Monochrome"),
    ROSE("Rose")
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val preset: ThemePreset = ThemePreset.OCEAN,
    val customBackgroundUri: String? = null,
    val backgroundOpacity: Float = 0.25f
)

class ThemePreferences(private val context: Context) {

    private object Keys {
        val MODE = stringPreferencesKey("theme_mode")
        val PRESET = stringPreferencesKey("theme_preset")
        val BG_URI = stringPreferencesKey("bg_uri")
        val BG_OPACITY = stringPreferencesKey("bg_opacity")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            preset = prefs[Keys.PRESET]?.let { runCatching { ThemePreset.valueOf(it) }.getOrNull() } ?: ThemePreset.OCEAN,
            customBackgroundUri = prefs[Keys.BG_URI],
            backgroundOpacity = prefs[Keys.BG_OPACITY]?.toFloatOrNull() ?: 0.25f
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.MODE] = mode.name }
    }

    suspend fun setPreset(preset: ThemePreset) {
        context.dataStore.edit { it[Keys.PRESET] = preset.name }
    }

    suspend fun setBackgroundUri(uri: String?) {
        context.dataStore.edit {
            if (uri == null) it.remove(Keys.BG_URI) else it[Keys.BG_URI] = uri
        }
    }

    suspend fun setBackgroundOpacity(opacity: Float) {
        context.dataStore.edit { it[Keys.BG_OPACITY] = opacity.toString() }
    }
}
