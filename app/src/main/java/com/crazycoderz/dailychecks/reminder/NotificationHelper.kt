package com.crazycoderz.dailychecks.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.crazycoderz.dailychecks.R

object NotificationHelper {

    const val CHANNEL_ID = "daily_checks_reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val soundUri = defaultNotificationSound()
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notification_channel_description)
            enableVibration(true)
            setSound(
                soundUri,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showReminder(context: Context) {
        showNotification(
            context = context,
            notificationId = 2001,
            title = context.getString(R.string.notification_title),
            body = context.getString(R.string.notification_body),
        )
    }

    fun showPushNotification(
        context: Context,
        title: String,
        body: String,
    ) {
        showNotification(
            context = context,
            notificationId = 2002,
            title = title,
            body = body,
        )
    }

    private fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        body: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(defaultNotificationSound())
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun defaultNotificationSound(): Uri = Settings.System.DEFAULT_NOTIFICATION_URI
}