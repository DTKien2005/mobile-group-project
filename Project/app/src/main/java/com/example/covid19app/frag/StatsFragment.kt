package com.example.covid19app.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.covid19app.R
import com.example.covid19app.api.CovidApiService
import com.example.covid19app.data.CovidStatsData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsFragment : Fragment() {

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

        // Call the merged Retrofit API
        val api = CovidApiService.create()
        api.getVietnamStats().enqueue(object : Callback<CovidStatsData> {
            override fun onResponse(call: Call<CovidStatsData>, response: Response<CovidStatsData>) {
                val stats = response.body()
                if (!isAdded || stats == null) return

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

            override fun onFailure(call: Call<CovidStatsData>, t: Throwable) {
                Log.e("StatsFragment", "API error", t)
                if (isAdded) tvCountry.text = "Error!!!"
            }
        })

        return root
    }
}
