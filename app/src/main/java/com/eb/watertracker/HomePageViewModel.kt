package com.eb.watertracker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import java.util.Calendar

class HomePageViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    fun notification(context: Context) {
        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        // AlarmReceiver sınıfına yönlendirilecek bir intent oluşur. (birbirleriyle iletişim kur.)
        // Bildirim zamanı geldiğinde AlarmReceiver tetikleyecek
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Alarm tetiklendiğinde gerçekleşecek işlemler pending intentte
// Intent'in belirli bir zamanda veya belirli bir koşulda çalışmasını sağlayan bir araçtır.


        // Şu anki zaman
        val currentTimeMillis = SystemClock.elapsedRealtime()

        val oneHour = 3 * 1000 //60 * 60 * 1000 // 1 saat
        val triggerTime = currentTimeMillis + oneHour
//Bildirimin ne zaman gösterileceği belirlenen bir zamanı temsil eder.

        // Alarmı ayarla // pendingIntent tetiklenecek içindeki olaylar geçrkleşicek.
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)
    }

    fun startReminder(
        context: Context,
        reminderTime: String,
        reminderId: Int = 1
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

// hours ve min split ile ayırdık. Aşağıya ayrı yrı verdik.
        val alarmStartTime = Calendar.getInstance()
        val now = Calendar.getInstance()
        alarmStartTime[Calendar.HOUR_OF_DAY] = hours
        alarmStartTime[Calendar.MINUTE] = min
        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, alarmStartTime.timeInMillis, intent
        )
    }

    fun lastReminder(
        context: Context,
        reminderTime: String,
        reminderId: Int = 1
    ) {

        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (hours, min) = reminderTime.split(":").map { it.toInt() }

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        var alarmFinishTime = Calendar.getInstance()
        val now = Calendar.getInstance()
        alarmFinishTime[Calendar.HOUR_OF_DAY] = hours - 1
        alarmFinishTime[Calendar.MINUTE] = 50 // min - 10  // 10 dakika önce göndersin.
        if (now.after(alarmFinishTime)) {
            alarmFinishTime.add(Calendar.DATE, 1)
        }
        // Eğer saat geçmişse bir sonraki güne atar

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, alarmFinishTime.timeInMillis, intent
        )
    }

    fun newDay(
        context: Context,
        reminderId: Int = 1,
        goal: String,
        tvGoal: TextView
    ) {
        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        var alarmFinishTime = Calendar.getInstance()
        val now = Calendar.getInstance()
        alarmFinishTime[Calendar.HOUR_OF_DAY] = 24
        alarmFinishTime[Calendar.MINUTE] = 0
        if (now.after(alarmFinishTime)) {
            alarmFinishTime.add(Calendar.DATE, 1)
            tvGoal.text = goal // Bir sonraki gün olduğunda hedef yeniden başlsın ?
        }
        // Eğer saat geçmişse bir sonraki güne atar

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, alarmFinishTime.timeInMillis, intent
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    fun createNotification(context: Context) {

        var notificationManager: NotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel_id = "com.eb.watertracker"
            val message = "xxx"
            val priority = NotificationManager.IMPORTANCE_DEFAULT

            var notificationChannel = NotificationChannel(channel_id, message, priority)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
