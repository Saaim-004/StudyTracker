package com.studytracker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY dueAtMillis ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("SELECT * FROM tasks WHERE remindersScheduled = 0")
    suspend fun getTasksNeedingReminders(): List<Task>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    suspend fun getAllActiveTasksOnce(): List<Task>

    @Query("UPDATE tasks SET remindersScheduled = :scheduled WHERE id = :id")
    suspend fun setRemindersScheduled(id: Long, scheduled: Boolean)
}
