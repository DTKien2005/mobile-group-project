package com.example.covid19app.features.vndashboard.ui.view

// imports you need:
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.covid19app.R
import com.example.covid19app.SymptomCheckerFragment
import com.example.covid19app.TrendsFragment

class VnDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vndashboard)

        // Default screen = Home (stats)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatsFragment())
                .commit()
        }

        // Home
        findViewById<Button>(R.id.btnHome)?.setOnClickListener {
            supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StatsFragment())
                .commit()
        }

        // Symptom Checker
        findViewById<Button>(R.id.btnSymptomChecker).setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SymptomCheckerFragment())
                .addToBackStack("symptom")
                .commit()
        }

        // Trends (Vietnam COVID-19 daily chart + date picker)
        findViewById<Button>(R.id.btnTrend).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, TrendsFragment())
                addToBackStack("trends")
            }
        }
    }
}
