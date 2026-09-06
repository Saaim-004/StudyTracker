package com.studytracker.app.data

import android.content.Context
import com.studytracker.app.notifications.AlarmScheduler
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val context: Context) {

    private val dao = AppDatabase.getInstance(context).taskDao()
    private val scheduler = AlarmScheduler(context)

    fun getAllTasks(): Flow<List<Task>> = dao.getAllTasks()

    suspend fun saveTask(task: Task): Long {
        // Cancel any previously scheduled alarms for this task first (covers edits to the due date).
        if (task.id != 0L) scheduler.cancelReminders(task)
        val id = dao.upsertTask(task.copy(remindersScheduled = false))
        val saved = task.copy(id = if (task.id == 0L) id else task.id)
        scheduler.scheduleReminders(saved)
        dao.setRemindersScheduled(saved.id, true)
        return id
    }

    suspend fun deleteTask(task: Task) {
        scheduler.cancelReminders(task)
        dao.deleteTask(task)
    }

    suspend fun setCompleted(id: Long, completed: Boolean) = dao.setCompleted(id, completed)

    /** Called after a device reboot to re-arm every alarm, since exact alarms don't survive a reboot. */
    suspend fun rescheduleAllReminders() {
        dao.getAllActiveTasksOnce().forEach { scheduler.scheduleReminders(it) }
    }
}
