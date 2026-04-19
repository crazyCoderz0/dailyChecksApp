package com.crazycoderz.dailychecks.data.repository

import com.crazycoderz.dailychecks.data.local.ChecklistItemDao
import com.crazycoderz.dailychecks.data.local.ChecklistItemEntity
import com.crazycoderz.dailychecks.data.preferences.ReminderPreferences
import com.crazycoderz.dailychecks.data.preferences.ReminderSettings
import com.crazycoderz.dailychecks.domain.StreakCalculator
import com.crazycoderz.dailychecks.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import java.time.LocalDate

class ChecklistRepository(
    private val dao: ChecklistItemDao,
    private val reminderPreferences: ReminderPreferences,
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock = Clock.systemDefaultZone(),
) {

    val items: Flow<List<ChecklistItemEntity>> = dao.observeItems()
    val reminderSettings: Flow<ReminderSettings> = reminderPreferences.settings

    suspend fun ensureDefaultItems() {
        if (dao.getItems().isNotEmpty()) return

        val now = clock.millis()
        dao.insertItems(
            listOf(
                ChecklistItemEntity(title = "Lock Door", createdAt = now),
                ChecklistItemEntity(title = "Turn Off Stove", createdAt = now + 1),
                ChecklistItemEntity(title = "Close Garage", createdAt = now + 2),
            ),
        )
    }

    suspend fun addItem(title: String) {
        dao.insertItem(
            ChecklistItemEntity(
                title = title.trim(),
                createdAt = clock.millis(),
            ),
        )
    }

    suspend fun checkItem(item: ChecklistItemEntity) {
        val today = LocalDate.now(clock)
        val result = StreakCalculator.calculate(
            previousDate = item.lastCheckedDate?.let(LocalDate::parse),
            previousStreak = item.streakCount,
            today = today,
        )
        val updated = item.copy(
            lastCheckedAt = Instant.now(clock).toEpochMilli(),
            lastCheckedDate = result.checkedDate.toString(),
            streakCount = result.streakCount,
        )
        dao.updateItem(updated)
    }

    suspend fun updateReminderEnabled(enabled: Boolean) {
        reminderPreferences.setEnabled(enabled)
        val settings = reminderPreferences.settingsSnapshot()
        if (enabled) {
            reminderScheduler.schedule(settings.hour, settings.minute)
        } else {
            reminderScheduler.cancel()
        }
    }

    suspend fun updateReminderTime(hour: Int, minute: Int) {
        reminderPreferences.setTime(hour, minute)
        val settings = reminderPreferences.settingsSnapshot()
        if (settings.enabled) {
            reminderScheduler.schedule(hour, minute)
        }
    }

    suspend fun getCurrentItems(): List<ChecklistItemEntity> = dao.getItems()

    suspend fun currentReminderSettings(): ReminderSettings = reminderPreferences.settingsSnapshot()
}

private suspend fun ReminderPreferences.settingsSnapshot(): ReminderSettings {
    return settings.first()
}
