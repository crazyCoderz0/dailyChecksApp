package com.crazycoderz.dailychecks.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.reminderDataStore by preferencesDataStore(name = "reminder_preferences")

class ReminderPreferences(private val context: Context) {

    val settings: Flow<ReminderSettings> = context.reminderDataStore.data.map { prefs ->
        ReminderSettings(
            enabled = prefs[KEY_ENABLED] ?: false,
            hour = prefs[KEY_HOUR] ?: 20,
            minute = prefs[KEY_MINUTE] ?: 0,
        )
    }

    suspend fun setEnabled(enabled: Boolean) {
        context.reminderDataStore.edit { prefs ->
            prefs[KEY_ENABLED] = enabled
        }
    }

    suspend fun setTime(hour: Int, minute: Int) {
        context.reminderDataStore.edit { prefs ->
            prefs[KEY_HOUR] = hour
            prefs[KEY_MINUTE] = minute
        }
    }

    companion object {
        private val KEY_ENABLED = booleanPreferencesKey("enabled")
        private val KEY_HOUR = intPreferencesKey("hour")
        private val KEY_MINUTE = intPreferencesKey("minute")
    }
}

data class ReminderSettings(
    val enabled: Boolean,
    val hour: Int,
    val minute: Int,
)

