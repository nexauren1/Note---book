package com.nexauren.paperya.data

import org.json.JSONObject
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val backgroundId: Int = 0,
    val themeId: Int = 0,
    val label: String = "",
    val attachmentsJson: String = "[]",
    val drawingJson: String = "[]",
    val reminderAt: Long = 0L,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("content", content)
        .put("backgroundId", backgroundId)
        .put("themeId", themeId)
        .put("label", label)
        .put("attachmentsJson", attachmentsJson)
        .put("drawingJson", drawingJson)
        .put("reminderAt", reminderAt)
        .put("isPinned", isPinned)
        .put("isFavorite", isFavorite)
        .put("isArchived", isArchived)
        .put("isDeleted", isDeleted)
        .put("createdAt", createdAt)
        .put("updatedAt", updatedAt)

    companion object {
        fun fromJson(json: JSONObject): Note = Note(
            id = json.optString("id").ifBlank { UUID.randomUUID().toString() },
            title = json.optString("title"),
            content = json.optString("content"),
            backgroundId = json.optInt("backgroundId", 0),
            themeId = json.optInt("themeId", 0),
            label = json.optString("label"),
            attachmentsJson = json.optString("attachmentsJson", "[]"),
            drawingJson = json.optString("drawingJson", "[]"),
            reminderAt = json.optLong("reminderAt", 0L),
            isPinned = json.optBoolean("isPinned", false),
            isFavorite = json.optBoolean("isFavorite", false),
            isArchived = json.optBoolean("isArchived", false),
            isDeleted = json.optBoolean("isDeleted", false),
            createdAt = json.optLong("createdAt", System.currentTimeMillis()),
            updatedAt = json.optLong("updatedAt", System.currentTimeMillis())
        )
    }
}

enum class NoteFilter {
    ALL, FAVORITES, ARCHIVED, TRASH
}
