package com.nexauren.notoora.ui

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexauren.notoora.data.Note
import com.nexauren.notoora.data.NoteFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NoteAction { PIN, FAVORITE, ARCHIVE, DELETE, RESTORE }

@Composable
fun HomeScreen(
    notes: List<Note>,
    query: String,
    onQueryChanged: (String) -> Unit,
    filter: NoteFilter,
    onFilterChanged: (NoteFilter) -> Unit,
    onCreate: () -> Unit,
    onOpen: (Note) -> Unit,
    onSettings: () -> Unit,
    onAction: (Note, NoteAction) -> Unit
) {
    val palette = MaterialTheme.colorScheme
    Scaffold(
        containerColor = palette.background,
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate, containerColor = palette.primary, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Create note")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(15.dp))
                        .background(palette.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("n", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("Notoora", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Your ideas, beautifully kept", style = MaterialTheme.typography.bodySmall, color = palette.onSurfaceVariant)
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search notes, words and labels") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = palette.surface,
                    unfocusedContainerColor = palette.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf(
                    NoteFilter.ALL to "All",
                    NoteFilter.FAVORITES to "Favorites",
                    NoteFilter.ARCHIVED to "Archived",
                    NoteFilter.TRASH to "Trash"
                ).forEach { (item, title) ->
                    FilterChip(
                        selected = filter == item,
                        onClick = { onFilterChanged(item) },
                        label = { Text(title) }
                    )
                }
            }

            if (notes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("✦", style = MaterialTheme.typography.displayMedium, color = palette.primary)
                        Text(
                            when (filter) {
                                NoteFilter.TRASH -> "Your trash is empty"
                                NoteFilter.ARCHIVED -> "Nothing archived yet"
                                NoteFilter.FAVORITES -> "Your favorite notes will appear here"
                                else -> "Start with a little idea"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Tap + to create your first note.", color = palette.onSurfaceVariant)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 155.dp),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onOpen(note) },
                            onAction = { onAction(note, it) }
                        )
                    }
                    item { Spacer(Modifier.height(78.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: Note, onClick: () -> Unit, onAction: (NoteAction) -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    val darkPaper = note.backgroundId == 11 || note.backgroundId == 19
    val ink = if (darkPaper) Color(0xFFF9F7FF) else Color(0xFF262633)
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp)
            .clip(RoundedCornerShape(19.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box {
            PaperBackground(note.backgroundId, Modifier.matchParentSize())
            Column(Modifier.fillMaxWidth().padding(13.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        note.title.ifBlank { "Untitled note" },
                        modifier = Modifier.weight(1f),
                        color = ink,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Box {
                        IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(27.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Note options", tint = ink)
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text(if (note.isPinned) "Unpin" else "Pin") },
                                onClick = { menuOpen = false; onAction(NoteAction.PIN) },
                                leadingIcon = { Icon(Icons.Default.PushPin, null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (note.isFavorite) "Remove favorite" else "Add to favorites") },
                                onClick = { menuOpen = false; onAction(NoteAction.FAVORITE) },
                                leadingIcon = { Icon(Icons.Default.Star, null) }
                            )
                            if (note.isDeleted) {
                                DropdownMenuItem(
                                    text = { Text("Restore note") },
                                    onClick = { menuOpen = false; onAction(NoteAction.RESTORE) }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(if (note.isArchived) "Unarchive" else "Archive") },
                                    onClick = { menuOpen = false; onAction(NoteAction.ARCHIVE) },
                                    leadingIcon = { Icon(Icons.Default.Archive, null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Move to trash") },
                                    onClick = { menuOpen = false; onAction(NoteAction.DELETE) },
                                    leadingIcon = { Icon(Icons.Default.Delete, null) }
                                )
                            }
                        }
                    }
                }
                if (note.content.isNotBlank()) {
                    Text(
                        note.content,
                        color = ink.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                } else {
                    Text("Add a little something…", color = ink.copy(alpha = 0.60f), style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(9.dp))
                if (note.label.isNotBlank()) {
                    Surface(color = ink.copy(alpha = 0.10f), shape = RoundedCornerShape(9.dp)) {
                        Text(note.label, color = ink, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(note.updatedAt)),
                        color = ink.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f)
                    )
                    if (note.isPinned) Icon(Icons.Default.PushPin, null, tint = ink, modifier = Modifier.size(14.dp))
                    if (note.isFavorite) Icon(Icons.Default.Favorite, null, tint = ink, modifier = Modifier.size(14.dp).padding(start = 2.dp))
                }
            }
        }
    }
}
