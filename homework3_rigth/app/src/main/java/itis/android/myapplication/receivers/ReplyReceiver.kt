package itis.android.myapplication.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import itis.android.myapplication.notifications.NotificationHelper
import itis.android.myapplication.notifications.NotificationRepository

class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val results = RemoteInput.getResultsFromIntent(intent) ?: return
        val reply = results.getCharSequence(NotificationHelper.REMOTE_INPUT_KEY)
            ?.toString()
            ?.trim()
            ?: return

        if (reply.isEmpty()) return

        NotificationRepository.addMessage(reply)

        val notificationId = intent?.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, -1) ?: -1
        if (notificationId != -1) {
            NotificationRepository.removeNotification(notificationId)
            NotificationManagerCompat.from(context).cancel(notificationId)
        }
    }
}

