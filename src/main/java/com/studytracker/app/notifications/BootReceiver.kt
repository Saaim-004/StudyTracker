package com.studytracker.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.studytracker.app.data.TaskRepository
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val request = OneTimeWorkRequestBuilder<RescheduleWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}

/** Runs off the main thread so we don't block the boot broadcast while re-arming every alarm. */
class RescheduleWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        return try {
            runBlocking { TaskRepository(applicationContext).rescheduleAllReminders() }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
