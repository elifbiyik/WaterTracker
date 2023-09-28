package com.eb.watertracker

import android.app.AlarmManager
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
        //    val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val intent =
            Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        /*      val calendar = Calendar.getInstance().apply {
                  set(Calendar.HOUR_OF_DAY, hours)        // 14
                  set(Calendar.MINUTE, min)               // 0
                  set(Calendar.SECOND, 0)

                  // Eğer belirlenen saat geçmişse, ertesi gün için ayarla
                  if (timeInMillis == System.currentTimeMillis()) {
                      add(Calendar.DAY_OF_YEAR, 1)
                  }
              }
              *//*        alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    intent
                )*//*
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            intent
        )
*/


        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        currentTime[Calendar.HOUR_OF_DAY] = currentHour
        currentTime[Calendar.MINUTE] = currentMinute

        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val alarmBeginTime = Calendar.getInstance()
        alarmBeginTime[Calendar.HOUR_OF_DAY] = hours
        alarmBeginTime[Calendar.MINUTE] = min


        val currentTimeMillis = currentTime.timeInMillis

        if (currentTimeMillis == alarmBeginTime.timeInMillis) {
            currentTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmBeginTime.timeInMillis,
            intent
        )







        Log.d("xxxxSystem.currentTimeMillis()", System.currentTimeMillis().toString())
        //     Log.d("xxxxSystem.currentTimeMillis()", calendar.timeInMillis.toString())
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

            var x = System.currentTimeMillis()
            // Eğer belirlenen saat geçmişse, ertesi gün için ayarla
            if (timeInMillis == x) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
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

    fun x(
        context: Context,
        reminderTime: String
    ) {
        val alarmStartTime = Calendar.getInstance()
        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        alarmStartTime[Calendar.HOUR_OF_DAY] = hours
        alarmStartTime[Calendar.MINUTE] = min

        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val currentTimeMillis = alarmStartTime.timeInMillis

        if (currentTimeMillis == alarmStartTime.timeInMillis) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                currentTimeMillis,
                pendingIntent
            )
        }
    }

    fun y(
        context: Context,
        reminderTime: String
    ) {

        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var alarmTime = Calendar.getInstance()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // içinde bulunulan saati alıyor.
        val currentMin = Calendar.getInstance().get(Calendar.MINUTE) // içinde bulunulan saati alıyor.

        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        
        if (currentHour == hours && min == currentMin) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime.timeInMillis,
                pendingIntent
            )

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
        //   var difMinute = (difference / (60 * 1000))
        var lastGoalGlass = lastGoal.toInt() / 200
        //  var alarmTimeRange = difMinute / lastGoalGlass   // Kaç dakika sonra diğer bardak içilmeli

        var alarmTimeRange = difference / lastGoalGlass

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
        val triggerTime = currentTimeMillis + alarmTimeRange//(alarmTimeRange * 60 * 1000)

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
                "The next alarm is in ${alarmTimeRange / (60 * 1000)} minutes. ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
