package com.studytracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single assignment / quiz / presentation / exam the student needs to track.
 * [dueAtMillis] stores the exact due date+time in device-local epoch millis,
 * synced straight from the date/time picker (i.e. the device's own clock/calendar).
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val type: TaskType,
    val dueAtMillis: Long,
    val notes: String = "",
    val isCompleted: Boolean = false,
    // Tracks which reminders have already been scheduled so we don't double-book alarms.
    val remindersScheduled: Boolean = false
)
