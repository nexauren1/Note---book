package com.nexauren.paperya.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexauren.paperya.data.Note
import kotlinx.coroutines.delay
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NoteEditorScreen(
    note: Note,
    onSave: (Note) -> Unit,
    onBack: (Note) -> Unit
) {
    val context = LocalContext.current
    var draft by remember(note.id) { mutableStateOf(note) }
    var showBackgroundPicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showDrawingPad by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showLabelEditor by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Popular") }
    var labelDraft by remember(draft.id) { mutableStateOf(draft.label) }

    val attachmentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val attachments = runCatching { JSONArray(draft.attachmentsJson) }.getOrElse { JSONArray() }
            var alreadyAdded = false
            for (index in 0 until attachments.length()) if (attachments.optString(index) == uri.toString()) alreadyAdded = true
            if (!alreadyAdded) attachments.put(uri.toString())
            draft = draft.copy(attachmentsJson = attachments.toString(), updatedAt = System.currentTimeMillis())
        }
    }

    LaunchedEffect(draft.title, draft.content, draft.backgroundId, draft.themeId, draft.label, draft.attachmentsJson, draft.drawingJson, draft.reminderAt, draft.isPinned, draft.isFavorite, draft.isArchived, draft.isDeleted) {
        delay(450)
        onSave(draft.copy(updatedAt = System.currentTimeMillis()))
    }

    Box(Modifier.fillMaxSize()) {
        PaperBackground(draft.backgroundId, Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = { onBack(draft) }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                Column(Modifier.weight(1f)) {
                    Text("EDIT NOTE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(draft.updatedAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF74717B)
                    )
                }
                IconButton(onClick = { showReminderDialog = true }) {
                    Icon(Icons.Default.Alarm, contentDescription = "Set reminder", tint = if (draft.reminderAt > System.currentTimeMillis()) MaterialTheme.colorScheme.primary else Color.Unspecified)
                }
                IconButton(onClick = { showThemePicker = true }) {
                    Icon(Icons.Default.Palette, contentDescription = "Choose theme")
                }
                IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, contentDescription = "More options") }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(if (draft.isPinned) "Unpin note" else "Pin note") },
                        leadingIcon = { Icon(Icons.Default.PushPin, null) },
                        onClick = { showMenu = false; draft = draft.copy(isPinned = !draft.isPinned, updatedAt = System.currentTimeMillis()) }
                    )
                    DropdownMenuItem(
                        text = { Text(if (draft.isFavorite) "Remove favorite" else "Add to favorites") },
                        leadingIcon = { Icon(Icons.Default.Star, null) },
                        onClick = { showMenu = false; draft = draft.copy(isFavorite = !draft.isFavorite, updatedAt = System.currentTimeMillis()) }
                    )
                    DropdownMenuItem(
                        text = { Text("Change paper background") },
                        onClick = { showMenu = false; showBackgroundPicker = true }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit label") },
                        onClick = { showMenu = false; labelDraft = draft.label; showLabelEditor = true }
                    )
                    DropdownMenuItem(
                        text = { Text("Archive note") },
                        onClick = { showMenu = false; draft = draft.copy(isArchived = !draft.isArchived, updatedAt = System.currentTimeMillis()); onBack(draft) }
                    )
                    DropdownMenuItem(
                        text = { Text("Move to trash") },
                        onClick = { showMenu = false; val deleted = draft.copy(isDeleted = true, updatedAt = System.currentTimeMillis()); onSave(deleted); onBack(deleted) }
                    )
                }
                IconButton(onClick = { onSave(draft.copy(updatedAt = System.currentTimeMillis())); onBack(draft) }) {
                    Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp)) {
                BasicTextField(
                    value = draft.title,
                    onValueChange = { draft = draft.copy(title = it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF292631), fontWeight = FontWeight.SemiBold),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        Box {
                            if (draft.title.isEmpty()) Text("Title", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF9C98A1))
                            inner()
                        }
                    }
                )
                Divider(color = Color(0xFF8A8490).copy(alpha = 0.20f))
                BasicTextField(
                    value = draft.content,
                    onValueChange = { draft = draft.copy(content = it) },
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false).heightIn(min = 250.dp).padding(top = 14.dp),
                    textStyle = TextStyle(
                        color = Color(0xFF2F2A35),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        Box {
                            if (draft.content.isEmpty()) Text("Write something worth keeping…", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF9C98A1))
                            inner()
                        }
                    }
                )

                if (draft.reminderAt > 0L) {
                    Surface(color = Color.White.copy(alpha = 0.72f), shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            "⏰ Reminder: " + SimpleDateFormat("EEE, MMM d · HH:mm", Locale.getDefault()).format(Date(draft.reminderAt)),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                val attachments = runCatching { JSONArray(draft.attachmentsJson) }.getOrElse { JSONArray() }
                if (attachments.length() > 0) {
                    Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("ATTACHMENTS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        for (index in 0 until attachments.length()) {
                            val uriText = attachments.optString(index)
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.55f)).padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AttachFile, null, tint = MaterialTheme.colorScheme.primary)
                                Text(Uri.parse(uriText).lastPathSegment ?: "Attached file", modifier = Modifier.weight(1f), maxLines = 1)
                                IconButton(onClick = {
                                    val updated = JSONArray()
                                    for (i in 0 until attachments.length()) if (i != index) updated.put(attachments.optString(i))
                                    draft = draft.copy(attachmentsJson = updated.toString())
                                }, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove attachment")
                                }
                            }
                        }
                    }
                }

                if (draft.drawingJson.isNotBlank() && draft.drawingJson != "[]") {
                    Surface(color = Color.White.copy(alpha = 0.72f), shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(top = 10.dp)) {
                        Text("✎ Sketch saved", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (draft.label.isNotBlank()) {
                    Surface(
                        modifier = Modifier.padding(top = 12.dp),
                        color = Color.White.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(draft.label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Surface(color = Color.White.copy(alpha = 0.96f), shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showDrawingPad = true }) { Text("✎  Draw") }
                    TextButton(onClick = { showBackgroundPicker = true }) { Text("▤  Paper") }
                    TextButton(onClick = { showThemePicker = true }) { Text("◉  Theme") }
                    TextButton(onClick = { attachmentPicker.launch(arrayOf("*/*")) }) { Icon(Icons.Default.AttachFile, null); Spacer(Modifier.width(4.dp)); Text("Attach") }
                    TextButton(onClick = { draft = draft.copy(content = appendLine(draft.content, "☐ ")) }) { Text("☐  Checklist") }
                    TextButton(onClick = { draft = draft.copy(content = appendLine(draft.content, "• ")) }) { Text("•  Bullets") }
                    TextButton(onClick = { draft = draft.copy(content = appendLine(draft.content, "1. ")) }) { Text("1.  Numbered") }
                    TextButton(onClick = { showEmojiPicker = true }) { Text("☺  Stickers") }
                }
            }
        }
    }

    if (showDrawingPad) {
        DrawingPad(
            initialJson = draft.drawingJson,
            paperId = draft.backgroundId,
            onDismiss = { showDrawingPad = false },
            onSave = { json -> draft = draft.copy(drawingJson = json, updatedAt = System.currentTimeMillis()) }
        )
    }

    if (showReminderDialog) {
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text("Reminder") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Choose when Paperya should remind you about this note.")
                    if (draft.reminderAt > System.currentTimeMillis()) {
                        Text("Scheduled: " + SimpleDateFormat("EEE, MMM d · HH:mm", Locale.getDefault()).format(Date(draft.reminderAt)), color = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = {
                        draft = draft.copy(reminderAt = System.currentTimeMillis() + 60L * 60L * 1000L, updatedAt = System.currentTimeMillis())
                        showReminderDialog = false
                    }) { Text("In one hour") }
                    TextButton(onClick = {
                        val tomorrow = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, 1)
                            set(Calendar.HOUR_OF_DAY, 9)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        draft = draft.copy(reminderAt = tomorrow.timeInMillis, updatedAt = System.currentTimeMillis())
                        showReminderDialog = false
                    }) { Text("Tomorrow at 09:00") }
                    if (draft.reminderAt > 0L) {
                        TextButton(onClick = {
                            draft = draft.copy(reminderAt = 0L, updatedAt = System.currentTimeMillis())
                            showReminderDialog = false
                        }) { Text("Clear reminder") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showReminderDialog = false }) { Text("Close") } }
        )
    }

    if (showBackgroundPicker) {
        AlertDialog(
            onDismissRequest = { showBackgroundPicker = false },
            title = { Text("Choose your paper") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf("Popular", "Plain", "Classic", "Trending").forEach { tab ->
                            FilterChip(selected = category == tab, onClick = { category = tab }, label = { Text(tab, maxLines = 1) })
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(PaperPresets.filter { it.category == category || category == "Popular" && (it.category == "Popular" || it.category == "Trending") }) { _, preset ->
                            val index = PaperPresets.indexOf(preset)
                            Column(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable {
                                    draft = draft.copy(backgroundId = index, updatedAt = System.currentTimeMillis())
                                    showBackgroundPicker = false
                                },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                PaperSwatch(index, Modifier.fillMaxWidth().height(94.dp))
                                Text(preset.name, style = MaterialTheme.typography.labelSmall, maxLines = 1, modifier = Modifier.padding(top = 4.dp, bottom = 2.dp))
                                if (draft.backgroundId == index) Text("Selected", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showBackgroundPicker = false }) { Text("Done") } }
        )
    }

    if (showThemePicker) {
        AlertDialog(
            onDismissRequest = { showThemePicker = false },
            title = { Text("Choose a theme") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(NotePalettes) { index, palette ->
                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable {
                                draft = draft.copy(themeId = index, updatedAt = System.currentTimeMillis())
                                showThemePicker = false
                            }.padding(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier.size(48.dp).clip(CircleShape).background(palette.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(Modifier.size(19.dp).clip(CircleShape).background(palette.secondary))
                            }
                            Text(palette.name, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 5.dp))
                            if (draft.themeId == index) Text("Selected", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showThemePicker = false }) { Text("Done") } }
        )
    }

    if (showEmojiPicker) {
        val stickers = listOf("🌷", "🌸", "🌿", "⭐", "✨", "☀️", "🌈", "🐻", "🐰", "🍓", "🍒", "🦋", "💖", "🎀", "☕", "📚", "🌻", "🌙")
        AlertDialog(
            onDismissRequest = { showEmojiPicker = false },
            title = { Text("Add a little personality") },
            text = {
                LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.heightIn(max = 280.dp)) {
                    itemsIndexed(stickers) { _, sticker ->
                        Box(
                            Modifier.size(42.dp).clip(CircleShape).clickable {
                                draft = draft.copy(content = appendLine(draft.content, sticker + " "))
                                showEmojiPicker = false
                            },
                            contentAlignment = Alignment.Center
                        ) { Text(sticker, style = MaterialTheme.typography.titleLarge) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showEmojiPicker = false }) { Text("Close") } }
        )
    }

    if (showLabelEditor) {
        AlertDialog(
            onDismissRequest = { showLabelEditor = false },
            title = { Text("Note label") },
            text = {
                OutlinedTextField(
                    value = labelDraft,
                    onValueChange = { labelDraft = it },
                    singleLine = true,
                    placeholder = { Text("Work, study, personal…") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    draft = draft.copy(label = labelDraft.trim(), updatedAt = System.currentTimeMillis())
                    showLabelEditor = false
                }) { Text("Save label") }
            },
            dismissButton = { TextButton(onClick = { showLabelEditor = false }) { Text("Cancel") } }
        )
    }
}

private fun appendLine(current: String, value: String): String =
    if (current.isBlank()) value else current.trimEnd() + "\n" + value
