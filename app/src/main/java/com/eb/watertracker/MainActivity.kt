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

        Handler(Looper.getMainLooper()).postDelayed({
            loadFragment(HomePageFragment())
        }, 2000)
    }

    override fun onBackPressed() {
        // Uygulamadan çıkış işlemleri
        finish()
    }

    fun loadFragment(fragment: Fragment) {
        if (!isFinishing() && !isDestroyed() ) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.constraint, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}