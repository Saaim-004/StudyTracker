package com.studytracker.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyColumnItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as lazyGridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studytracker.app.data.Task
import com.studytracker.app.ui.theme.CalendarOverloadColor
import com.studytracker.app.ui.theme.colorForSubject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

private data class DayInfo(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
    val colors: List<Color>,
    val isOverloaded: Boolean,
    val tasks: List<Task>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    tasks: List<Task>,
    onOpenTask: (Task) -> Unit,
    onBack: () -> Unit
) {
    val zone = ZoneId.systemDefault()
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Group every task by the calendar day it's due on (in the device's local timezone).
    val tasksByDate = remember(tasks) {
        tasks.groupBy {
            Date(it.dueAtMillis).toInstant().atZone(zone).toLocalDate()
        }
    }

    val firstOfMonth = visibleMonth.atDay(1)
    // Sunday-start grid: shift back to the most recent Sunday on/before the 1st.
    val gridStart = firstOfMonth.minusDays(firstOfMonth.dayOfWeek.value.toLong() % 7)
    val days = (0 until 42).map { offset ->
        val date = gridStart.plusDays(offset.toLong())
        val dayTasks = tasksByDate[date].orEmpty()
        val distinctColors = dayTasks.map { colorForSubject(it.subject) }.distinct()
        DayInfo(
            date = date,
            inCurrentMonth = YearMonth.from(date) == visibleMonth,
            colors = distinctColors,
            isOverloaded = distinctColors.size > 2,
            tasks = dayTasks
        )
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Calendar") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            }
        )
    }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            // Month switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { visibleMonth = visibleMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                }
                Text(
                    "${visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${visibleMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { visibleMonth = visibleMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "3+ subjects on one day turns the date red \u2014 that's your overload warning.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            // Weekday header
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(label, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp)
            ) {
                lazyGridItems(days) { day ->
                    DayCell(
                        day = day,
                        isSelected = day.date == selectedDate,
                        isToday = day.date == LocalDate.now(),
                        onClick = { selectedDate = if (selectedDate == day.date) null else day.date }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            val dayTasks = selectedDate?.let { tasksByDate[it].orEmpty() } ?: emptyList()
            if (selectedDate == null) {
                Text(
                    "Tap a date to see what's due that day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (dayTasks.isEmpty()) {
                Text("Nothing due on this date.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
                        .format(Date.from(selectedDate!!.atStartOfDay(zone).toInstant())),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    lazyColumnItems(dayTasks, key = { it.id }) { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenTask(task) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(colorForSubject(task.subject))
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(task.title, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "${task.type.label} \u00b7 ${task.subject.ifBlank { "General" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(day: DayInfo, isSelected: Boolean, isToday: Boolean, onClick: () -> Unit) {
    val cellColor = when {
        day.isOverloaded -> CalendarOverloadColor
        else -> Color.Transparent
    }
    val textColor = when {
        day.isOverloaded -> Color.White
        !day.inCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(cellColor)
            .then(
                if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                else Modifier
            )
            .then(
                if (isToday && !isSelected) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                else Modifier
            )
            .clickable(enabled = day.inCurrentMonth) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(day.date.dayOfMonth.toString(), color = textColor, style = MaterialTheme.typography.bodyMedium)
            if (!day.isOverloaded && day.colors.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    day.colors.take(2).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}
