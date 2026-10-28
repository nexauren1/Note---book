package com.nexauren.paperya.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject

class NotesDatabase(context: Context) : SQLiteOpenHelper(context, "paperya.db", null, 4) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE notes (
                id TEXT PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                background_id INTEGER NOT NULL DEFAULT 0,
                theme_id INTEGER NOT NULL DEFAULT 0,
                label TEXT NOT NULL DEFAULT '',
                attachments_json TEXT NOT NULL DEFAULT '[]',
                drawing_json TEXT NOT NULL DEFAULT '[]',
                custom_background_uri TEXT NOT NULL DEFAULT '',
                reminder_at INTEGER NOT NULL DEFAULT 0,
                is_pinned INTEGER NOT NULL DEFAULT 0,
                is_favorite INTEGER NOT NULL DEFAULT 0,
                is_archived INTEGER NOT NULL DEFAULT 0,
                is_deleted INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE notes ADD COLUMN drawing_json TEXT NOT NULL DEFAULT '[]'")
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE notes ADD COLUMN reminder_at INTEGER NOT NULL DEFAULT 0")
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE notes ADD COLUMN custom_background_uri TEXT NOT NULL DEFAULT ''")
        }
    }

    fun getNotes(filter: NoteFilter = NoteFilter.ALL): List<Note> {
        val where = when (filter) {
            NoteFilter.ALL -> "is_deleted = 0 AND is_archived = 0"
            NoteFilter.FAVORITES -> "is_deleted = 0 AND is_archived = 0 AND is_favorite = 1"
            NoteFilter.ARCHIVED -> "is_deleted = 0 AND is_archived = 1"
            NoteFilter.TRASH -> "is_deleted = 1"
        }
        val result = mutableListOf<Note>()
        readableDatabase.query("notes", null, where, null, null, null, "is_pinned DESC, updated_at DESC").use { cursor ->
            while (cursor.moveToNext()) result += cursor.toNote()
        }
        return result
    }

    fun getNote(id: String): Note? {
        readableDatabase.query("notes", null, "id = ?", arrayOf(id), null, null, null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.toNote() else null
        }
    }

    @Synchronized
    fun save(note: Note): Note {
        val values = ContentValues().apply {
            put("id", note.id)
            put("title", note.title)
            put("content", note.content)
            put("background_id", note.backgroundId)
            put("theme_id", note.themeId)
            put("label", note.label)
            put("attachments_json", note.attachmentsJson)
            put("drawing_json", note.drawingJson)
            put("custom_background_uri", note.customBackgroundUri)
            put("reminder_at", note.reminderAt)
            put("is_pinned", if (note.isPinned) 1 else 0)
            put("is_favorite", if (note.isFavorite) 1 else 0)
            put("is_archived", if (note.isArchived) 1 else 0)
            put("is_deleted", if (note.isDeleted) 1 else 0)
            put("created_at", note.createdAt)
            put("updated_at", note.updatedAt)
        }
        writableDatabase.insertWithOnConflict("notes", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return note
    }

    @Synchronized
    fun mergeBackupJson(backup: String): Int {
        val root = JSONObject(backup)
        if (root.optString("format") !in setOf("noteflow-backup", "paperya-backup", "notoora-backup")) {
            throw IllegalArgumentException("This is not a supported NoteFlow backup file.")
        }
        val rows = root.optJSONArray("notes")
            ?: throw IllegalArgumentException("The backup file does not contain notes.")
        var changed = 0
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (index in 0 until rows.length()) {
                val remote = Note.fromJson(rows.getJSONObject(index))
                val local = getNote(remote.id)
                if (local == null || remote.updatedAt > local.updatedAt) {
                    save(remote)
                    changed++
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return changed
    }

    @Synchronized
    fun exportBackupJson(): String {
        val rows = JSONArray()
        readableDatabase.query("notes", null, null, null, null, null, "updated_at DESC").use { cursor ->
            while (cursor.moveToNext()) rows.put(cursor.toNote().toJson())
        }
        return JSONObject()
            .put("format", "noteflow-backup")
            .put("schemaVersion", 4)
            .put("exportedAt", System.currentTimeMillis())
            .put("notes", rows)
            .toString(2)
    }

    private fun Cursor.toNote(): Note = Note(
        id = getString(getColumnIndexOrThrow("id")),
        title = getString(getColumnIndexOrThrow("title")),
        content = getString(getColumnIndexOrThrow("content")),
        backgroundId = getInt(getColumnIndexOrThrow("background_id")),
        themeId = getInt(getColumnIndexOrThrow("theme_id")),
        label = getString(getColumnIndexOrThrow("label")),
        attachmentsJson = getString(getColumnIndexOrThrow("attachments_json")),
        drawingJson = getString(getColumnIndexOrThrow("drawing_json")),
        customBackgroundUri = getString(getColumnIndexOrThrow("custom_background_uri")),
        reminderAt = getLong(getColumnIndexOrThrow("reminder_at")),
        isPinned = getInt(getColumnIndexOrThrow("is_pinned")) == 1,
        isFavorite = getInt(getColumnIndexOrThrow("is_favorite")) == 1,
        isArchived = getInt(getColumnIndexOrThrow("is_archived")) == 1,
        isDeleted = getInt(getColumnIndexOrThrow("is_deleted")) == 1,
        createdAt = getLong(getColumnIndexOrThrow("created_at")),
        updatedAt = getLong(getColumnIndexOrThrow("updated_at"))
    )
}
