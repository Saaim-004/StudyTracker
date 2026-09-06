package com.studytracker.app.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.studytracker.app.data.Task
import com.studytracker.app.data.TaskType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    existingTask: Task?,
    onSave: (Task) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var subject by remember { mutableStateOf(existingTask?.subject ?: "") }
    var notes by remember { mutableStateOf(existingTask?.notes ?: "") }
    var type by remember { mutableStateOf(existingTask?.type ?: TaskType.ASSIGNMENT) }
    var dueAtMillis by remember {
        mutableStateOf(existingTask?.dueAtMillis ?: (System.currentTimeMillis() + 24 * 60 * 60 * 1000))
    }
    var typeMenuExpanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance().apply { timeInMillis = dueAtMillis }
    val dateLabel = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date(dueAtMillis))
    val timeLabel = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(dueAtMillis))

    fun pickDate() {
        // Pulls the current date straight from the device's own calendar/clock.
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val c = Calendar.getInstance().apply { timeInMillis = dueAtMillis }
                c.set(year, month, day)
                dueAtMillis = c.timeInMillis
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun pickTime() {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val c = Calendar.getInstance().apply { timeInMillis = dueAtMillis }
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                dueAtMillis = c.timeInMillis
            },
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingTask == null) "New task" else "Edit task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = subject, onValueChange = { subject = it },
                label = { Text("Subject / Class") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            ExposedDropdownMenuBox(expanded = typeMenuExpanded, onExpandedChange = { typeMenuExpanded = it }) {
                OutlinedTextField(
                    value = type.label, onValueChange = {}, readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = typeMenuExpanded, onDismissRequest = { typeMenuExpanded = false }) {
                    TaskType.values().forEach { option ->
                        DropdownMenuItem(text = { Text(option.label) }, onClick = {
                            type = option
                            typeMenuExpanded = false
                        })
                    }
                }
            }

            Text("Due date & time", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { pickDate() }, modifier = Modifier.weight(1f)) { Text(dateLabel) }
                OutlinedButton(onClick = { pickTime() }, modifier = Modifier.weight(1f)) { Text(timeLabel) }
            }
            Text(
                "You'll get a reminder 2 days before, an alarm 1 day before, and another alarm at 5 AM on the due day.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Button(
                onClick = {
                    onSave(
                        (existingTask ?: Task(title = "", subject = "", type = TaskType.ASSIGNMENT, dueAtMillis = 0))
                            .copy(
                                title = title.ifBlank { "Untitled" },
                                subject = subject,
                                type = type,
                                dueAtMillis = dueAtMillis,
                                notes = notes
                            )
                    )
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
