package com.nexauren.notoora.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    driveStatus: String,
    appThemeId: Int,
    onBack: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDriveSync: () -> Unit,
    onDriveRestore: () -> Unit,
    onThemeChange: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileDownload, null, tint = MaterialTheme.colorScheme.primary)
                    Text("Portable backup", modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Text("Export a Notoora JSON backup or import one later. Import merges notes and keeps the newest version of each note.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.FileDownload, null)
                    Text("  Export notes")
                }
                OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.FileUpload, null)
                    Text("  Import backup")
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, null, tint = MaterialTheme.colorScheme.primary)
                    Text("Google Drive", modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Text("Use your Google account to merge the latest changes and keep one Notoora backup file in Drive.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(driveStatus, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Button(onClick = onDriveSync, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CloudSync, null)
                    Text("  Sync now")
                }
                OutlinedButton(onClick = onDriveRestore, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.FileDownload, null)
                    Text("  Merge from Google Drive")
                }
                Text("Drive setup requires the Drive API and OAuth consent configuration for this app before first use.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary)
                    Text("App accent theme", modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(NotePalettes) { index, palette ->
                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(
                                if (appThemeId == index) palette.secondary else Color.Transparent
                            ).padding(7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(35.dp).clip(CircleShape),
                                color = palette.primary,
                                onClick = { onThemeChange(index) }
                            ) {}
                            Text(palette.name, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, null, tint = MaterialTheme.colorScheme.primary)
                    Text("Privacy first", modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Text("Your notes are stored locally first. Drive access is requested only when you choose a Drive action.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.size(20.dp))
    }
}
