package com.studytracker.app

import android.app.Application
import com.studytracker.app.notifications.NotificationHelper

class StudyTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannels(this)
    }
}
