package com.studytracker.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.studytracker.app.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)
    private val themePrefs = ThemePreferences(application)

    val tasks = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings = themePrefs.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun saveTask(task: Task) = viewModelScope.launch { repository.saveTask(task) }

    fun deleteTask(task: Task) = viewModelScope.launch { repository.deleteTask(task) }

    fun toggleCompleted(task: Task) = viewModelScope.launch {
        repository.setCompleted(task.id, !task.isCompleted)
    }

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { themePrefs.setThemeMode(mode) }

    fun setPreset(preset: ThemePreset) = viewModelScope.launch { themePrefs.setPreset(preset) }

    fun setBackgroundUri(uri: String?) = viewModelScope.launch { themePrefs.setBackgroundUri(uri) }

    fun setBackgroundOpacity(opacity: Float) = viewModelScope.launch { themePrefs.setBackgroundOpacity(opacity) }
}
