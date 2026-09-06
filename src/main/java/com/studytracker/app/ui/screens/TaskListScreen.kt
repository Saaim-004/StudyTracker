package com.studytracker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studytracker.app.data.Task
import com.studytracker.app.ui.components.AppBackground
import com.studytracker.app.ui.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    customBackgroundUri: String?,
    backgroundOpacity: Float,
    onAddTask: () -> Unit,
    onOpenTask: (Task) -> Unit,
    onToggleCompleted: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onOpenThemeSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StudyTracker") },
                actions = {
                    IconButton(onClick = onOpenThemeSettings) {
                        Icon(Icons.Default.Palette, contentDescription = "Theme settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddTask, icon = {
                Icon(Icons.Default.Add, contentDescription = null)
            }, text = { Text("New task") })
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->
        AppBackground(customImageUri = customBackgroundUri, opacity = backgroundOpacity) {
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "No assignments or quizzes yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tap \u201cNew task\u201d to add your first deadline.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val sorted = tasks.sortedWith(compareBy({ it.isCompleted }, { it.dueAtMillis }))
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = padding.calculateTopPadding() + 12.dp, bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sorted, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggleCompleted = { onToggleCompleted(task) },
                            onClick = { onOpenTask(task) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}
