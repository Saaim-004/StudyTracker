package com.studytracker.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.studytracker.app.MainActivity
import com.studytracker.app.R

object NotificationHelper {

    const val CHANNEL_STANDARD = "reminders_standard"
    const val CHANNEL_ALARM = "reminders_alarm"

    fun ensureChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val standard = NotificationChannel(
            CHANNEL_STANDARD,
            "Upcoming deadlines",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Heads-up reminders a couple of days ahead" }

        val alarmSoundAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val alarmChannel = NotificationChannel(
            CHANNEL_ALARM,
            "Urgent deadline alarms",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Loud alarm-style reminders for tomorrow's and today's deadlines"
            setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM), alarmSoundAttributes)
            enableVibration(true)
        }

        manager.createNotificationChannel(standard)
        manager.createNotificationChannel(alarmChannel)
    }

    fun showReminder(context: Context, taskId: Long, title: String, body: String, type: ReminderType) {
        val channel = if (type.usesAlarmSound) CHANNEL_ALARM else CHANNEL_STANDARD

        val openIntent = android.content.Intent(context, MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, taskId.toInt(), openIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(if (type.usesAlarmSound) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(if (type.usesAlarmSound) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = (taskId * 10 + type.ordinalCode).toInt()
        NotificationManagerCompat.from(context).apply {
            // Caller must already hold POST_NOTIFICATIONS on Android 13+; MainActivity requests it on launch.
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, notification)
            }
        }
    }
}
