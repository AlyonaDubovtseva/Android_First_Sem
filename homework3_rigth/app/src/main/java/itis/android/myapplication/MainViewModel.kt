package itis.android.myapplication

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import itis.android.myapplication.notifications.NotificationConfig
import itis.android.myapplication.notifications.NotificationHelper
import itis.android.myapplication.notifications.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    val messages = NotificationRepository.messages
    val notifications = NotificationRepository.notifications
    val lastNotificationId = NotificationRepository.lastNotificationId

    private var nextNotificationId = 1

    private val _intentData = MutableStateFlow<NotificationIntentData?>(null)
    val intentData: StateFlow<NotificationIntentData?> = _intentData

    fun addUserMessage(text: String) {
        NotificationRepository.addMessage(text)
    }

    fun createNotification(context: Context, config: NotificationConfig): Int {
        val id = nextNotificationId++
        NotificationRepository.addOrUpdateNotification(id, config)
        NotificationHelper.showNotification(context, id, config)
        return id
    }

    fun updateNotification(context: Context, notificationId: Int, newText: String): Boolean {
        if (!isNotificationActive(context, notificationId)) {
            NotificationRepository.removeNotification(notificationId)
            return false
        }
        val existing = notifications.value[notificationId] ?: return false
        val message = newText.trim().takeUnless { it.isEmpty() }
        val updated = existing.copy(message = message, shouldExpand = existing.shouldExpand && message != null)
        NotificationRepository.addOrUpdateNotification(notificationId, updated)
        NotificationHelper.showNotification(context, notificationId, updated)
        return true
    }

    fun clearNotifications(context: Context): Boolean {
        val hasAny = notifications.value.isNotEmpty()
        if (!hasAny) return false
        NotificationRepository.clearNotifications()
        NotificationManagerCompat.from(context).cancelAll()
        return true
    }

    fun handleIncomingNotificationIntent(intent: Intent?) {
        if (intent?.hasExtra(NotificationHelper.EXTRA_TITLE) != true) return
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE)
        _intentData.value = NotificationIntentData(title = title, message = message)
    }

    data class NotificationIntentData(
        val title: String,
        val message: String?
    )

    private fun isNotificationActive(context: Context, notificationId: Int): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        val manager = context.getSystemService(NotificationManager::class.java) ?: return false
        return manager.activeNotifications.any { it.id == notificationId }
    }
}

