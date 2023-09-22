package com.eb.watertracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppTheme_DayNight)
        } else {
            setTheme(R.style.AppTheme)
        }

      //  delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



// Splash ekran
        Handler(Looper.getMainLooper()).postDelayed({
            loadFragment(HomePageFragment())
        }, 2000)
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.constraint, fragment)
            .addToBackStack(null)
            .commit()
    }
}