package com.nexauren.paperya.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WidgetCustomizationScreen(
    selectedThemeId: Int,
    isPortuguese: Boolean,
    onBack: () -> Unit,
    onApply: (Int) -> Unit
) {
    var previewThemeId by remember(selectedThemeId) { mutableIntStateOf(selectedThemeId) }
    val selectedTheme = WidgetThemeCatalog.get(previewThemeId)
    val background = Color(0xFFF7F8FC)

    Surface(color = background, modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = uiText(isPortuguese, "Voltar", "Back"))
                }
                Text(
                    uiText(isPortuguese, "Personalizar widget", "Customize widget"),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { onApply(previewThemeId) }) {
                    Text(uiText(isPortuguese, "Adicionar", "Apply"), fontWeight = FontWeight.SemiBold)
                }
            }

            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    uiText(isPortuguese, "Pré-visualização", "Preview"),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(selectedTheme.cardColor)),
                    border = BorderStroke(1.dp, Color(selectedTheme.borderColor)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(Color(selectedTheme.headerStart), Color(selectedTheme.headerEnd))))
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                uiText(isPortuguese, "Notas Rápidas", "Quick Notes"),
                                modifier = Modifier.weight(1f),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                        }
                        Text(
                            uiText(isPortuguese, "A tua próxima ideia", "Your next idea"),
                            modifier = Modifier.padding(start = 8.dp, top = 13.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(selectedTheme.titleColor)
                        )
                        Text(
                            uiText(isPortuguese, "Guarda pensamentos, listas e pequenos planos num só lugar.", "Capture thoughts, lists and little plans in one place."),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            color = Color(selectedTheme.bodyColor),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NoteAdd, contentDescription = null, tint = Color(selectedTheme.accentColor), modifier = Modifier.size(23.dp))
                            Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color(selectedTheme.accentColor), modifier = Modifier.size(23.dp))
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(selectedTheme.accentColor), modifier = Modifier.size(23.dp))
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(selectedTheme.accentColor), modifier = Modifier.size(23.dp))
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(selectedTheme.accentColor), modifier = Modifier.size(23.dp))
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        uiText(isPortuguese, "Temas", "Themes"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        uiText(isPortuguese, "Escolhe um estilo para o widget do ecrã inicial.", "Choose a style for your home-screen widget."),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    WidgetThemeCatalog.themes.chunked(2).forEach { rowThemes ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowThemes.forEach { theme ->
                                val isSelected = previewThemeId == theme.id
                                Card(
                                    modifier = Modifier.weight(1f).clickable { previewThemeId = theme.id },
                                    shape = RoundedCornerShape(18.dp),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EE)
                                    ),
                                    colors = CardDefaults.cardColors(containerColor = Color(theme.cardColor)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 0.dp)
                                ) {
                                    Column {
                                        Box(
                                            Modifier.fillMaxWidth().height(36.dp)
                                                .background(Brush.horizontalGradient(listOf(Color(theme.headerStart), Color(theme.headerEnd))))
                                        )
                                        Row(
                                            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(Modifier.weight(1f)) {
                                                Text(if (isPortuguese) theme.namePortuguese else theme.name,
                                                    color = Color(theme.titleColor), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                                Text(
                                                    if (isSelected) uiText(isPortuguese, "Selecionado", "Selected") else uiText(isPortuguese, "Pré-visualizar", "Preview"),
                                                    color = Color(theme.mutedColor),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                            if (isSelected) Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                            if (rowThemes.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
