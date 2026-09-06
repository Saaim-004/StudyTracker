package com.studytracker.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.studytracker.app.data.ThemePreset

fun colorSchemeFor(preset: ThemePreset, darkTheme: Boolean): ColorScheme {
    val primary = when (preset) {
        ThemePreset.OCEAN -> if (darkTheme) OceanPrimaryDark else OceanPrimaryLight
        ThemePreset.LAVENDER -> if (darkTheme) LavenderPrimaryDark else LavenderPrimaryLight
        ThemePreset.FOREST -> if (darkTheme) ForestPrimaryDark else ForestPrimaryLight
        ThemePreset.SUNSET -> if (darkTheme) SunsetPrimaryDark else SunsetPrimaryLight
        ThemePreset.MONOCHROME -> if (darkTheme) MonoPrimaryDark else MonoPrimaryLight
        ThemePreset.ROSE -> if (darkTheme) RosePrimaryDark else RosePrimaryLight
    }
    return if (darkTheme) {
        darkColorScheme(primary = primary, secondary = primary)
    } else {
        lightColorScheme(primary = primary, secondary = primary)
    }
}
