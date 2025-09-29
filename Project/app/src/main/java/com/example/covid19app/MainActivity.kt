package com.example.covid19app

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import com.example.covid19app.features.vndashboard.ui.view.VnDashboardActivity
import com.example.covid19app.SymptomCheckerFragment
import com.example.covid19app.TrendsFragment


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            loadFragment(TrendsFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_trends -> {
                    loadFragment(TrendsFragment())
                    true
                }

                R.id.nav_symptom -> {
                    loadFragment(SymptomCheckerFragment())
                    true
                }

                else -> false
            }
        }
        // Create a button to open the dashboard
        val dashboardButton: Button = findViewById(R.id.covid_dashboard_button)

        // Event when u click the button
        dashboardButton.setOnClickListener {
            val intent = Intent(this, VnDashboardActivity::class.java)
            startActivity(intent)
            Log.d(TAG, "Dashboard button")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
