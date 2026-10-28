package com.nexauren.paperya.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val ReadyLabels = listOf("Trabalho", "Estudo", "Pessoal", "Ideias", "Tarefas")

fun labelDisplayName(label: String, isPortuguese: Boolean): String {
    if (isPortuguese) return label
    return when (label) {
        "Trabalho" -> "Work"
        "Estudo" -> "Study"
        "Pessoal" -> "Personal"
        "Ideias" -> "Ideas"
        "Tarefas" -> "Tasks"
        else -> label
    }
}

@Composable
fun LabelManagerScreen(
    labels: List<String>,
    customLabels: Set<String>,
    noteCounts: Map<String, Int>,
    isPortuguese: Boolean,
    onBack: () -> Unit,
    onOpenLabel: (String) -> Unit,
    onCreateLabel: (String) -> Unit,
    onDeleteCustomLabel: (String) -> Unit
) {
    var newLabel by remember { mutableStateOf("") }
    val palette = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = uiText(isPortuguese, "Voltar", "Back"))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    uiText(isPortuguese, "As minhas etiquetas", "My labels"),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    uiText(isPortuguese, "Cada etiqueta tem a sua própria página.", "Every label has its own page."),
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.onSurfaceVariant
                )
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = palette.surface)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(uiText(isPortuguese, "Criar etiqueta", "Create a label"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = newLabel,
                    onValueChange = { newLabel = it.take(32) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    placeholder = { Text(uiText(isPortuguese, "Ex.: Receitas, Viagens…", "E.g. Recipes, Travel…")) }
                )
                Button(
                    onClick = {
                        val value = newLabel.trim()
                        if (value.isNotBlank() && labels.none { it.equals(value, ignoreCase = true) }) {
                            onCreateLabel(value)
                            newLabel = ""
                        }
                    },
                    enabled = newLabel.isNotBlank() && labels.none { it.equals(newLabel.trim(), ignoreCase = true) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("  " + uiText(isPortuguese, "Adicionar etiqueta", "Add label"))
                }
            }
        }

        Text(
            uiText(isPortuguese, "ETIQUETAS PRONTAS", "READY-MADE LABELS"),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = palette.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
            ReadyLabels.take(3).forEach { label ->
                Surface(
                    modifier = Modifier.weight(1f).clickable { onOpenLabel(label) },
                    color = palette.secondaryContainer,
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Column(Modifier.padding(vertical = 12.dp, horizontal = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalOffer, null, tint = palette.onSecondaryContainer)
                        Text(labelDisplayName(label, isPortuguese), style = MaterialTheme.typography.labelMedium, color = palette.onSecondaryContainer)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
            ReadyLabels.drop(3).forEach { label ->
                Surface(
                    modifier = Modifier.weight(1f).clickable { onOpenLabel(label) },
                    color = palette.secondaryContainer,
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Row(Modifier.padding(vertical = 10.dp, horizontal = 7.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalOffer, null, tint = palette.onSecondaryContainer, modifier = Modifier.size(16.dp))
                        Text("  " + labelDisplayName(label, isPortuguese), style = MaterialTheme.typography.labelMedium, color = palette.onSecondaryContainer)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        }

        Text(
            uiText(isPortuguese, "TODAS AS ETIQUETAS", "ALL LABELS"),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = palette.onSurfaceVariant
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(labels.distinct().sortedBy { labelDisplayName(it, isPortuguese).lowercase() }, key = { it }) { label ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenLabel(label) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = palette.surface)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 13.dp, end = 5.dp, top = 7.dp, bottom = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(39.dp).clip(CircleShape).background(palette.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalOffer, null, tint = palette.onSecondaryContainer)
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 11.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(labelDisplayName(label, isPortuguese), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                (noteCounts[label] ?: 0).toString() + " " + uiText(isPortuguese, "notas", "notes"),
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.onSurfaceVariant
                            )
                        }
                        if (customLabels.contains(label)) {
                            IconButton(onClick = { onDeleteCustomLabel(label) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = uiText(isPortuguese, "Eliminar etiqueta", "Delete label"))
                            }
                        }
                    }
                }
            }
        }
    }
}
