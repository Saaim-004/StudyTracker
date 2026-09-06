# StudyTracker — Android App

A native Android app (Kotlin + Jetpack Compose) for tracking assignments, quizzes, presentations,
and exams, with staged deadline reminders.

## What's included

**Appearance**
- Light / Dark / System theme toggle
- A collection of 6 built-in color themes (Ocean, Lavender, Forest, Sunset, Monochrome, Rose)
- Upload a custom photo as the app background (from the device's own photo picker), with an
  adjustable dimness slider so text stays readable over any image

**Task management**
- Add/edit/delete assignments, quizzes, presentations, and exams with a title, subject, due
  date & time, and notes
- The due date/time comes straight from the device's own date/time picker (i.e. its system
  clock/calendar), exactly as requested
- Mark tasks complete; list is sorted by due date, completed items sink to the bottom

**Reminders (the core request)**
Every task automatically schedules three alarms, using Android's `AlarmManager` so they fire
even if the app is closed or the phone is idle:
1. **2 days before** the due date/time — a normal notification
2. **1 day before** the due date/time — a notification through the "alarm" channel, which plays
   the device's default alarm sound
3. **5:00 AM on the due date itself** — another alarm-sound notification as a same-day wake-up

If the phone reboots, a `BOOT_COMPLETED` receiver re-arms every alarm (Android clears exact
alarms on reboot, so this is required for reminders to keep working).

## Project structure

```
app/src/main/java/com/studytracker/app/
├── MainActivity.kt                 – requests permissions, hosts the Compose UI
├── StudyTrackerApp.kt              – Application class, creates notification channels
├── data/                           – Room database (tasks) + DataStore (theme/background prefs)
├── notifications/                  – AlarmScheduler, BroadcastReceiver, BootReceiver, channels
└── ui/
    ├── theme/                      – Material3 theme + the 6 color presets
    ├── screens/                    – Task list, Add/Edit task, Appearance settings
    └── components/                 – TaskCard, AppBackground (renders the custom photo)
```

## Opening & running it

1. Install **Android Studio** (Koala/2024.1 or newer).
2. Open the `StudyTracker` folder as a project (File → Open).
3. Let Gradle sync — it will download the AndroidX/Compose/Room dependencies automatically.
4. Run on an emulator or a physical device with **Android 8.0 (API 26) or newer**.
5. On first launch the app asks for:
   - **Notification permission** (Android 13+)
   - **"Alarms & reminders" permission** (Android 12+, needed for the reminders to fire at the
     *exact* minute rather than being delayed by the OS) — this opens a system settings screen
     once; just toggle it on and go back.

## Notes & things you may want to tweak

- **Alarm sound**: reminders use the device's own default alarm sound (via `RingtoneManager`).
  If you'd rather ship a specific sound file, drop an mp3 into `res/raw/` and point the
  `alarmChannel.setSound(...)` call in `NotificationHelper.kt` at it instead.
- **App icon**: I included a simple placeholder adaptive icon (`ic_launcher_background/foreground`)
  — swap those vector drawables for real artwork whenever you're ready.
- **Exact-alarm battery notice**: some phone brands (Samsung, Xiaomi, etc.) aggressively kill
  background alarms unless the app is also excluded from battery optimization — worth testing
  reminders on your actual device, not just the emulator.
- This project isn't compiled/tested in a real Android SDK here (this sandbox only has network
  access to a small allow-list of domains, not Google's Maven repo), so do a first Gradle sync
  and skim for any dependency-version hiccups — the code itself follows current, standard
  Compose/Room/AlarmManager patterns.
