package com.crazycoderz.dailychecks.ui

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.crazycoderz.dailychecks.DailyChecksApplication
import com.crazycoderz.dailychecks.data.local.ChecklistItemEntity
import com.crazycoderz.dailychecks.data.repository.ChecklistRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.LocalDate
import java.util.Date

class MainViewModel(
    application: Application,
    private val repository: ChecklistRepository,
) : AndroidViewModel(application) {

    private val uiEvents = MutableSharedFlow<UiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = uiEvents

    private var monetizedActionCount = 0

    val uiState: StateFlow<MainUiState> = combine(
        repository.items,
        repository.reminderSettings,
    ) { items, reminder ->
        MainUiState(
            items = items.map { it.toUiModel() },
            reminderEnabled = reminder.enabled,
            reminderHour = reminder.hour,
            reminderMinute = reminder.minute,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiState(),
    )

    init {
        viewModelScope.launch {
            repository.ensureDefaultItems()
        }
    }

    fun checkItem(itemId: Long) {
        viewModelScope.launch {
            val currentItem = repository.getCurrentItems().firstOrNull { it.id == itemId } ?: return@launch
            repository.checkItem(currentItem)
            onMonetizedAction()
        }
    }

    fun addItem(title: String) {
        val trimmed = title.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            repository.addItem(trimmed)
            onMonetizedAction()
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateReminderEnabled(enabled)
            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                uiEvents.tryEmit(UiEvent.RequestNotificationPermission)
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.updateReminderTime(hour, minute)
        }
    }

    fun onNotificationPermissionDenied() {
        // Notification permission is optional; reminder stays scheduled for older Android versions.
    }

    private fun onMonetizedAction() {
        monetizedActionCount += 1
        if (monetizedActionCount % 4 == 0) {
            uiEvents.tryEmit(UiEvent.ShowInterstitialAd)
        }
    }

    sealed interface UiEvent {
        data object ShowInterstitialAd : UiEvent
        data object RequestNotificationPermission : UiEvent
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = application as DailyChecksApplication
                    return MainViewModel(
                        application = application,
                        repository = app.repository,
                    ) as T
                }
            }
    }
}

data class MainUiState(
    val items: List<ChecklistItemUiModel> = emptyList(),
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
)

data class ChecklistItemUiModel(
    val id: Long,
    val title: String,
    val lastCheckedLabel: String,
    val streakLabel: String,
    val isCheckedToday: Boolean,
)

private fun ChecklistItemEntity.toUiModel(): ChecklistItemUiModel {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    val checkedToday = lastCheckedDate == LocalDate.now().toString()
    return ChecklistItemUiModel(
        id = id,
        title = title,
        lastCheckedLabel = lastCheckedAt?.let { formatter.format(Date(it)) } ?: "Not checked yet",
        streakLabel = if (streakCount == 1) "1 day" else "$streakCount days",
        isCheckedToday = checkedToday,
    )
}
