package com.studytracker.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.studytracker.app.data.Task
import com.studytracker.app.ui.screens.AddEditTaskScreen
import com.studytracker.app.ui.screens.CalendarScreen
import com.studytracker.app.ui.screens.TaskListScreen
import com.studytracker.app.ui.screens.ThemeSettingsScreen
import com.studytracker.app.ui.theme.StudyTrackerTheme

private const val ROUTE_LIST = "list"
private const val ROUTE_ADD_EDIT = "add_edit?taskId={taskId}"
private const val ROUTE_THEME = "theme"
private const val ROUTE_CALENDAR = "calendar"

@Composable
fun StudyTrackerNavHost() {
    val viewModel: MainViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val navController = rememberNavController()

    StudyTrackerTheme(themeMode = settings.themeMode, preset = settings.preset) {
        NavHost(navController = navController, startDestination = ROUTE_LIST) {
            composable(ROUTE_LIST) {
                TaskListScreen(
                    tasks = tasks,
                    customBackgroundUri = settings.customBackgroundUri,
                    backgroundOpacity = settings.backgroundOpacity,
                    onAddTask = { navController.navigate("add_edit") },
                    onOpenTask = { task -> navController.navigate("add_edit?taskId=${task.id}") },
                    onToggleCompleted = { viewModel.toggleCompleted(it) },
                    onDeleteTask = { viewModel.deleteTask(it) },
                    onOpenThemeSettings = { navController.navigate(ROUTE_THEME) },
                    onOpenCalendar = { navController.navigate(ROUTE_CALENDAR) }
                )
            }
            composable(ROUTE_CALENDAR) {
                CalendarScreen(
                    tasks = tasks,
                    onOpenTask = { task -> navController.navigate("add_edit?taskId=${task.id}") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = ROUTE_ADD_EDIT,
                arguments = listOf(navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = 0L
                })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
                val existing: Task? = tasks.find { it.id == taskId }
                AddEditTaskScreen(
                    existingTask = existing,
                    onSave = { task ->
                        viewModel.saveTask(task)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ROUTE_THEME) {
                ThemeSettingsScreen(
                    settings = settings,
                    onThemeModeChange = { viewModel.setThemeMode(it) },
                    onPresetChange = { viewModel.setPreset(it) },
                    onBackgroundUriChange = { viewModel.setBackgroundUri(it) },
                    onOpacityChange = { viewModel.setBackgroundOpacity(it) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
