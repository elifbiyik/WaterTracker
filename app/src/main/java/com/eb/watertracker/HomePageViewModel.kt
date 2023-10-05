package com.eb.watertracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class HomePageViewModel : ViewModel() {

    var userMutableLiveData = MutableLiveData<SharedPreferences>()
    var loginMutableLiveData = MutableLiveData<SharedPreferences>()

    fun sharedPreferenceLogin(
        context: Context,
        begin: String,
        finish: String,
        goal: String,
        name: String
    ) {
        var sharedPreferences = context.getSharedPreferences(
            "user", AppCompatActivity.MODE_PRIVATE
        )
        if (name != "") {
            var editor = sharedPreferences.edit()
            editor.putString("name", name)
            editor.putString("begin", begin)
            editor.putString("finish", finish)
            editor.putString("goal", goal)
            editor.apply()
        }
        loginMutableLiveData.value = sharedPreferences
    }

    fun sharedPreferenceForUser(context: Context, lastGoal: String, name: String, count: Int) {
        var sharedPreferences = context.getSharedPreferences(
            "userData", AppCompatActivity.MODE_PRIVATE
        )

        if (name != "") {
            var editor = sharedPreferences.edit()
            editor.putString("name", name)
            editor.putString("lastGoal", lastGoal)
            editor.putString("count", count.toString())
            editor.apply()
        }
        userMutableLiveData.value = sharedPreferences
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleNotification(
        context: Context,
        reminderTime: String,
        notificationText: String
    ) {
        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val notificationTime = calculateNotificationTime(hours, min)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("notificationText", notificationText)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            hours * 60 + min, // Farklı requestlerde gönderilmeli o yüzden dakika olark gönder.
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notificationTime,
            pendingIntent
        )
    }

    fun newDay(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NewDayReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationTime = calculateNotificationTime(23, 59)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notificationTime,
            pendingIntent
        )
    }

    fun calculateNotificationTime(hours: Int, min: Int): Long {
        val now = Calendar.getInstance()
        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
        }
        // Eğer belirlenen saat şu andan önceyse, bir sonraki gün aynı saatte bildirim gönder
        if (notificationTime.before(now)) {
            notificationTime.add(Calendar.DATE, 1)
        }
        return notificationTime.timeInMillis
    }


    fun calculateFinish (hours: Int, min: Int): Long {
        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
        }
        return notificationTime.timeInMillis
    }

    fun calculateNow (): Long {
        val now = Calendar.getInstance()
        return now.timeInMillis
    }

    fun timeNotification(
        context: Context,
        userFinish: String,
        lastGoal: String
    ) {
        val (hoursFinish, minFinish) = userFinish.split(":").map { it.toInt() }

        var alarmStartTime = calculateNow()
        var alarmFinishTime = calculateFinish(hoursFinish, minFinish)

        var difference = alarmFinishTime - alarmStartTime
        var lastGoalGlass = lastGoal.toInt() / 200

        var alarmTimeRange = (alarmFinishTime - System.currentTimeMillis()) / lastGoalGlass
        val triggerTime = System.currentTimeMillis() + alarmTimeRange

        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )

        if (alarmFinishTime < System.currentTimeMillis()) {
            Toast.makeText(
                context,
                "You can do it ! ",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "The next alarm is in ${alarmTimeRange / (60 * 1000)} minutes. ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

/*
    fun timeNotification(
        context: Context,
        userFinish: String,
        lastGoal: String
    ) {

        val alarmStartTime = Calendar.getInstance()
        val currentHour = alarmStartTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = alarmStartTime.get(Calendar.MINUTE)
        alarmStartTime[Calendar.HOUR_OF_DAY] = currentHour
        alarmStartTime[Calendar.MINUTE] = currentMinute
        alarmStartTime[Calendar.SECOND] = 0
        alarmStartTime[Calendar.MILLISECOND] = 0

        val (hoursFinish, minFinish) = userFinish.split(":").map { it.toInt() }
        val alarmFinishTime = Calendar.getInstance()
        alarmFinishTime[Calendar.HOUR_OF_DAY] = hoursFinish
        alarmFinishTime[Calendar.MINUTE] = minFinish
        alarmFinishTime[Calendar.SECOND] = 0
        alarmFinishTime[Calendar.MILLISECOND] = 0

        var difference = alarmFinishTime.timeInMillis - alarmStartTime.timeInMillis
        var lastGoalGlass = lastGoal.toInt() / 200

        var alarmTimeRange = difference / lastGoalGlass
        val currentTimeMillis = alarmStartTime.timeInMillis
        val triggerTime = currentTimeMillis + alarmTimeRange

        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )

        if (alarmFinishTime < alarmStartTime) {
            Toast.makeText(
                context,
                "You can do it ! ",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "The next alarm is in ${alarmTimeRange / (60 * 1000)} minutes. ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/
}

