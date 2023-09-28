package com.eb.watertracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
// Telefon dark mode ise uygulama dark moda geçer

// Splash ekran
        Handler(Looper.getMainLooper()).postDelayed({
            loadFragment(HomePageFragment())
        }, 2000)
    }

    fun loadFragment(fragment: Fragment) {
        if (!isFinishing() && !isDestroyed() ) { // FragmentManager has been destroyed hatası verd. için yaz.
            supportFragmentManager.beginTransaction()
                .replace(R.id.constraint, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}