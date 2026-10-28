package com.nexauren.paperya.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun PaperyaTheme(paletteId: Int = 0, content: @Composable () -> Unit) {
    val selected = NotePalettes.getOrElse(paletteId) { NotePalettes.first() }
    val scheme = lightColorScheme(
        primary = selected.primary,
        onPrimary = Color.White,
        secondary = selected.secondary,
        onSecondary = selected.ink,
        background = selected.page,
        onBackground = selected.ink,
        surface = selected.card,
        onSurface = selected.ink,
        surfaceVariant = selected.secondary,
        onSurfaceVariant = selected.ink
    )
    MaterialTheme(colorScheme = scheme, typography = MaterialTheme.typography, content = content)
}
