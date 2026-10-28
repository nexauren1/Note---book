package com.nexauren.paperya.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.json.JSONArray
import org.json.JSONObject

private data class InkPoint(val x: Float, val y: Float)
private data class InkStroke(val color: Int, val width: Float, val points: List<InkPoint>)

@Composable
fun DrawingPad(
    initialJson: String,
    paperId: Int,
    customImageUri: String?,
    isPortuguese: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val strokes = remember(initialJson) { mutableStateListOf<InkStroke>().apply { addAll(parseStrokes(initialJson)) } }
    val colors = listOf(
        0xFF242331.toInt(), 0xFFF4D82F.toInt(), 0xFF1E9FF3.toInt(),
        0xFFE9576B.toInt(), 0xFF36B86C.toInt(), 0xFF8C58DE.toInt(),
        0xFFEF68B7.toInt(), 0xFF9A6B44.toInt(), 0xFF16B7B1.toInt()
    )
    var activeColor by remember { mutableIntStateOf(colors.first()) }
    var strokeWidth by remember { mutableFloatStateOf(5.5f) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(uiText(isPortuguese, "Área de desenho", "Sketch pad"), style = MaterialTheme.typography.headlineSmall)
                        Text(uiText(isPortuguese, "Desenha, rabisca ou escreve à mão", "Draw, doodle or write by hand"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) }) {
                        Icon(Icons.Default.Undo, contentDescription = uiText(isPortuguese, "Desfazer último traço", "Undo last stroke"))
                    }
                    IconButton(onClick = { strokes.clear() }) {
                        Icon(Icons.Default.Delete, contentDescription = uiText(isPortuguese, "Limpar desenho", "Clear drawing"))
                    }
                    IconButton(onClick = { onSave(serializeStrokes(strokes)); onDismiss() }) {
                        Icon(Icons.Default.Check, contentDescription = uiText(isPortuguese, "Guardar desenho", "Save drawing"), tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Box(
                    Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(18.dp))
                ) {
                    PaperBackground(paperId, Modifier.fillMaxSize(), customImageUri)
                    Canvas(
                        Modifier.fillMaxSize().pointerInput(activeColor, strokeWidth) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val normalizedX = if (size.width > 0) (offset.x / size.width).coerceIn(0f, 1f) else 0f
                                    val normalizedY = if (size.height > 0) (offset.y / size.height).coerceIn(0f, 1f) else 0f
                                    strokes.add(InkStroke(activeColor, strokeWidth, listOf(InkPoint(normalizedX, normalizedY))))
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val normalizedX = if (size.width > 0) (change.position.x / size.width).coerceIn(0f, 1f) else 0f
                                    val normalizedY = if (size.height > 0) (change.position.y / size.height).coerceIn(0f, 1f) else 0f
                                    if (strokes.isNotEmpty()) {
                                        val last = strokes.last()
                                        strokes[strokes.lastIndex] = last.copy(points = last.points + InkPoint(normalizedX, normalizedY))
                                    }
                                }
                            )
                        }
                    ) {
                        strokes.forEach { stroke ->
                            val points = stroke.points
                            if (points.size == 1) {
                                drawCircle(
                                    color = Color(stroke.color),
                                    radius = stroke.width.dp.toPx() / 2f,
                                    center = Offset(points[0].x * size.width, points[0].y * size.height)
                                )
                            } else if (points.size > 1) {
                                val path = Path().apply {
                                    moveTo(points.first().x * size.width, points.first().y * size.height)
                                    points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) }
                                }
                                drawPath(
                                    path = path,
                                    color = Color(stroke.color),
                                    style = Stroke(width = stroke.width.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
                                )
                            }
                        }
                    }
                }

                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { colorValue ->
                        Box(
                            Modifier.size(if (activeColor == colorValue) 36.dp else 31.dp)
                                .clip(CircleShape)
                                .background(Color(colorValue))
                                .clickable { activeColor = colorValue },
                            contentAlignment = Alignment.Center
                        ) {
                            if (activeColor == colorValue) {
                                Box(Modifier.size(15.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.85f)))
                            }
                        }
                    }
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(uiText(isPortuguese, "Traço", "Pen"), style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = strokeWidth,
                        onValueChange = { strokeWidth = it },
                        valueRange = 2f..15f,
                        modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
                    )
                    Text(strokeWidth.toInt().toString(), style = MaterialTheme.typography.labelMedium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text(uiText(isPortuguese, "Cancelar", "Cancel")) }
                    Button(onClick = { onSave(serializeStrokes(strokes)); onDismiss() }) { Text(uiText(isPortuguese, "Guardar desenho", "Save sketch")) }
                }
            }
        }
    }
}

private fun serializeStrokes(strokes: List<InkStroke>): String {
    val rows = JSONArray()
    strokes.forEach { stroke ->
        val points = JSONArray()
        stroke.points.forEach { point ->
            points.put(JSONObject().put("x", point.x.toDouble()).put("y", point.y.toDouble()))
        }
        rows.put(
            JSONObject()
                .put("color", stroke.color)
                .put("width", stroke.width.toDouble())
                .put("points", points)
        )
    }
    return rows.toString()
}

private fun parseStrokes(json: String): List<InkStroke> = runCatching {
    val rows = JSONArray(json)
    buildList {
        for (index in 0 until rows.length()) {
            val row = rows.getJSONObject(index)
            val pointsJson = row.optJSONArray("points") ?: JSONArray()
            val points = buildList {
                for (pointIndex in 0 until pointsJson.length()) {
                    val point = pointsJson.getJSONObject(pointIndex)
                    add(InkPoint(point.optDouble("x").toFloat().coerceIn(0f, 1f), point.optDouble("y").toFloat().coerceIn(0f, 1f)))
                }
            }
            if (points.isNotEmpty()) add(InkStroke(row.optInt("color", 0xFF242331.toInt()), row.optDouble("width", 5.5).toFloat().coerceIn(1f, 20f), points))
        }
    }
}.getOrDefault(emptyList())
