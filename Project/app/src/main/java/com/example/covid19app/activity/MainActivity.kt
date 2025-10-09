package com.example.covid19app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.covid19app.R
import com.example.covid19app.offlinedata.DataSeeder
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.vndashboard)

        // Seed offline data on first launch
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Starting data seeding...")
                DataSeeder.ensureSeeded(this@MainActivity)
                Log.d(TAG, "Data seeding complete.")
            } catch (e: Exception) {
                Log.e(TAG, "Data seeding failed: ${e.message}", e)
            }

            // Launch dashboard once seeding completes (first run) or immediately (subsequent runs)
            val intent = Intent(this@MainActivity, VnDashboardActivity::class.java)
            startActivity(intent)
            finish()
            Log.d(TAG, "Dashboard started.")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
}
