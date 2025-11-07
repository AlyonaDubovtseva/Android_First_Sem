package itis.android.myapplication.notifications

import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import itis.android.myapplication.R

enum class NotificationImportance(@StringRes val labelRes: Int) {
    LOW(R.string.priority_low),
    MEDIUM(R.string.priority_medium),
    HIGH(R.string.priority_high),
    MAX(R.string.priority_max);

    fun toChannelImportance(): Int = when (this) {
        LOW -> android.app.NotificationManager.IMPORTANCE_LOW
        MEDIUM -> android.app.NotificationManager.IMPORTANCE_DEFAULT
        HIGH -> android.app.NotificationManager.IMPORTANCE_HIGH
        MAX -> android.app.NotificationManager.IMPORTANCE_MAX
    }

    fun toCompatPriority(): Int = when (this) {
        LOW -> NotificationCompat.PRIORITY_LOW
        MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
        HIGH -> NotificationCompat.PRIORITY_HIGH
        MAX -> NotificationCompat.PRIORITY_MAX
    }
}

data class NotificationConfig(
    val title: String,
    val message: String?,
    val importance: NotificationImportance,
    val shouldExpand: Boolean,
    val shouldOpenMain: Boolean,
    val hasReply: Boolean
)

