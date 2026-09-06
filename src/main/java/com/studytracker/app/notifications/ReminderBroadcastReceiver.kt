package com.studytracker.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Reminder"
        val body = intent.getStringExtra(EXTRA_BODY) ?: ""
        val typeCode = intent.getIntExtra(EXTRA_TYPE, 0)
        val type = ReminderType.values().first { it.ordinalCode == typeCode }

        NotificationHelper.ensureChannels(context)
        NotificationHelper.showReminder(context, taskId, title, body, type)
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_TYPE = "extra_type"
    }
}
