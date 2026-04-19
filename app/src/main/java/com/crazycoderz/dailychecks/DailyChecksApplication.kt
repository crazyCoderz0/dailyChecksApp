package com.crazycoderz.dailychecks

import android.app.Application
import android.util.Log
import com.crazycoderz.dailychecks.data.local.AppDatabase
import com.crazycoderz.dailychecks.data.preferences.ReminderPreferences
import com.crazycoderz.dailychecks.data.repository.ChecklistRepository
import com.crazycoderz.dailychecks.reminder.NotificationHelper
import com.crazycoderz.dailychecks.reminder.ReminderScheduler
import com.google.android.gms.ads.MobileAds
import com.google.firebase.messaging.FirebaseMessaging


class DailyChecksApplication : Application() {

    companion object {
        private const val TAG = "DailyChecksApp"
    }

    val database by lazy { AppDatabase.getInstance(this) }
    val reminderPreferences by lazy { ReminderPreferences(this) }
    val reminderScheduler by lazy { ReminderScheduler(this) }
    val repository by lazy {
        ChecklistRepository(
            dao = database.checklistItemDao(),
            reminderPreferences = reminderPreferences,
            reminderScheduler = reminderScheduler,
        )
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        NotificationHelper.createNotificationChannel(this)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                Log.d(TAG, "FCM token: ${task.result}")
            }
    }
}