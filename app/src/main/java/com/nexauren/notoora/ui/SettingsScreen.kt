package com.nexauren.paperya.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    driveStatus: String,
    isPortuguese: Boolean,
    onBack: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDriveSync: () -> Unit,
    onDriveRestore: () -> Unit,
    onLanguageChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = uiText(isPortuguese, "Voltar", "Back"))
            }
            Column(Modifier.weight(1f)) {
                Text(uiText(isPortuguese, "Definições", "Settings"), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(uiText(isPortuguese, "Personaliza a tua experiência", "Personalize your experience"), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            }
        }

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, null, tint = colors.primary)
                    Text("  " + uiText(isPortuguese, "Idioma", "Language"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Text(uiText(isPortuguese, "Escolhe o idioma da interface.", "Choose the interface language."), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = isPortuguese, onClick = { onLanguageChange(true) }, label = { Text("Português") })
                    FilterChip(selected = !isPortuguese, onClick = { onLanguageChange(false) }, label = { Text("English") })
                }
            }
        }

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileDownload, null, tint = colors.primary)
                    Text("  " + uiText(isPortuguese, "Importar e exportar", "Import and export"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    uiText(isPortuguese, "Cria uma cópia JSON das tuas notas ou importa uma cópia anterior. Os desenhos e o texto são incluídos; os ficheiros anexados ainda não são transportados dentro da cópia.", "Export a JSON copy of your notes or import an earlier backup. Text and saved drawings are included; attached file bytes are not yet bundled."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.FileDownload, null)
                    Text("  " + uiText(isPortuguese, "Exportar notas", "Export notes"))
                }
                OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.FileUpload, null)
                    Text("  " + uiText(isPortuguese, "Importar cópia", "Import backup"))
                }
            }
        }

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, null, tint = colors.primary)
                    Text("  Google Drive", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    uiText(isPortuguese, "Usa a tua conta Google para combinar as alterações com uma cópia no Drive. A primeira utilização requer a configuração OAuth da aplicação.", "Merge local changes with a backup in your Google Drive. First use requires OAuth setup for this app."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
                Surface(color = colors.secondaryContainer, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(driveStatus, style = MaterialTheme.typography.bodySmall, color = colors.onSecondaryContainer, modifier = Modifier.padding(12.dp))
                }
                Button(onClick = onDriveSync, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CloudSync, null)
                    Text("  " + uiText(isPortuguese, "Sincronizar agora", "Sync now"))
                }
                OutlinedButton(onClick = onDriveRestore, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.FileDownload, null)
                    Text("  " + uiText(isPortuguese, "Recuperar do Drive", "Restore from Drive"))
                }
            }
        }

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, null, tint = colors.primary)
                    Text("  " + uiText(isPortuguese, "Privacidade primeiro", "Privacy first"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    uiText(isPortuguese, "As notas ficam no dispositivo por defeito. O acesso ao Drive é pedido apenas quando escolhes uma ação de sincronização.", "Notes stay on your device by default. Drive access is requested only when you choose a sync action."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.size(12.dp))
    }
}
