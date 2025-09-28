package com.example.covid19app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.covid19app.features.vndashboard.ui.view.VnDashboardActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        // Attach layout XML
        setContentView(R.layout.main_activity)

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
}
