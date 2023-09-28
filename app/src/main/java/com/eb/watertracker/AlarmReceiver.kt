package com.eb.watertracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        createNotification(context)
        sendNotification(context)
    }

    // Kanal kullanma sebebi ;
    // bildirimlerin gruplandırılması, önceliklerinin belirlenmesi ve kullanıcıya bildirim tercihlerini yönetme imkanı sağlar.
    fun createNotification(context: Context) {
        var notificationManager: NotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel_id = "com.eb.watertracker"
            val message = "Water Tracker Channel"
            val priority = NotificationManager.IMPORTANCE_HIGH //.IMPORTANCE_DEFAULT
            val lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            var notificationChannel = NotificationChannel(channel_id, message, priority)
            notificationChannel.lockscreenVisibility = lockscreenVisibility // Kilit ekranı kapalıysa da bildirim gösterilecek.
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun sendNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val activityIntent = Intent(context, MainActivity::class.java)
        val id = 1
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder = NotificationCompat.Builder(context, "com.eb.watertracker")
            .setContentTitle("Water Tracker")
            .setContentText("It's time to drink water!!!")
            .setSmallIcon(R.drawable.glass)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, builder.build())
        //  Eğer bu değeri sabit bir değer olarak belirlerseniz,
        //  her zaman aynı bildirim üzerine yazılır ve eski bildirimin üzerine yeni bildirimi getirir.

    }
}
