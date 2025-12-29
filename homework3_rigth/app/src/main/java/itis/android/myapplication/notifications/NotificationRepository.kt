package itis.android.myapplication.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NotificationRepository {

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages.asStateFlow()

    private val _notifications = MutableStateFlow<Map<Int, NotificationConfig>>(emptyMap())
    val notifications: StateFlow<Map<Int, NotificationConfig>> = _notifications.asStateFlow()

    private val _lastNotificationId = MutableStateFlow<Int?>(null)
    val lastNotificationId: StateFlow<Int?> = _lastNotificationId.asStateFlow()

    fun addMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        _messages.update { current -> current + trimmed }
    }

    fun addOrUpdateNotification(id: Int, config: NotificationConfig) {
        _notifications.update { current -> current + (id to config) }
        _lastNotificationId.value = id
    }

    fun removeNotification(id: Int) {
        _notifications.update { current ->
            val updated = current - id
            _lastNotificationId.value = updated.keys.maxOrNull()
            updated
        }
    }

    fun clearNotifications() {
        _notifications.value = emptyMap()
        _lastNotificationId.value = null
    }
}

