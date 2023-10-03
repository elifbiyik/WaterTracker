package com.eb.watertracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class NewDayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        clearSharedPreference(context)
    }

    private fun clearSharedPreference(context: Context) {
        var sharedPreferences =
            context.getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        editor.remove("lastGoal")
        editor.remove("count")
        editor.apply()
    }
}