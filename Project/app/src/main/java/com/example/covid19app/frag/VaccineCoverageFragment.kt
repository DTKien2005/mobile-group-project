package com.example.covid19app.frag

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.covid19app.R
import com.example.covid19app.api.RetrofitInstance
import com.example.covid19app.data.VaccineResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VaccineCoverageFragment : Fragment(R.layout.fragment_vaccine_coverage) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // Bottom buttons
//        view.findViewById<View>(R.id.btnToWorld).setOnClickListener {
//            findNavController().navigate(R.id.worldFragment)
//        }
//        view.findViewById<View>(R.id.btnToCompare).setOnClickListener {
//            findNavController().navigate(R.id.compareFragment)
//        }

        // --- Load Vietnam data and show ---
        val tv = view.findViewById<TextView>(R.id.tvVaccine) // add this TextView in your XML
        tv.text = "Loading Vietnam vaccine coverage…"

        RetrofitInstance.api.getVaccineCoverage(lastDays = "30", fullData = false) // was getVietnamCoverage
            .enqueue(object : Callback<VaccineResponseData> {
                override fun onResponse(
                    call: Call<VaccineResponseData>,
                    response: Response<VaccineResponseData>
                ) {
                    if (response.isSuccessful) {
                        val timeline = response.body()?.timeline.orEmpty()
                        val text = buildString {
                            append("Vietnam (last 30 days):\n")
                            timeline.entries.toList().takeLast(7).forEach { (date, doses) ->
                                append("$date → $doses doses\n")
                            }
                        }
                        tv.text = text.ifEmpty { "No data." }
                    } else {
                        tv.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<VaccineResponseData>, t: Throwable) {
                    tv.text = "Failed: ${t.message}"
                }
            })
    }
}