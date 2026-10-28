package com.nexauren.paperya.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun PaperBackground(presetId: Int, modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    val preset = PaperPresets.getOrElse(presetId) { PaperPresets.first() }
    Box(modifier = modifier) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(preset.base)
            if (preset.pattern == PaperPattern.GRADIENT) {
                drawRect(brush = Brush.linearGradient(listOf(preset.base, preset.accent, preset.second)))
            }
            val step = 25.dp.toPx()
            when (preset.pattern) {
                PaperPattern.PLAIN, PaperPattern.GRADIENT -> Unit
                PaperPattern.RULED -> {
                    var y = step
                    while (y < size.height) {
                        drawLine(preset.accent.copy(alpha = 0.70f), Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                        y += step
                    }
                    drawLine(preset.second.copy(alpha = 0.55f), Offset(18.dp.toPx(), 0f), Offset(18.dp.toPx(), size.height), 1.dp.toPx())
                }
                PaperPattern.GRID -> {
                    var y = 0f
                    while (y < size.height) {
                        drawLine(preset.accent.copy(alpha = 0.52f), Offset(0f, y), Offset(size.width, y), 0.8.dp.toPx())
                        y += step
                    }
                    var x = 0f
                    while (x < size.width) {
                        drawLine(preset.accent.copy(alpha = 0.52f), Offset(x, 0f), Offset(x, size.height), 0.8.dp.toPx())
                        x += step
                    }
                }
                PaperPattern.DOTS -> {
                    var y = 10.dp.toPx()
                    while (y < size.height) {
                        var x = 10.dp.toPx()
                        while (x < size.width) {
                            drawCircle(preset.accent.copy(alpha = 0.55f), 1.4.dp.toPx(), Offset(x, y))
                            x += step
                        }
                        y += step
                    }
                }
                PaperPattern.GINGHAM -> {
                    var y = 0f
                    while (y < size.height) {
                        drawRect(preset.accent.copy(alpha = 0.10f), Offset(0f, y), Size(size.width, step / 2f))
                        y += step
                    }
                    var x = 0f
                    while (x < size.width) {
                        drawRect(preset.accent.copy(alpha = 0.10f), Offset(x, 0f), Size(step / 2f, size.height))
                        x += step
                    }
                }
                PaperPattern.CONFETTI -> {
                    val colors = listOf(preset.accent, preset.second, Color(0xFFF1A5B8), Color(0xFFFFC76F))
                    for (i in 0..36) {
                        val x = ((i * 73) % 997) / 997f * size.width
                        val y = ((i * 139) % 991) / 991f * size.height
                        drawCircle(colors[i % colors.size].copy(alpha = 0.45f), (3 + i % 4).dp.toPx(), Offset(x, y))
                    }
                }
                PaperPattern.LEAVES -> {
                    for (i in 0..17) {
                        val y = (i / 9f) * size.height + 16.dp.toPx()
                        val sideX = if (i % 2 == 0) 9.dp.toPx() else size.width - 9.dp.toPx()
                        drawCircle(preset.accent.copy(alpha = 0.36f), 7.dp.toPx(), Offset(sideX, y))
                        drawCircle(preset.second.copy(alpha = 0.28f), 4.dp.toPx(), Offset(sideX + if (i % 2 == 0) 5f else -5f, y + 9.dp.toPx()))
                    }
                    var y = 0f
                    while (y < size.height) {
                        drawLine(preset.accent.copy(alpha = 0.3f), Offset(0f, y), Offset(size.width, y), 0.5.dp.toPx())
                        y += 32.dp.toPx()
                    }
                }
                PaperPattern.STARS -> {
                    for (i in 0..44) {
                        val x = ((i * 97) % 991) / 991f * size.width
                        val y = ((i * 53) % 983) / 983f * size.height
                        drawCircle(preset.accent.copy(alpha = 0.52f), (1 + i % 3).dp.toPx(), Offset(x, y))
                        if (i % 6 == 0) {
                            drawLine(preset.second.copy(alpha = 0.6f), Offset(x - 4.dp.toPx(), y), Offset(x + 4.dp.toPx(), y), 1.dp.toPx())
                            drawLine(preset.second.copy(alpha = 0.6f), Offset(x, y - 4.dp.toPx()), Offset(x, y + 4.dp.toPx()), 1.dp.toPx())
                        }
                    }
                }
                PaperPattern.MARGINS -> {
                    var y = 18.dp.toPx()
                    while (y < size.height) {
                        drawLine(preset.accent.copy(alpha = 0.72f), Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                        y += 28.dp.toPx()
                    }
                    drawLine(preset.second.copy(alpha = 0.62f), Offset(26.dp.toPx(), 0f), Offset(26.dp.toPx(), size.height), 1.5.dp.toPx())
                    drawLine(preset.second.copy(alpha = 0.62f), Offset(size.width - 26.dp.toPx(), 0f), Offset(size.width - 26.dp.toPx(), size.height), 1.5.dp.toPx())
                }
            }
        }
        Box(Modifier.fillMaxSize()) { content() }
    }
}

@Composable
fun PaperSwatch(presetId: Int, modifier: Modifier = Modifier) {
    PaperBackground(presetId = presetId, modifier = modifier.clip(RoundedCornerShape(14.dp)))
}
