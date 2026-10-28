package com.nexauren.paperya

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.nexauren.paperya.data.DriveBackupClient
import com.nexauren.paperya.data.Note
import com.nexauren.paperya.data.NoteFilter
import com.nexauren.paperya.data.NotesDatabase
import com.nexauren.paperya.ui.HomeScreen
import com.nexauren.paperya.ui.NoteAction
import com.nexauren.paperya.ui.NoteEditorScreen
import com.nexauren.paperya.ui.PaperyaTheme
import com.nexauren.paperya.ui.SettingsScreen
import com.nexauren.paperya.widget.PaperyaWidgetProvider
import com.nexauren.paperya.reminders.NoteReminderReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class DriveOperation { SYNC, RESTORE }

class MainActivity : ComponentActivity() {
    companion object { const val EXTRA_CREATE_NEW_NOTE = "paperya_create_new_note" }

    private lateinit var database: NotesDatabase
    private var pendingExportJson = ""
    private var pendingDriveOperation = DriveOperation.SYNC
    private var refreshCounter by mutableIntStateOf(0)
    private var driveStatus by mutableStateOf("Not connected yet")

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) driveStatus = "Reminder saved, but notifications are disabled. Enable them in Android settings."
    }

    private val exportBackupLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) {
            driveStatus = "Export cancelled"
        } else {
            runCatching {
                contentResolver.openOutputStream(uri)?.bufferedWriter(Charsets.UTF_8).use { writer ->
                    writer?.write(pendingExportJson)
                }
            }.onSuccess {
                driveStatus = "Backup exported successfully"
            }.onFailure {
                driveStatus = "Export failed: " + (it.message ?: "unknown error")
            }
        }
    }

    private val importBackupLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                try {
                    val text = withContext(Dispatchers.IO) {
                        contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                            ?: throw IllegalStateException("Could not read the selected file.")
                    }
                    val merged = withContext(Dispatchers.IO) { database.mergeBackupJson(text) }
                    refreshCounter++
                    driveStatus = "Imported backup; merged " + merged + " changed notes"
                } catch (exception: Exception) {
                    driveStatus = "Import failed: " + (exception.message ?: "unknown error")
                }
            }
        }
    }

    private val driveConsentLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            runCatching {
                Identity.getAuthorizationClient(this).getAuthorizationResultFromIntent(result.data!!).accessToken
            }.onSuccess { token ->
                if (token.isNullOrBlank()) driveStatus = "Google did not return an access token"
                else runDriveOperation(token)
            }.onFailure {
                driveStatus = "Google authorization failed: " + (it.message ?: "unknown error")
            }
        } else {
            driveStatus = "Google Drive authorization was cancelled"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = NotesDatabase(applicationContext)
        setContent {
            PaperyaRoot(
                database = database,
                refreshCounter = refreshCounter,
                driveStatus = driveStatus,
                initialOpenNoteId = intent.getStringExtra(NoteReminderReceiver.EXTRA_NOTE_ID),
                startWithNewNote = intent.getBooleanExtra(EXTRA_CREATE_NEW_NOTE, false),
                onRefresh = { refreshCounter++; PaperyaWidgetProvider.updateAll(this@MainActivity) },
                onReminderChanged = { note -> scheduleReminder(note) },
                onExport = {
                    pendingExportJson = database.exportBackupJson()
                    exportBackupLauncher.launch("Paperya-backup.json")
                },
                onImport = { importBackupLauncher.launch(arrayOf("application/json", "application/octet-stream", "text/plain")) },
                onDriveSync = { requestDriveAuthorization(DriveOperation.SYNC) },
                onDriveRestore = { requestDriveAuthorization(DriveOperation.RESTORE) }
            )
        }
    }

    private fun scheduleReminder(note: Note) {
        val alarmManager = getSystemService(AlarmManager::class.java)
        val alarmIntent = Intent(this, NoteReminderReceiver::class.java).apply {
            putExtra(NoteReminderReceiver.EXTRA_NOTE_ID, note.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            note.id.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (note.reminderAt <= System.currentTimeMillis() || note.isDeleted || note.isArchived) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, note.reminderAt, pendingIntent)
    }

    private fun requestDriveAuthorization(operation: DriveOperation) {
        pendingDriveOperation = operation
        driveStatus = "Requesting Google Drive access…"
        val request = AuthorizationRequest.builder()
            .setRequestedScopes(listOf(Scope("https://www.googleapis.com/auth/drive.file")))
            .build()
        Identity.getAuthorizationClient(this).authorize(request)
            .addOnSuccessListener { result ->
                if (result.hasResolution()) {
                    val pendingIntent = result.pendingIntent
                    if (pendingIntent == null) {
                        driveStatus = "Google needs permission, but no consent screen was returned"
                    } else {
                        driveConsentLauncher.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
                    }
                } else {
                    val token = result.accessToken
                    if (token.isNullOrBlank()) driveStatus = "Google did not return an access token"
                    else runDriveOperation(token)
                }
            }
            .addOnFailureListener { exception ->
                driveStatus = "Google Drive setup error: " + (exception.message ?: "authorization unavailable")
            }
    }

    private fun runDriveOperation(token: String) {
        lifecycleScope.launch {
            driveStatus = "Working with Google Drive…"
            try {
                val message = withContext(Dispatchers.IO) {
                    when (pendingDriveOperation) {
                        DriveOperation.SYNC -> {
                            val remote = DriveBackupClient.downloadBackup(token)
                            if (remote != null) database.mergeBackupJson(remote)
                            DriveBackupClient.uploadBackup(token, database.exportBackupJson())
                            if (remote == null) "Created your first Paperya backup in Drive"
                            else "Drive sync completed; local and cloud notes were merged"
                        }
                        DriveOperation.RESTORE -> {
                            val remote = DriveBackupClient.downloadBackup(token)
                                ?: throw IllegalStateException("No Paperya Backup.json file was found in this Drive account.")
                            val changed = database.mergeBackupJson(remote)
                            "Merged " + changed + " notes from Google Drive"
                        }
                    }
                }
                refreshCounter++
                driveStatus = message
            } catch (exception: Exception) {
                driveStatus = "Drive action failed: " + (exception.message ?: "unknown error")
            }
        }
    }
}

@Composable
private fun PaperyaRoot(
    database: NotesDatabase,
    refreshCounter: Int,
    driveStatus: String,
    initialOpenNoteId: String?,
    startWithNewNote: Boolean,
    onRefresh: () -> Unit,
    onReminderChanged: (Note) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDriveSync: () -> Unit,
    onDriveRestore: () -> Unit
) {
    var selectedNote by androidx.compose.runtime.remember(initialOpenNoteId, startWithNewNote) { mutableStateOf(initialOpenNoteId?.let { database.getNote(it) } ?: if (startWithNewNote) database.save(Note()) else null) }
    var settingsOpen by androidx.compose.runtime.remember { mutableStateOf(false) }
    var currentFilter by androidx.compose.runtime.remember { mutableStateOf(NoteFilter.ALL) }
    var search by androidx.compose.runtime.remember { mutableStateOf("") }
    val context = LocalContext.current
    val preferences = androidx.compose.runtime.remember(context) {
        context.getSharedPreferences("paperya_settings", android.content.Context.MODE_PRIVATE)
    }
    val defaultPortuguese = androidx.compose.runtime.remember(context) {
        context.resources.configuration.locales[0].language == "pt"
    }
    var isPortuguese by androidx.compose.runtime.saveable.rememberSaveable {
        mutableStateOf(preferences.getBoolean("isPortuguese", defaultPortuguese))
    }
    var appThemeId by androidx.compose.runtime.saveable.rememberSaveable { mutableIntStateOf(0) }
    PaperyaTheme(paletteId = selectedNote?.themeId ?: appThemeId) {
        Surface(Modifier.fillMaxSize()) {
            when {
                selectedNote != null -> {
                    NoteEditorScreen(
                        note = selectedNote!!,
                        isPortuguese = isPortuguese,
                        onSave = { note ->
                            val previous = selectedNote
                            database.save(note)
                            if (previous?.reminderAt != note.reminderAt || previous?.isArchived != note.isArchived || previous?.isDeleted != note.isDeleted) {
                                onReminderChanged(note)
                            }
                            selectedNote = note
                            onRefresh()
                        },
                        onBack = { note ->
                            val previous = selectedNote
                            database.save(note)
                            if (previous?.reminderAt != note.reminderAt || previous?.isArchived != note.isArchived || previous?.isDeleted != note.isDeleted) {
                                onReminderChanged(note)
                            }
                            selectedNote = null
                            onRefresh()
                        }
                    )
                }
                settingsOpen -> {
                    SettingsScreen(
                        driveStatus = driveStatus,
                        appThemeId = appThemeId,
                        isPortuguese = isPortuguese,
                        onBack = { settingsOpen = false },
                        onExport = onExport,
                        onImport = onImport,
                        onDriveSync = onDriveSync,
                        onDriveRestore = onDriveRestore,
                        onThemeChange = { appThemeId = it },
                        onLanguageChange = {
                            isPortuguese = it
                            preferences.edit().putBoolean("isPortuguese", it).apply()
                        }
                    )
                }
                else -> {
                    val notes = androidx.compose.runtime.remember(refreshCounter, currentFilter, search) {
                        database.getNotes(currentFilter).filter {
                            search.isBlank() || it.title.contains(search, ignoreCase = true) ||
                                it.content.contains(search, ignoreCase = true) ||
                                it.label.contains(search, ignoreCase = true)
                        }
                    }
                    HomeScreen(
                        notes = notes,
                        query = search,
                        onQueryChanged = { search = it },
                        filter = currentFilter,
                        onFilterChanged = { currentFilter = it },
                        onCreate = {
                            val note = database.save(Note())
                            selectedNote = note
                        },
                        onOpen = { selectedNote = it },
                        onSettings = { settingsOpen = true },
                        isPortuguese = isPortuguese,
                        onAction = { note, action ->
                            val updated = when (action) {
                                NoteAction.PIN -> note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis())
                                NoteAction.FAVORITE -> note.copy(isFavorite = !note.isFavorite, updatedAt = System.currentTimeMillis())
                                NoteAction.ARCHIVE -> note.copy(isArchived = !note.isArchived, updatedAt = System.currentTimeMillis())
                                NoteAction.DELETE -> note.copy(isDeleted = true, updatedAt = System.currentTimeMillis())
                                NoteAction.RESTORE -> note.copy(isDeleted = false, isArchived = false, updatedAt = System.currentTimeMillis())
                            }
                            database.save(updated)
                            if (note.reminderAt != updated.reminderAt || note.isArchived != updated.isArchived || note.isDeleted != updated.isDeleted) onReminderChanged(updated)
                            onRefresh()
                        }
                    )
                }
            }
        }
    }
}
