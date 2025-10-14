package com.example.covid19app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.covid19app.R
import android.view.View

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_home)

        // Set up button listeners to navigate to VnDashboardActivity
        findViewById<View>(R.id.btnStats).setOnClickListener {
            navigateToDashboardActivity(0) // StatsFragment is at index 0
        }

        findViewById<View>(R.id.btnSymptomChecker).setOnClickListener {
            navigateToDashboardActivity(1) // SymptomCheckerActivity is at index 1
        }

        findViewById<View>(R.id.btnTrends).setOnClickListener {
            navigateToDashboardActivity(2) // TrendsFragment is at index 2
        }

        findViewById<View>(R.id.btnVaccineCoverage).setOnClickListener {
            navigateToDashboardActivity(3) // VaccineCoverageFragment is at index 3
        }

        findViewById<View>(R.id.btnWorldVaccine).setOnClickListener {
            navigateToDashboardActivity(4) // WorldVaccineFragment is at index 4
        }

        findViewById<View>(R.id.btnSearch).setOnClickListener {
            navigateToDashboardActivity(6) // CountrySearchFragment is at index 6
        }

    }

    // Start the VnDashboardActivity with the selected fragment index
    private fun navigateToDashboardActivity(fragmentIndex: Int) {
        val intent = Intent(this, CovidActivity::class.java)
        intent.putExtra("FRAGMENT_INDEX", fragmentIndex) // Pass the fragment index
        startActivity(intent)
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
