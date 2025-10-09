package com.example.covid19app.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.covid19app.R
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.CovidStatsData
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsFragment : Fragment() {

    private val gson by lazy { Gson() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_stats, container, false)

        val tvUpdated        = root.findViewById<TextView>(R.id.tvUpdated)
        val tvCountry        = root.findViewById<TextView>(R.id.tvCountry)
        val tvCases          = root.findViewById<TextView>(R.id.tvCases)
        val tvTodayCases     = root.findViewById<TextView>(R.id.tvTodayCases)
        val tvDeaths         = root.findViewById<TextView>(R.id.tvDeaths)
        val tvTodayDeaths    = root.findViewById<TextView>(R.id.tvTodayDeaths)
        val tvRecovered      = root.findViewById<TextView>(R.id.tvRecovered)
        val tvTodayRecovered = root.findViewById<TextView>(R.id.tvTodayRecovered)
        val tvActive         = root.findViewById<TextView>(R.id.tvActive)
        val tvCritical       = root.findViewById<TextView>(R.id.tvCritical)
        val tvTests          = root.findViewById<TextView>(R.id.tvTests)
        val tvPopulation     = root.findViewById<TextView>(R.id.tvPopulation)

        // Network first
        RetrofitInstance.api.getVietnamStats()
            .enqueue(object : Callback<CovidStatsData> {
                override fun onResponse(call: Call<CovidStatsData>, response: Response<CovidStatsData>) {
                    val stats = response.body()
                    if (!isAdded || stats == null) {
                        loadFromCache(tvCountry) // if server returns empty, try cache
                        return
                    }

                    // cache the latest
                    lifecycleScope.launch(Dispatchers.IO) {
                        FileCache.writeText(requireContext(), "vn_stats.json", gson.toJson(stats))
                    }
                    renderStats(
                        tvUpdated, tvCountry, tvCases, tvTodayCases, tvDeaths, tvTodayDeaths,
                        tvRecovered, tvTodayRecovered, tvActive, tvCritical, tvTests, tvPopulation, stats
                    )
                }

                override fun onFailure(call: Call<CovidStatsData>, t: Throwable) {
                    Log.e("StatsFragment", "API error", t)
                    loadFromCache(tvCountry)
                }
            })

        return root
    }

    private fun loadFromCache(tvCountry: TextView) {
        val cached = FileCache.readText(requireContext(), "vn_stats.json")
        if (cached == null) {
            tvCountry.text = "Offline & no cached stats"
            Toast.makeText(requireContext(), "Offline & no cached stats", Toast.LENGTH_SHORT).show()
            return
        }
        val stats = gson.fromJson(cached, CovidStatsData::class.java)
        // Re-find all views to be safe (we're still in onCreateView scope)
        view?.let { root ->
            renderStats(
                root.findViewById(R.id.tvUpdated),
                root.findViewById(R.id.tvCountry),
                root.findViewById(R.id.tvCases),
                root.findViewById(R.id.tvTodayCases),
                root.findViewById(R.id.tvDeaths),
                root.findViewById(R.id.tvTodayDeaths),
                root.findViewById(R.id.tvRecovered),
                root.findViewById(R.id.tvTodayRecovered),
                root.findViewById(R.id.tvActive),
                root.findViewById(R.id.tvCritical),
                root.findViewById(R.id.tvTests),
                root.findViewById(R.id.tvPopulation),
                stats
            )
        }
    }

    private fun renderStats(
        tvUpdated: TextView, tvCountry: TextView, tvCases: TextView, tvTodayCases: TextView,
        tvDeaths: TextView, tvTodayDeaths: TextView, tvRecovered: TextView, tvTodayRecovered: TextView,
        tvActive: TextView, tvCritical: TextView, tvTests: TextView, tvPopulation: TextView,
        stats: CovidStatsData
    ) {
        tvUpdated.text        = "Updated: ${stats.updated}"
        tvCountry.text        = "Country: ${stats.country}"
        tvCases.text          = "Cases: ${stats.cases}"
        tvTodayCases.text     = "Today Cases: ${stats.todayCases}"
        tvDeaths.text         = "Deaths: ${stats.deaths}"
        tvTodayDeaths.text    = "Today Deaths: ${stats.todayDeaths}"
        tvRecovered.text      = "Recovered: ${stats.recovered}"
        tvTodayRecovered.text = "Today Recovered: ${stats.todayRecovered}"
        tvActive.text         = "Active: ${stats.active}"
        tvCritical.text       = "Critical: ${stats.critical}"
        tvTests.text          = "Tests: ${stats.tests}"
        tvPopulation.text     = "Population: ${stats.population}"
    }
}
