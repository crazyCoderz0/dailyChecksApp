package com.crazycoderz.dailychecks.messaging

import android.util.Log
import com.crazycoderz.dailychecks.reminder.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DailyChecksFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "DailyChecksFCM"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Daily Checks"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "You have a new update."

        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.showPushNotification(
            context = this,
            title = title,
            body = body,
        )
    }
}