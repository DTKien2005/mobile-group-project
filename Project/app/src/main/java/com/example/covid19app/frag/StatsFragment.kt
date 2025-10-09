package com.example.covid19app.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import com.example.covid19app.R
import com.example.covid19app.data.CovidStatsData
import com.example.covid19app.viewmodel.StatsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {

    private lateinit var statsViewModel: StatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_stats, container, false)

        // Bind views
        val tvUpdated = root.findViewById<TextView>(R.id.tvUpdated)
        val tvCountry = root.findViewById<TextView>(R.id.tvCountry)
        val tvCases = root.findViewById<TextView>(R.id.tvCases)
        val tvTodayCases = root.findViewById<TextView>(R.id.tvTodayCases)
        val tvDeaths = root.findViewById<TextView>(R.id.tvDeaths)
        val tvTodayDeaths = root.findViewById<TextView>(R.id.tvTodayDeaths)
        val tvRecovered = root.findViewById<TextView>(R.id.tvRecovered)
        val tvTodayRecovered = root.findViewById<TextView>(R.id.tvTodayRecovered)
        val tvActive = root.findViewById<TextView>(R.id.tvActive)
        val tvCritical = root.findViewById<TextView>(R.id.tvCritical)
        val tvTests = root.findViewById<TextView>(R.id.tvTests)
        val tvPopulation = root.findViewById<TextView>(R.id.tvPopulation)

        // Initialize ViewModel
        statsViewModel = ViewModelProvider(this).get(StatsViewModel::class.java)

        // Observe changes to the stats
        statsViewModel.stats.observe(viewLifecycleOwner, Observer { stats ->
            stats?.let {
                renderStats(
                    tvUpdated, tvCountry, tvCases, tvTodayCases, tvDeaths, tvTodayDeaths,
                    tvRecovered, tvTodayRecovered, tvActive, tvCritical, tvTests, tvPopulation, it
                )
            }
        })

        // Load the stats
        statsViewModel.loadStats()

        return root
    }

    // Render stats on the UI
    private fun renderStats(
        tvUpdated: TextView, tvCountry: TextView, tvCases: TextView, tvTodayCases: TextView,
        tvDeaths: TextView, tvTodayDeaths: TextView, tvRecovered: TextView, tvTodayRecovered: TextView,
        tvActive: TextView, tvCritical: TextView, tvTests: TextView, tvPopulation: TextView,
        stats: CovidStatsData
    ) {
        tvUpdated.text = "Updated: ${stats.updated}"
        tvCountry.text = "Country: ${stats.country}"
        tvCases.text = "Cases: ${stats.cases}"
        tvTodayCases.text = "Today Cases: ${stats.todayCases}"
        tvDeaths.text = "Deaths: ${stats.deaths}"
        tvTodayDeaths.text = "Today Deaths: ${stats.todayDeaths}"
        tvRecovered.text = "Recovered: ${stats.recovered}"
        tvTodayRecovered.text = "Today Recovered: ${stats.todayRecovered}"
        tvActive.text = "Active: ${stats.active}"
        tvCritical.text = "Critical: ${stats.critical}"
        tvTests.text = "Tests: ${stats.tests}"
        tvPopulation.text = "Population: ${stats.population}"
    }
}
