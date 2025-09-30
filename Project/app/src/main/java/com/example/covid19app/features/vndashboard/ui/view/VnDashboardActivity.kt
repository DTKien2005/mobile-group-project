package com.example.covid19app.features.vndashboard.ui.view

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.covid19app.R
import com.example.covid19app.features.vndashboard.data.api.CovidApiService
import com.example.covid19app.features.vndashboard.data.api.CovidApiService.CovidCallback
import com.example.covid19app.features.vndashboard.data.model.CovidStats

class VnDashboardActivity : AppCompatActivity() {
    private var tvUpdated: TextView? = null
    private var tvCountry: TextView? = null
    private var tvCases: TextView? = null
    private var tvTodayCases: TextView? = null
    private var tvDeaths: TextView? = null
    private var tvTodayDeaths: TextView? = null
    private var tvRecovered: TextView? = null
    private var tvTodayRecovered: TextView? = null
    private var tvActive: TextView? = null
    private var tvCritical: TextView? = null
    private var tvTests: TextView? = null
    private var tvPopulation: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vndashboard)

        // Gán view từ layout
        tvUpdated = findViewById<TextView>(R.id.tvUpdated)
        tvCountry = findViewById<TextView>(R.id.tvCountry)
        tvCases = findViewById<TextView>(R.id.tvCases)
        tvTodayCases = findViewById<TextView>(R.id.tvTodayCases)
        tvDeaths = findViewById<TextView>(R.id.tvDeaths)
        tvTodayDeaths = findViewById<TextView>(R.id.tvTodayDeaths)
        tvRecovered = findViewById<TextView>(R.id.tvRecovered)
        tvTodayRecovered = findViewById<TextView>(R.id.tvTodayRecovered)
        tvActive = findViewById<TextView>(R.id.tvActive)
        tvCritical = findViewById<TextView>(R.id.tvCritical)
        tvTests = findViewById<TextView>(R.id.tvTests)
        tvPopulation = findViewById<TextView>(R.id.tvPopulation)

        // Gọi API qua Service
        CovidApiService.fetchCovidStats(this, object : CovidCallback {
            override fun onSuccess(stats: CovidStats?) {
                tvUpdated!!.text = "Updated: " + stats?.updated
                tvCountry!!.text = "Country: " + stats?.country
                tvCases!!.text = "Cases: " + stats?.cases
                tvTodayCases!!.text = "Today Cases: " + stats?.todayCases
                tvDeaths!!.text = "Deaths: " + stats?.deaths
                tvTodayDeaths!!.text = "Today Deaths: " + stats?.todayDeaths
                tvRecovered!!.text = "Recovered: " + stats?.recovered
                tvTodayRecovered!!.text = "Today Recovered: " + stats?.todayRecovered
                tvActive!!.text = "Active: " + stats?.active
                tvCritical!!.text = "Critical: " + stats?.critical
                tvTests!!.text = "Tests: " + stats?.tests
                tvPopulation!!.text = "Population: " + stats?.population
            }


            override fun onError(errorMessage: String?) {
                Log.e("VnDashboardActivity", "API error: " + errorMessage)
                tvCountry!!.setText("Error!!!")
            }
        })
    }
}
