package com.nexauren.paperya.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.TimeUnit

object DriveBackupClient {
    private const val BACKUP_NAME = "NoteFlow Backup.json"
    private val LEGACY_BACKUP_NAMES = listOf("Paperya Backup.json", "Notoora Backup.json")
    private val http = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun downloadBackup(accessToken: String): String? {
        val fileId = findBackupId(accessToken) ?: return null
        val request = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files/" + fileId + "?alt=media")
            .header("Authorization", "Bearer " + accessToken)
            .get()
            .build()
        return http.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IllegalStateException("Google Drive returned HTTP " + response.code + ": " + body.take(220))
            body
        }
    }

    fun uploadBackup(accessToken: String, json: String) {
        val fileId = findBackupId(accessToken)
        if (fileId != null) {
            val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://www.googleapis.com/upload/drive/v3/files/" + fileId + "?uploadType=media")
                .header("Authorization", "Bearer " + accessToken)
                .patch(body)
                .build()
            http.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) throw IllegalStateException("Google Drive returned HTTP " + response.code + ": " + text.take(220))
            }
        } else {
            createBackup(accessToken, json)
        }
    }

    private fun findBackupId(token: String): String? {
        for (name in listOf(BACKUP_NAME) + LEGACY_BACKUP_NAMES) {
            val query = URLEncoder.encode("name = '" + name + "' and trashed = false", "UTF-8")
            val request = Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files?q=" + query + "&spaces=drive&pageSize=20&orderBy=modifiedTime desc&fields=files(id,name,modifiedTime)")
                .header("Authorization", "Bearer " + token)
                .get()
                .build()
            val found = http.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) throw IllegalStateException("Google Drive returned HTTP " + response.code + ": " + body.take(220))
                val files = JSONObject(body).optJSONArray("files")
                if (files == null || files.length() == 0) null else files.getJSONObject(0).optString("id").ifBlank { null }
            }
            if (found != null) return found
        }
        return null
    }

    private fun createBackup(token: String, json: String) {
        val boundary = "noteflow-" + UUID.randomUUID().toString()
        val metadata = JSONObject().put("name", BACKUP_NAME).put("mimeType", "application/json").toString()
        val bodyText = "--" + boundary + "\r\n" +
            "Content-Type: application/json; charset=UTF-8\r\n\r\n" +
            metadata + "\r\n" +
            "--" + boundary + "\r\n" +
            "Content-Type: application/json; charset=UTF-8\r\n\r\n" +
            json + "\r\n" +
            "--" + boundary + "--"
        val body = bodyText.toRequestBody(("multipart/related; boundary=" + boundary).toMediaType())
        val request = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
            .header("Authorization", "Bearer " + token)
            .post(body)
            .build()
        http.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IllegalStateException("Google Drive returned HTTP " + response.code + ": " + text.take(220))
        }
    }
}
