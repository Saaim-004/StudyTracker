package com.studytracker.app.notifications

/**
 * The three reminder stages requested:
 *  - TWO_DAYS_BEFORE: quiet heads-up, 2 days before the due date/time.
 *  - ONE_DAY_BEFORE_ALARM: loud, alarm-sound notification, 1 day before.
 *  - FIVE_AM_DUE_DAY: alarm-style wake-up reminder at 5:00 AM on the due day itself.
 */
enum class ReminderType(val ordinalCode: Int, val usesAlarmSound: Boolean) {
    TWO_DAYS_BEFORE(0, usesAlarmSound = false),
    ONE_DAY_BEFORE_ALARM(1, usesAlarmSound = true),
    FIVE_AM_DUE_DAY(2, usesAlarmSound = true)
}
