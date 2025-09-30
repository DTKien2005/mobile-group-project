package com.example.covid19app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class TrendsFragment : Fragment() {

    private lateinit var lineChart: LineChart
    private val TAG = "TrendsFragment"

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        val root = inflater.inflate(R.layout.activity_trends, container, false)
        lineChart = root.findViewById(R.id.lineChart)

        fetchCovidData()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    private fun fetchCovidData() {
        Retrofit.Builder()
            .baseUrl("https://disease.sh/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CovidApiService::class.java)
            .getVietnamTrends()
            .enqueue(object : Callback<HistoricalResponse> {
                override fun onResponse(
                    call: Call<HistoricalResponse>,
                    response: Response<HistoricalResponse>
                ) {
                    response.body()?.timeline?.cases?.let { showChart(it) }
                }

                override fun onFailure(call: Call<HistoricalResponse>, t: Throwable) {
                    Log.e(TAG, "API Error", t)
                }
            })
    }

    private fun showChart(cases: Map<String, Int>) {
        // Offload chart data processing to a background thread
        Thread {
            val inputFormat = SimpleDateFormat("M/d/yy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

            // Sort data in the background
            val sorted = cases.entries.sortedBy { inputFormat.parse(it.key) }

            val labels = sorted.map { entry ->
                runCatching { outputFormat.format(inputFormat.parse(entry.key)!!) }
                    .getOrElse { entry.key }
            }

            val entries = sorted.mapIndexed { index, entry ->
                Entry(index.toFloat(), entry.value.toFloat())
            }

            // Check if the fragment is still attached before updating the UI
            requireActivity().runOnUiThread {
                if (isAdded) {
                    lineChart.apply {
                        data = LineData(LineDataSet(entries, "Ca nhiễm (30 ngày)").apply {
                            color = Color.BLUE
                            lineWidth = 2f
                            setDrawCircles(false)
                            setDrawValues(false)
                        })
                        xAxis.apply {
                            valueFormatter = IndexAxisValueFormatter(labels)
                            position = XAxis.XAxisPosition.BOTTOM
                            granularity = 5f
                            setLabelCount(6, true)
                        }
                        axisLeft.apply {
                            axisMinimum = 0f
                            granularity = 1000f
                            setLabelCount(6, true)
                        }
                        axisRight.isEnabled = false
                        description.isEnabled = false
                        legend.isEnabled = false

                        invalidate()
                    }
                }
            }
        }.start() // Start the background thread
    }
}