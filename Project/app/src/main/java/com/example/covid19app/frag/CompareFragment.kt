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

class CompareFragment : Fragment(R.layout.fragment_compare) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.findViewById<View>(R.id.btnToVn).setOnClickListener {
//            findNavController().navigate(R.id.vaccineCoverageFragment)
//        }
//        view.findViewById<View>(R.id.btnToWorld).setOnClickListener {
//            findNavController().navigate(R.id.worldFragment)
//        }

        val tv = view.findViewById<TextView>(R.id.tvCompare) // add this TextView in your XML
        tv.text = "Loading comparison…"

        // Vietnam
        RetrofitInstance.api.getVaccineCoverage("30", false)   // was getVietnamCoverage
            .enqueue(object : Callback<VaccineResponseData> {
                override fun onResponse(
                    call: Call<VaccineResponseData>, response: Response<VaccineResponseData>
                ) {
                    val vn = response.body()?.timeline.orEmpty()

                    // World vaccine coverage (after VN)
                    RetrofitInstance.api.getWorldVaccineCoverage("30", false)
                        .enqueue(object : Callback<Map<String, Long>> {
                            override fun onResponse(
                                call: Call<Map<String, Long>>,
                                resp: Response<Map<String, Long>>
                            ) {
                                val world = resp.body().orEmpty()
                                val vnText = buildString {
                                    append("Vietnam (7 latest):\n")
                                    vn.entries.toList().takeLast(7)
                                        .forEach { (d, v) -> append("$d → $v\n") }
                                }
                                val worldText = buildString {
                                    append("\nWorld (7 latest):\n")
                                    world.entries.toList().takeLast(7)
                                        .forEach { (d, v) -> append("$d → $v\n") }
                                }
                                tv.text = vnText + worldText
                            }
                            override fun onFailure(call: Call<Map<String, Long>>, t: Throwable) {
                                tv.text = "Vietnam loaded, world failed: ${t.message}"
                            }
                        })
                }
                override fun onFailure(call: Call<VaccineResponseData>, t: Throwable) {
                    tv.text = "Vietnam failed: ${t.message}"
                }
            })
    }
}