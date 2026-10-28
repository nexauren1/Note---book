package com.nexauren.paperya.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nexauren.paperya.MainActivity
import com.nexauren.paperya.R
import com.nexauren.paperya.data.NotesDatabase

class NoteReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: return
        val note = runCatching { NotesDatabase(context.applicationContext).getNote(noteId) }.getOrNull() ?: return
        if (note.isDeleted || note.isArchived || note.reminderAt <= System.currentTimeMillis()) return

        val title = note.title.ifBlank { "NoteFlow reminder" }
        val body = note.content.replace('\n', ' ').trim().take(180).ifBlank { "You wanted to come back to this note." }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Note reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Reminders you set on your NoteFlow notes"
                }
            )
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_NOTE_ID, noteId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingOpen = PendingIntent.getActivity(
            context, noteId.hashCode(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noteflow)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingOpen)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            manager.notify(noteId.hashCode(), notification)
        } catch (_: SecurityException) {
            // Android 13+ users can deny notification permission.
        }
    }

    companion object {
        const val CHANNEL_ID = "paperya_note_reminders"
        const val EXTRA_NOTE_ID = "paperya_open_note_id"
    }
}
