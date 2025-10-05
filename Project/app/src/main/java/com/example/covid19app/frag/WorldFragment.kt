package com.example.covid19app.frag

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.covid19app.R
import com.example.covid19app.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorldFragment : Fragment(R.layout.fragment_world) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.findViewById<View>(R.id.btnToVn).setOnClickListener {
//            findNavController().navigate(R.id.vaccineCoverageFragment)
//        }
//        view.findViewById<View>(R.id.btnToCompare).setOnClickListener {
//            findNavController().navigate(R.id.compareFragment)
//        }

        val tv = view.findViewById<TextView>(R.id.tvWorld) // add this TextView in your XML
        tv.text = "Loading world vaccine coverage…"

        RetrofitInstance.api.getWorldData(lastDays = "30", fullData = false) // was getWorldCoverage
            .enqueue(object : Callback<Map<String, Long>> {
                override fun onResponse(
                    call: Call<Map<String, Long>>,
                    response: Response<Map<String, Long>>
                ) {
                    if (response.isSuccessful) {
                        val timeline = response.body().orEmpty()
                        val text = buildString {
                            append("World (last 30 days):\n")
                            timeline.entries.toList().takeLast(7).forEach { (date, doses) ->
                                append("$date → $doses doses\n")
                            }
                        }
                        tv.text = text.ifEmpty { "No data." }
                    } else {
                        tv.text = "Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Map<String, Long>>, t: Throwable) {
                    tv.text = "Failed: ${t.message}"
                }
            })
    }
}