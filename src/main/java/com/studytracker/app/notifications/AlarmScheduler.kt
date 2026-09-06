package com.studytracker.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.studytracker.app.data.Task
import java.text.SimpleDateFormat
import java.util.*

/**
 * Schedules the three reminders for a task:
 *   1) 2 days before the due date/time      -> normal notification
 *   2) 1 day before the due date/time       -> alarm-sound notification
 *   3) 5:00 AM on the due date itself       -> alarm-sound notification
 *
 * Uses AlarmManager.setExactAndAllowWhileIdle so reminders fire even in Doze mode.
 * Alarms are cleared by the OS on reboot, so BootReceiver re-arms everything.
 */
class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminders(task: Task) {
        if (task.isCompleted) return
        val now = System.currentTimeMillis()

        val twoDaysBefore = task.dueAtMillis - TimeUnit_DAY * 2
        val oneDayBefore = task.dueAtMillis - TimeUnit_DAY

        val fiveAmCalendar = Calendar.getInstance().apply {
            timeInMillis = task.dueAtMillis
            set(Calendar.HOUR_OF_DAY, 5)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val fiveAmDueDay = fiveAmCalendar.timeInMillis

        val dueDateLabel = SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault())
            .format(Date(task.dueAtMillis))

        if (twoDaysBefore > now) {
            schedule(
                task, ReminderType.TWO_DAYS_BEFORE, twoDaysBefore,
                title = "Due in 2 days: ${task.title}",
                body = "${task.type.label} for ${task.subject.ifBlank { "your class" }} is due $dueDateLabel."
            )
        }
        if (oneDayBefore > now) {
            schedule(
                task, ReminderType.ONE_DAY_BEFORE_ALARM, oneDayBefore,
                title = "\u23F0 Due tomorrow: ${task.title}",
                body = "${task.type.label} for ${task.subject.ifBlank { "your class" }} is due $dueDateLabel. Don't forget!"
            )
        }
        if (fiveAmDueDay > now) {
            schedule(
                task, ReminderType.FIVE_AM_DUE_DAY, fiveAmDueDay,
                title = "\u23F0 Due TODAY: ${task.title}",
                body = "${task.type.label} for ${task.subject.ifBlank { "your class" }} is due today at " +
                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(task.dueAtMillis)) + "."
            )
        }
    }

    fun cancelReminders(task: Task) {
        ReminderType.values().forEach { type ->
            val pi = buildPendingIntent(task, type)
            alarmManager.cancel(pi)
        }
    }

    private fun schedule(task: Task, type: ReminderType, triggerAtMillis: Long, title: String, body: String) {
        val pendingIntent = buildPendingIntent(task, type, title, body)
        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            // Falls back to an inexact alarm if the user hasn't granted "Alarms & reminders" permission.
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun buildPendingIntent(
        task: Task,
        type: ReminderType,
        title: String? = null,
        body: String? = null
    ): PendingIntent {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(ReminderBroadcastReceiver.EXTRA_TASK_ID, task.id)
            putExtra(ReminderBroadcastReceiver.EXTRA_TYPE, type.ordinalCode)
            title?.let { putExtra(ReminderBroadcastReceiver.EXTRA_TITLE, it) }
            body?.let { putExtra(ReminderBroadcastReceiver.EXTRA_BODY, it) }
        }
        val requestCode = (task.id * 10 + type.ordinalCode).toInt()
        return PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val TimeUnit_DAY = 24 * 60 * 60 * 1000L
    }
}
