package com.no5ing.bbibbi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class FirebaseService : FirebaseMessagingService() {
    companion object {
        const val channel_id = "bbibbi_channel"
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("[FirebaseService] onMessageReceived notification: ${remoteMessage.notification}")
        Timber.d("[FirebaseService] onMessageReceived data: ${remoteMessage.data}")
        remoteMessage.notification?.apply {
            val intent = Intent(this@FirebaseService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    .or(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    .or(Intent.FLAG_ACTIVITY_NEW_TASK)
                remoteMessage.data.forEach { (t, u) ->
                    Timber.d("Extra data: $t, $u")
                    putExtra(t, u)
                }
            }
            val pendingIntent = PendingIntent
                .getActivity(
                    this@FirebaseService, 0, intent, PendingIntent.FLAG_IMMUTABLE
                        .or(PendingIntent.FLAG_CANCEL_CURRENT)
                )
            val builder = NotificationCompat
                .Builder(this@FirebaseService, channel_id)
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(0xFF3FD960.toInt())
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel()
            notificationManager.notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channel_id, "삐삐", importance)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}