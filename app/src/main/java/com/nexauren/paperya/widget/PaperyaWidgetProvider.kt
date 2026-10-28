package com.nexauren.paperya.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.nexauren.paperya.MainActivity
import com.nexauren.paperya.R
import com.nexauren.paperya.data.NotesDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaperyaWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        val note = runCatching { NotesDatabase(context.applicationContext).getNotes().firstOrNull() }.getOrNull()
        appWidgetIds.forEach { widgetId ->
            val views = RemoteViews(context.packageName, R.layout.paperya_widget)
            views.setTextViewText(R.id.widget_note_title, note?.title?.ifBlank { "A little idea" } ?: "Your notes, beautifully kept")
            views.setTextViewText(
                R.id.widget_note_preview,
                note?.content?.replace('\n', ' ')?.trim()?.take(95)?.ifBlank { "Tap + to capture something new." }
                    ?: "Tap + to capture something new."
            )
            views.setTextViewText(
                R.id.widget_updated,
                note?.let { "Updated " + SimpleDateFormat("MMM d · HH:mm", Locale.getDefault()).format(Date(it.updatedAt)) }
                    ?: "Your private notebook"
            )

            val openIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val openPending = PendingIntent.getActivity(
                context, widgetId * 10 + 1, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_open_app, openPending)
            views.setOnClickPendingIntent(R.id.widget_note_preview, openPending)

            val newNoteIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_CREATE_NEW_NOTE, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val newNotePending = PendingIntent.getActivity(
                context, widgetId * 10 + 2, newNoteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_new_note, newNotePending)
            manager.updateAppWidget(widgetId, views)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, PaperyaWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isNotEmpty()) PaperyaWidgetProvider().onUpdate(context, manager, ids)
        }
    }
}
