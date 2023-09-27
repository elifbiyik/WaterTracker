package com.eb.watertracker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
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
        Log.d("Home", "share")

        if (name != "") {
            var editor = sharedPreferences.edit()
            editor.putString("name", name)
            editor.putString("lastGoal", lastGoal)
            editor.putString("count", count.toString())
            editor.apply()
        }
        userMutableLiveData.value = sharedPreferences
        Log.d("xxxxxxxxx", userMutableLiveData.value.toString())
    }

    fun startReminder(
        context: Context,
        reminderTime: String,
        reminderId: Int = 1
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val intent =
            Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)        // 14
            set(Calendar.MINUTE, min)               // 0
            set(Calendar.SECOND, 0)

            // Eğer belirlenen saat geçmişse, ertesi gün için ayarla
            if (timeInMillis == System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            intent
        )

        Log.d("xxxxSystem.currentTimeMillis()", System.currentTimeMillis().toString())
        Log.d("xxxxSystem.currentTimeMillis()", calendar.timeInMillis.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun lastReminder(
        context: Context,
        reminderTime: String,
        reminderId: Int = 1
    ) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val intent =
            Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)

            var x = System.currentTimeMillis()
            // Eğer belirlenen saat geçmişse, ertesi gün için ayarla
            if (timeInMillis == x) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            intent
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun newDay(
        context: Context,
    ) {

        var sharedPreferences = context.getSharedPreferences(
            "userData", AppCompatActivity.MODE_PRIVATE
        )
        var editor = sharedPreferences.edit()

        var alarmFinishTime = Calendar.getInstance()
        val currentHour =
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // içinde bulunulan saati alıyor.
        if (currentHour == 23) {
            alarmFinishTime.add(Calendar.DAY_OF_YEAR, 1)
            editor.remove("lastGoal")
            editor.remove("count")
            editor.apply()
        }
    }

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

        val (hoursFinish, minFinish) = userFinish.split(":").map { it.toInt() }
        val alarmFinishTime = Calendar.getInstance()
        alarmFinishTime[Calendar.HOUR_OF_DAY] = hoursFinish
        alarmFinishTime[Calendar.MINUTE] = minFinish

        var difference = alarmFinishTime.timeInMillis - alarmStartTime.timeInMillis
        var difMinute = (difference / (60 * 1000))
        var lastGoalGlass = lastGoal.toInt() / 200
        var alarmTimeRange = difMinute / lastGoalGlass   // Kaç dakika sonra diğer bardak içilmeli

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

        val currentTimeMillis = alarmStartTime.timeInMillis  //System.currentTimeMillis()
        val triggerTime = currentTimeMillis + (alarmTimeRange * 60 * 1000)

        Log.d("xxxxxxxxxxxbutonCurrentMilliis", currentTimeMillis.toString())
        Log.d("xxxxxxxxxxxbutonCurrentMilliis", (alarmTimeRange * 60 * 1000).toString())


        // setAndAllowWhileIdle -> idle modu, telefonun ekranı kapalı ve kullanıcının cihazı kullanmadığı moddur.
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
                "The next alarm is in ${alarmTimeRange} minutes. ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
