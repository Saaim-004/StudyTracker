package com.studytracker.app.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

/**
 * A fixed palette so colors stay visually distinct and consistent across the app.
 * The same subject/class always maps to the same color, every time it's used.
 */
private val SubjectPalette = listOf(
    Color(0xFF4C8DFF), // blue
    Color(0xFF32B27C), // green
    Color(0xFFFFA23A), // orange
    Color(0xFFB16CE0), // purple
    Color(0xFFFF6B81), // pink
    Color(0xFF2CC7C7), // teal
    Color(0xFFE0C23A), // gold
    Color(0xFF8D99AE)  // slate
)

/** Reserved for "too many things happening this day" — deliberately outside the normal palette. */
val CalendarOverloadColor = Color(0xFFD32F2F)

fun colorForSubject(subject: String): Color {
    val key = subject.trim().ifBlank { "General" }.lowercase()
    val index = abs(key.hashCode()) % SubjectPalette.size
    return SubjectPalette[index]
}
