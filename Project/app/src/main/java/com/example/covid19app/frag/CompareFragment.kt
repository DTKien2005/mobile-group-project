package com.example.covid19app.frag

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.covid19app.R
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.VaccineResponseData
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompareFragment : Fragment(R.layout.fragment_compare) {

    private val gson by lazy { Gson() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tv = view.findViewById<TextView>(R.id.tvCompare)
        tv.text = "Loading comparison…"

        loadComparison(tv)
    }

    private fun loadComparison(tv: TextView) {
        // Vietnam first
        RetrofitInstance.api.getVaccineCoverage("30", false)
            .enqueue(object : Callback<VaccineResponseData> {
                override fun onResponse(call: Call<VaccineResponseData>, response: Response<VaccineResponseData>) {
                    if (response.isSuccessful && response.body() != null) {
                        val vn = response.body()!!
                        lifecycleScope.launch(Dispatchers.IO) {
                            FileCache.writeText(requireContext(), "vn_vaccine.json", gson.toJson(vn))
                        }
                        loadWorld(tv, vn.timeline)
                    } else {
                        // Fallback to cache if VN fails
                        val cached = FileCache.readText(requireContext(), "vn_vaccine.json")
                        val vnData = cached?.let { gson.fromJson(it, VaccineResponseData::class.java) }
                        if (vnData != null) loadWorld(tv, vnData.timeline)
                        else tv.text = "Vietnam data unavailable (offline & no cache)"
                    }
                }

                override fun onFailure(call: Call<VaccineResponseData>, t: Throwable) {
                    val cached = FileCache.readText(requireContext(), "vn_vaccine.json")
                    val vnData = cached?.let { gson.fromJson(it, VaccineResponseData::class.java) }
                    if (vnData != null) loadWorld(tv, vnData.timeline)
                    else tv.text = "Vietnam failed: ${t.message}"
                }
            })
    }

    private fun loadWorld(tv: TextView, vnTimeline: Map<String, Long>) {
        RetrofitInstance.api.getWorldVaccineCoverage("30", false)
            .enqueue(object : Callback<Map<String, Long>> {
                override fun onResponse(call: Call<Map<String, Long>>, response: Response<Map<String, Long>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val world = response.body()!!
                        lifecycleScope.launch(Dispatchers.IO) {
                            FileCache.writeText(requireContext(), "world_vaccine.json", gson.toJson(world))
                        }
                        displayComparison(tv, vnTimeline, world)
                    } else {
                        val cached = FileCache.readText(requireContext(), "world_vaccine.json")
                        val world = cached?.let { gson.fromJson(it, Map::class.java) as Map<String, Long> }
                        if (world != null) displayComparison(tv, vnTimeline, world)
                        else tv.text = "Vietnam loaded, world data unavailable (offline & no cache)"
                    }
                }

                override fun onFailure(call: Call<Map<String, Long>>, t: Throwable) {
                    val cached = FileCache.readText(requireContext(), "world_vaccine.json")
                    val world = cached?.let { gson.fromJson(it, Map::class.java) as Map<String, Long> }
                    if (world != null) displayComparison(tv, vnTimeline, world)
                    else tv.text = "Vietnam loaded, world failed: ${t.message}"
                }
            })
    }

    private fun displayComparison(tv: TextView, vn: Map<String, Long>, world: Map<String, Long>) {
        val vnText = buildString {
            append("Vietnam (7 latest):\n")
            vn.entries.toList().takeLast(7).forEach { (d, v) -> append("$d → $v\n") }
        }
        val worldText = buildString {
            append("\nWorld (7 latest):\n")
            world.entries.toList().takeLast(7).forEach { (d, v) -> append("$d → $v\n") }
        }
        tv.text = vnText + worldText
    }
}
