package com.nexauren.paperya.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexauren.paperya.data.Note
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    notes: List<Note>,
    isPortuguese: Boolean,
    onBack: () -> Unit,
    onOpenNote: (Note) -> Unit
) {
    val palette = MaterialTheme.colorScheme
    val locale = if (isPortuguese) Locale("pt", "PT") else Locale.ENGLISH
    var visibleMonth by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }
    var selectedDay by remember { mutableStateOf(startOfCalendarDay(System.currentTimeMillis())) }
    val monthCalendar = Calendar.getInstance().apply { timeInMillis = visibleMonth }
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstWeekday = if (isPortuguese) Calendar.MONDAY else Calendar.SUNDAY
    val offset = (monthCalendar.get(Calendar.DAY_OF_WEEK) - firstWeekday + 7) % 7
    val dayCells = buildList<Int?> {
        repeat(offset) { add(null) }
        for (day in 1..daysInMonth) add(day)
    }
    val reminders = notes.filter { it.reminderAt > 0L && !it.isArchived && !it.isDeleted }.sortedBy { it.reminderAt }
    val reminderDays = reminders.map { startOfCalendarDay(it.reminderAt) }.toSet()
    val notesForDay = reminders.filter { startOfCalendarDay(it.reminderAt) == selectedDay }
    val monthTitle = SimpleDateFormat("MMMM yyyy", locale).format(Date(visibleMonth))
    val selectedDateTitle = SimpleDateFormat("EEE, d MMM yyyy", locale).format(Date(selectedDay))
    val weekNames = if (isPortuguese) {
        listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
    } else {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }

    Column(
        Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = uiText(isPortuguese, "Voltar", "Back"))
            }
            Column(Modifier.weight(1f)) {
                Text(uiText(isPortuguese, "Calendário", "Calendar"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(uiText(isPortuguese, "Organiza os teus lembretes", "Keep your reminders organized"), style = MaterialTheme.typography.bodySmall, color = palette.onSurfaceVariant)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = palette.surface)
        ) {
            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val previous = Calendar.getInstance().apply {
                            timeInMillis = visibleMonth
                            add(Calendar.MONTH, -1)
                        }
                        visibleMonth = previous.timeInMillis
                        selectedDay = startOfCalendarDay(previous.timeInMillis)
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = uiText(isPortuguese, "Mês anterior", "Previous month"))
                    }
                    Text(
                        monthTitle.replaceFirstChar { it.titlecase(locale) },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = {
                        val next = Calendar.getInstance().apply {
                            timeInMillis = visibleMonth
                            add(Calendar.MONTH, 1)
                        }
                        visibleMonth = next.timeInMillis
                        selectedDay = startOfCalendarDay(next.timeInMillis)
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = uiText(isPortuguese, "Mês seguinte", "Next month"))
                    }
                }

                Row(Modifier.fillMaxWidth()) {
                    weekNames.forEach { day ->
                        Text(day, Modifier.weight(1f).padding(vertical = 5.dp), color = palette.onSurfaceVariant, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 270.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(dayCells) { _, day ->
                        if (day == null) {
                            Spacer(Modifier.size(38.dp))
                        } else {
                            val dayCalendar = Calendar.getInstance().apply {
                                timeInMillis = visibleMonth
                                set(Calendar.DAY_OF_MONTH, day)
                            }
                            val dayKey = startOfCalendarDay(dayCalendar.timeInMillis)
                            val selected = dayKey == selectedDay
                            val hasReminder = reminderDays.contains(dayKey)
                            Column(
                                Modifier.fillMaxWidth().padding(1.dp).clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) palette.primaryContainer else Color.Transparent)
                                    .clickable { selectedDay = dayKey }
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    day.toString(),
                                    color = if (selected) palette.onPrimaryContainer else palette.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                                Box(
                                    Modifier.padding(top = 3.dp).size(4.dp).clip(CircleShape)
                                        .background(
                                            if (hasReminder) {
                                                if (selected) palette.onPrimaryContainer else palette.tertiary
                                            } else Color.Transparent
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            uiText(isPortuguese, "Lembretes de ", "Reminders for ") + selectedDateTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (notesForDay.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = palette.primary, modifier = Modifier.size(32.dp))
                    Text(uiText(isPortuguese, "Nenhum lembrete neste dia", "No reminders on this day"), color = palette.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notesForDay, key = { it.id }) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenNote(note) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = palette.surface)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = palette.primary)
                            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                Text(
                                    note.title.ifBlank { uiText(isPortuguese, "Nota sem título", "Untitled note") },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (note.content.isNotBlank()) Text(
                                    note.content,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.onSurfaceVariant
                                )
                            }
                            Text(
                                SimpleDateFormat("HH:mm", locale).format(Date(note.reminderAt)),
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun startOfCalendarDay(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis
