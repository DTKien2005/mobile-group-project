package com.example.covid19app.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.covid19app.R
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorldVaccineFragment : Fragment() {

    private val gson by lazy { Gson() }
    private var tvWorld: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_world, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvWorld = view.findViewById(R.id.tvWorld)
        loadWorldVaccineData()
    }

    private fun loadWorldVaccineData() {
        RetrofitInstance.api.getWorldVaccineCoverage("30", false)
            .enqueue(object : Callback<Map<String, Long>> {
                override fun onResponse(
                    call: Call<Map<String, Long>>,
                    response: Response<Map<String, Long>>
                ) {
                    if (!isAdded) return
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        lifecycleScope.launch(Dispatchers.IO) {
                            FileCache.writeText(requireContext(), "world_vaccine.json", gson.toJson(data))
                        }
                        renderWorld(data)
                    } else {
                        loadFromCache()
                    }
                }

                override fun onFailure(call: Call<Map<String, Long>>, t: Throwable) {
                    if (!isAdded) return
                    loadFromCache()
                }
            })
    }

    private fun loadFromCache() {
        val cached = FileCache.readText(requireContext(), "world_vaccine.json")
        if (cached != null) {
            val type = object : com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.type
            val mapDouble: Map<String, Double> = gson.fromJson(cached, type)
            val mapLong: Map<String, Long> = mapDouble.mapValues { it.value.toLong() }
            renderWorld(mapLong)
        } else {
            tvWorld?.text = "Offline & no cached data"
            Toast.makeText(requireContext(), "Offline & no world cache", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderWorld(data: Map<String, Long>) {
        // entries -> Set, so convert to list and sort by date
        val inputFormats = listOf(
            java.text.SimpleDateFormat("M/d/yy", java.util.Locale.ENGLISH),
            java.text.SimpleDateFormat("MM/dd/yy", java.util.Locale.ENGLISH)
        )

        fun parse(d: String) = inputFormats.firstNotNullOfOrNull { f ->
            runCatching { f.parse(d) }.getOrNull()
        }?.time ?: Long.MIN_VALUE

        val latest7 = data
            .entries
            .toList()
            .sortedBy { parse(it.key) }   // chronological
            .takeLast(7)

        val text = buildString {
            append("World Vaccine Coverage (Latest 7 days):\n")
            latest7.forEach { (date, value) -> append("$date → $value doses\n") }
        }
        tvWorld?.text = text
    }
}
