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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
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
    isPortuguese: Boolean,
    startWidgetAction: String? = null,
    onWidgetActionConsumed: () -> Unit = {},
    onSave: (Note) -> Unit,
    onBack: (Note) -> Unit
) {
    val context = LocalContext.current
    var draft by remember(note.id) { mutableStateOf(note) }
    val editorPalette = NotePalettes.getOrElse(draft.themeId) { NotePalettes.first() }
    var showBackgroundPicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showDrawingPad by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showLabelEditor by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Popular") }
    var labelDraft by remember(draft.id) { mutableStateOf(draft.label) }

    val backgroundPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            draft = draft.copy(customBackgroundUri = uri.toString(), updatedAt = System.currentTimeMillis())
        }
    }

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

    LaunchedEffect(note.id, startWidgetAction) {
        when (startWidgetAction) {
            "photo" -> {
                delay(250)
                backgroundPhotoPicker.launch(arrayOf("image/*"))
            }
            "audio" -> {
                delay(250)
                attachmentPicker.launch(arrayOf("audio/*"))
            }
            "draw" -> showDrawingPad = true
        }
        if (startWidgetAction != null) onWidgetActionConsumed()
    }

    LaunchedEffect(draft.title, draft.content, draft.backgroundId, draft.themeId, draft.label, draft.attachmentsJson, draft.drawingJson, draft.customBackgroundUri, draft.reminderAt, draft.isPinned, draft.isFavorite, draft.isArchived, draft.isDeleted) {
        delay(450)
        onSave(draft.copy(updatedAt = System.currentTimeMillis()))
    }

    Box(Modifier.fillMaxSize()) {
        PaperBackground(draft.backgroundId, Modifier.fillMaxSize(), draft.customBackgroundUri.takeIf { it.isNotBlank() })
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = { onBack(draft) }) { Icon(Icons.Default.ArrowBack, contentDescription = uiText(isPortuguese, "Voltar", "Back")) }
                Column(Modifier.weight(1f)) {
                    Text(uiText(isPortuguese, "EDITAR NOTA", "EDIT NOTE"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(draft.updatedAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF74717B)
                    )
                }
                IconButton(onClick = { showReminderDialog = true }) {
                    Icon(Icons.Default.Alarm, contentDescription = uiText(isPortuguese, "Definir lembrete", "Set reminder"), tint = if (draft.reminderAt > System.currentTimeMillis()) MaterialTheme.colorScheme.primary else Color.Unspecified)
                }
                IconButton(onClick = { showThemePicker = true }) {
                    Icon(Icons.Default.Palette, contentDescription = uiText(isPortuguese, "Escolher tema", "Choose theme"))
                }
                IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, contentDescription = uiText(isPortuguese, "Mais opções", "More options")) }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(if (draft.isPinned) uiText(isPortuguese, "Desafixar nota", "Unpin note") else uiText(isPortuguese, "Fixar nota", "Pin note")) },
                        leadingIcon = { Icon(Icons.Default.PushPin, null) },
                        onClick = { showMenu = false; draft = draft.copy(isPinned = !draft.isPinned, updatedAt = System.currentTimeMillis()) }
                    )
                    DropdownMenuItem(
                        text = { Text(if (draft.isFavorite) uiText(isPortuguese, "Remover dos favoritos", "Remove favorite") else uiText(isPortuguese, "Adicionar aos favoritos", "Add to favorites")) },
                        leadingIcon = { Icon(Icons.Default.Star, null) },
                        onClick = { showMenu = false; draft = draft.copy(isFavorite = !draft.isFavorite, updatedAt = System.currentTimeMillis()) }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Mudar fundo do papel", "Change paper background")) },
                        onClick = { showMenu = false; showBackgroundPicker = true }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Editar etiqueta", "Edit label")) },
                        onClick = { showMenu = false; labelDraft = draft.label; showLabelEditor = true }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Arquivar nota", "Archive note")) },
                        onClick = { showMenu = false; draft = draft.copy(isArchived = !draft.isArchived, updatedAt = System.currentTimeMillis()); onBack(draft) }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Mover para o lixo", "Move to trash")) },
                        onClick = { showMenu = false; val deleted = draft.copy(isDeleted = true, updatedAt = System.currentTimeMillis()); onSave(deleted); onBack(deleted) }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Anexar ficheiro", "Attach file")) },
                        onClick = { showMenu = false; attachmentPicker.launch(arrayOf("*/*")) }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Lista de tarefas", "Checklist")) },
                        onClick = { showMenu = false; draft = draft.copy(content = appendLine(draft.content, "☐ ")) }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Lista com marcadores", "Bulleted list")) },
                        onClick = { showMenu = false; draft = draft.copy(content = appendLine(draft.content, "• ")) }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Lista numerada", "Numbered list")) },
                        onClick = { showMenu = false; draft = draft.copy(content = appendLine(draft.content, "1. ")) }
                    )
                    DropdownMenuItem(
                        text = { Text(uiText(isPortuguese, "Autocolantes e emojis", "Stickers and emoji")) },
                        onClick = { showMenu = false; showEmojiPicker = true }
                    )
                }
                IconButton(onClick = { onSave(draft.copy(updatedAt = System.currentTimeMillis())); onBack(draft) }) {
                    Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Column(
                Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(editorPalette.page.copy(alpha = 0.98f))
                    .padding(horizontal = 18.dp)
            ) {
                BasicTextField(
                    value = draft.title,
                    onValueChange = { draft = draft.copy(title = it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(color = editorPalette.ink, fontWeight = FontWeight.SemiBold),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        Box {
                            if (draft.title.isEmpty()) Text(uiText(isPortuguese, "Título", "Title"), style = MaterialTheme.typography.headlineSmall, color = Color(0xFF9C98A1))
                            inner()
                        }
                    }
                )
                Divider(color = editorPalette.ink.copy(alpha = 0.18f))
                BasicTextField(
                    value = draft.content,
                    onValueChange = { draft = draft.copy(content = it) },
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false).heightIn(min = 250.dp).padding(top = 14.dp),
                    textStyle = TextStyle(
                        color = editorPalette.ink,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        Box {
                            if (draft.content.isEmpty()) Text(uiText(isPortuguese, "Escreve algo que valha a pena guardar…", "Write something worth keeping…"), style = MaterialTheme.typography.bodyLarge, color = Color(0xFF9C98A1))
                            inner()
                        }
                    }
                )

                if (draft.reminderAt > 0L) {
                    Surface(color = Color.White.copy(alpha = 0.72f), shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            uiText(isPortuguese, "⏰ Lembrete: ", "⏰ Reminder: ") + SimpleDateFormat("EEE, d MMM · HH:mm", if (isPortuguese) Locale("pt", "PT") else Locale.ENGLISH).format(Date(draft.reminderAt)),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                val attachments = runCatching { JSONArray(draft.attachmentsJson) }.getOrElse { JSONArray() }
                if (attachments.length() > 0) {
                    Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(uiText(isPortuguese, "ANEXOS", "ATTACHMENTS"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        for (index in 0 until attachments.length()) {
                            val uriText = attachments.optString(index)
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.55f)).padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AttachFile, null, tint = MaterialTheme.colorScheme.primary)
                                Text(Uri.parse(uriText).lastPathSegment ?: uiText(isPortuguese, "Ficheiro anexado", "Attached file"), modifier = Modifier.weight(1f), maxLines = 1)
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
                        Text(uiText(isPortuguese, "✎ Desenho guardado", "✎ Sketch saved"), modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), style = MaterialTheme.typography.labelLarge)
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

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                color = editorPalette.card,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 7.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showDrawingPad = true }, modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Text(uiText(isPortuguese, "Desenhar", "Draw"), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    TextButton(onClick = { showBackgroundPicker = true }, modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("▤", style = MaterialTheme.typography.titleMedium)
                            Text(uiText(isPortuguese, "Papel", "Paper"), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    TextButton(onClick = { backgroundPhotoPicker.launch(arrayOf("image/*")) }, modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Text(uiText(isPortuguese, "Foto", "Photo"), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    TextButton(onClick = { showThemePicker = true }, modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Palette, contentDescription = null)
                            Text(uiText(isPortuguese, "Tema", "Theme"), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }

    if (showDrawingPad) {
        DrawingPad(
            initialJson = draft.drawingJson,
            paperId = draft.backgroundId,
            customImageUri = draft.customBackgroundUri.takeIf { it.isNotBlank() },
            isPortuguese = isPortuguese,
            onDismiss = { showDrawingPad = false },
            onSave = { json -> draft = draft.copy(drawingJson = json, updatedAt = System.currentTimeMillis()) }
        )
    }

    if (showReminderDialog) {
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text(uiText(isPortuguese, "Lembrete", "Reminder")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(uiText(isPortuguese, "Escolhe quando NoteFlow te deve lembrar desta nota.", "Choose when NoteFlow should remind you about this note."))
                    if (draft.reminderAt > System.currentTimeMillis()) {
                        Text(uiText(isPortuguese, "Agendado: ", "Scheduled: ") + SimpleDateFormat("EEE, d MMM · HH:mm", if (isPortuguese) Locale("pt", "PT") else Locale.ENGLISH).format(Date(draft.reminderAt)), color = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = {
                        draft = draft.copy(reminderAt = System.currentTimeMillis() + 60L * 60L * 1000L, updatedAt = System.currentTimeMillis())
                        showReminderDialog = false
                    }) { Text(uiText(isPortuguese, "Daqui a uma hora", "In one hour")) }
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
                    }) { Text(uiText(isPortuguese, "Amanhã às 09:00", "Tomorrow at 09:00")) }
                    if (draft.reminderAt > 0L) {
                        TextButton(onClick = {
                            draft = draft.copy(reminderAt = 0L, updatedAt = System.currentTimeMillis())
                            showReminderDialog = false
                        }) { Text(uiText(isPortuguese, "Remover lembrete", "Clear reminder")) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showReminderDialog = false }) { Text(uiText(isPortuguese, "Fechar", "Close")) } }
        )
    }

    if (showBackgroundPicker) {
        AlertDialog(
            onDismissRequest = { showBackgroundPicker = false },
            title = { Text(uiText(isPortuguese, "Escolhe o fundo", "Choose your paper")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf(
                            "Popular" to uiText(isPortuguese, "Popular", "Popular"),
                            "Common" to uiText(isPortuguese, "Comum", "Common"),
                            "Plain" to uiText(isPortuguese, "Plano", "Plain"),
                            "Classic" to uiText(isPortuguese, "Clássica", "Classic"),
                            "Trending" to uiText(isPortuguese, "Na moda", "Trending")
                        ).forEach { (tab, label) ->
                            FilterChip(selected = category == tab, onClick = { category = tab }, label = { Text(label, maxLines = 1) })
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(PaperPresets.filter { it.category == category }) { _, preset ->
                            val index = PaperPresets.indexOf(preset)
                            Column(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable {
                                    draft = draft.copy(backgroundId = index, customBackgroundUri = "", updatedAt = System.currentTimeMillis())
                                    showBackgroundPicker = false
                                },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                PaperSwatch(index, Modifier.fillMaxWidth().height(94.dp))
                                Text(paperDisplayName(preset.name, isPortuguese), style = MaterialTheme.typography.labelSmall, maxLines = 1, modifier = Modifier.padding(top = 4.dp, bottom = 2.dp))
                                if (draft.backgroundId == index) Text(uiText(isPortuguese, "Selecionado", "Selected"), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showBackgroundPicker = false }) { Text(uiText(isPortuguese, "Concluído", "Done")) } }
        )
    }

    if (showThemePicker) {
        AlertDialog(
            onDismissRequest = { showThemePicker = false },
            title = { Text(uiText(isPortuguese, "Escolhe um tema", "Choose a theme")) },
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
                            Text(paletteDisplayName(palette.name, isPortuguese), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 5.dp))
                            if (draft.themeId == index) Text(uiText(isPortuguese, "Selecionado", "Selected"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showThemePicker = false }) { Text(uiText(isPortuguese, "Concluído", "Done")) } }
        )
    }

    if (showEmojiPicker) {
        val stickers = listOf("🌷", "🌸", "🌿", "⭐", "✨", "☀️", "🌈", "🐻", "🐰", "🍓", "🍒", "🦋", "💖", "🎀", "☕", "📚", "🌻", "🌙")
        AlertDialog(
            onDismissRequest = { showEmojiPicker = false },
            title = { Text(uiText(isPortuguese, "Dá personalidade à nota", "Add a little personality")) },
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
            confirmButton = { TextButton(onClick = { showEmojiPicker = false }) { Text(uiText(isPortuguese, "Fechar", "Close")) } }
        )
    }

    if (showLabelEditor) {
        AlertDialog(
            onDismissRequest = { showLabelEditor = false },
            title = { Text(uiText(isPortuguese, "Etiqueta da nota", "Note label")) },
            text = {
                OutlinedTextField(
                    value = labelDraft,
                    onValueChange = { labelDraft = it },
                    singleLine = true,
                    placeholder = { Text(uiText(isPortuguese, "Trabalho, estudo, pessoal…", "Work, study, personal…")) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Text(uiText(isPortuguese, "Etiquetas prontas", "Quick labels"), style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Trabalho", "Estudo", "Pessoal").forEach { readyLabel ->
                        FilterChip(selected = labelDraft == readyLabel, onClick = { labelDraft = readyLabel }, label = { Text(labelDisplayName(readyLabel, isPortuguese)) })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Ideias", "Tarefas").forEach { readyLabel ->
                        FilterChip(selected = labelDraft == readyLabel, onClick = { labelDraft = readyLabel }, label = { Text(labelDisplayName(readyLabel, isPortuguese)) })
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    draft = draft.copy(label = labelDraft.trim(), updatedAt = System.currentTimeMillis())
                    showLabelEditor = false
                }) { Text(uiText(isPortuguese, "Guardar etiqueta", "Save label")) }
            },
            dismissButton = { TextButton(onClick = { showLabelEditor = false }) { Text(uiText(isPortuguese, "Cancelar", "Cancel")) } }
        )
    }
}

private fun appendLine(current: String, value: String): String =
    if (current.isBlank()) value else current.trimEnd() + "\n" + value
