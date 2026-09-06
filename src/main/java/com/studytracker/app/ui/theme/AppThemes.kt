package com.studytracker.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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

    // This is the actual fix: previously only `primary` was tinted, so the screen's
    // background/surface never visibly changed when you picked a new swatch. Now we
    // blend a touch of the preset color into the background/surface too, so switching
    // themes actually recolors the whole screen, not just the buttons.
    return if (darkTheme) {
        val background = lerp(Color(0xFF121212), primary, 0.10f)
        val surface = lerp(Color(0xFF1C1C1C), primary, 0.14f)
        darkColorScheme(
            primary = primary,
            secondary = primary,
            background = background,
            surface = surface,
            surfaceVariant = lerp(surface, primary, 0.10f)
        )
    } else {
        val background = lerp(Color(0xFFFDFDFD), primary, 0.06f)
        val surface = lerp(Color(0xFFFFFFFF), primary, 0.08f)
        lightColorScheme(
            primary = primary,
            secondary = primary,
            background = background,
            surface = surface,
            surfaceVariant = lerp(surface, primary, 0.10f)
        )
    }
}
