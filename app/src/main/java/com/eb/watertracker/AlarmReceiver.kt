package com.eb.watertracker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
// Değişim olduğunda burası tetiklenicek .
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var builder = NotificationCompat.Builder(context, "com.eb.watertracker")
            .setContentTitle("Water Tracker")
            .setContentText("It's time to drink water!!!")
            .setSmallIcon(R.drawable.glass)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
        notificationManager.notify(1, builder.build())
    }
}
