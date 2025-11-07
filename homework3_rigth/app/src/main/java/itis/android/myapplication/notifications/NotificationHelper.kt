package itis.android.myapplication.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import itis.android.myapplication.MainActivity
import itis.android.myapplication.R
import itis.android.myapplication.receivers.ReplyReceiver

object NotificationHelper {

    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val REMOTE_INPUT_KEY = "reply_text"

    private const val CHANNEL_ID_PREFIX = "hw3_channel_"

    fun showNotification(context: Context, notificationId: Int, config: NotificationConfig) {
        val channelId = ensureChannel(context, config.importance)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(config.title)
            .setPriority(config.importance.toCompatPriority())
            .setAutoCancel(true)

        config.message?.takeIf { it.isNotBlank() }?.let { message ->
            builder.setContentText(message)
            if (config.shouldExpand) {
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            }
        }

        if (config.shouldOpenMain) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_TITLE, config.title)
                config.message?.let { putExtra(EXTRA_MESSAGE, it) }
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                flags
            )
            builder.setContentIntent(pendingIntent)
        }

        if (config.hasReply) {
            val replyLabel = context.getString(R.string.notification_reply_label)
            val remoteInput = RemoteInput.Builder(REMOTE_INPUT_KEY)
                .setLabel(replyLabel)
                .build()

            val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
                putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                putExtra(EXTRA_TITLE, config.title)
            }
            val mutableFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or mutableFlag
            )
            val action = NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                replyLabel,
                replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()
            builder.addAction(action)
        }

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    private fun ensureChannel(context: Context, importance: NotificationImportance): String {
        val channelId = CHANNEL_ID_PREFIX + importance.name.lowercase()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channelName = context.getString(R.string.notification_channel_name) +
                    " " + context.getString(importance.labelRes)
            val channel = NotificationChannel(
                channelId,
                channelName,
                importance.toChannelImportance()
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }
            manager?.createNotificationChannel(channel)
        }
        return channelId
    }
}

