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
import com.example.covid19app.data.VaccineResponseData
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VaccineCoverageFragment : Fragment() {

    private val gson by lazy { Gson() }
    private var tvVaccine: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_vaccine_coverage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvVaccine = view.findViewById(R.id.tvVaccine)
        loadVietnamVaccineData()
    }

    private fun loadVietnamVaccineData() {
        RetrofitInstance.api.getVietnamVaccineCoverage("30", false)
            .enqueue(object : Callback<VaccineResponseData> {
                override fun onResponse(
                    call: Call<VaccineResponseData>,
                    response: Response<VaccineResponseData>
                ) {
                    if (!isAdded) return
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        lifecycleScope.launch(Dispatchers.IO) {
                            FileCache.writeText(requireContext(), "vn_vaccine.json", gson.toJson(data))
                        }
                        renderVaccine(data)
                    } else {
                        loadFromCache()
                    }
                }

                override fun onFailure(call: Call<VaccineResponseData>, t: Throwable) {
                    if (!isAdded) return
                    loadFromCache()
                }
            })
    }

    private fun loadFromCache() {
        val cached = FileCache.readText(requireContext(), "vn_vaccine.json")
        if (cached != null) {
            val obj = gson.fromJson(cached, VaccineResponseData::class.java)
            renderVaccine(obj)
        } else {
            tvVaccine?.text = "Offline & no cached data"
            Toast.makeText(requireContext(), "Offline & no vaccine cache", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderVaccine(data: VaccineResponseData) {
        // entries -> Set, so convert to list and sort by date
        val inputFormats = listOf(
            java.text.SimpleDateFormat("M/d/yy", java.util.Locale.ENGLISH),
            java.text.SimpleDateFormat("MM/dd/yy", java.util.Locale.ENGLISH)
        )

        fun parse(d: String) = inputFormats.firstNotNullOfOrNull { f ->
            runCatching { f.parse(d) }.getOrNull()
        }?.time ?: Long.MIN_VALUE

        val latest7 = data.timeline
            .entries
            .toList()
            .sortedBy { parse(it.key) }   // chronological
            .takeLast(7)

        val text = buildString {
            append("Vietnam Vaccine Coverage (Latest 7 days):\n")
            latest7.forEach { (date, value) -> append("$date → $value doses\n") }
        }
        tvVaccine?.text = text
    }
}
