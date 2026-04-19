package com.crazycoderz.dailychecks.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.bootReminderDataStore by preferencesDataStore(name = "reminder_preferences")

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        CoroutineScope(Dispatchers.IO).launch {
            val prefs = context.bootReminderDataStore.data.first()
            val enabled = prefs[booleanPreferencesKey("enabled")] ?: false
            val hour = prefs[intPreferencesKey("hour")] ?: 20
            val minute = prefs[intPreferencesKey("minute")] ?: 0

            if (enabled) {
                ReminderScheduler(context).schedule(hour, minute)
            }
        }
    }
}

