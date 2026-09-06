package com.studytracker.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTaskType(type: TaskType): String = type.name

    @TypeConverter
    fun toTaskType(value: String): TaskType = TaskType.valueOf(value)
}
