package com.example.covid19app.frag

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.covid19app.R
import com.example.covid19app.api.HistoricalResponse
import com.example.covid19app.api.RetrofitInstance
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.example.covid19app.offlinedata.FileCache
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class TrendsFragment : Fragment() {
    private val TAG = "TrendsFragment"

    private var lineChart: LineChart? = null
    private var tvResult: TextView? = null
    private val gson by lazy { Gson() }

    private var currentCall: Call<HistoricalResponse>? = null

    private var allCases: Map<String, Int>? = null
    private var sortedDateList: List<Pair<Long, Int>> = emptyList()

    private var dailyData: List<Pair<Long, Int>> = emptyList()
    private var labels: List<String> = emptyList()

    // Accept both 1/2/20 and 01/02/20
    private val inputFormats = listOf(
        SimpleDateFormat("M/d/yy", Locale.ENGLISH),
        SimpleDateFormat("MM/dd/yy", Locale.ENGLISH)
    )
    private val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_trends, container, false)
        lineChart = root.findViewById(R.id.lineChart)
        tvResult = root.findViewById(R.id.tvResult)
        root.findViewById<Button>(R.id.btnPickDate).setOnClickListener { showDatePicker() }
        fetchCovidData()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentCall?.cancel()
        currentCall = null
        lineChart = null
        tvResult = null
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val dlg = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                var selMillis = selCal.timeInMillis

                if (sortedDateList.isEmpty()) {
                    tvResult?.text = "No data available to view."
                    return@DatePickerDialog
                }

                val minMillis = sortedDateList.first().first
                val maxMillis = sortedDateList.last().first
                selMillis = selMillis.coerceIn(minMillis, maxMillis)

                var nearest = sortedDateList.lastOrNull { it.first <= selMillis }
                if (nearest == null) nearest = sortedDateList.firstOrNull { it.first >= selMillis }
                if (nearest == null) {
                    tvResult?.text = "No data found for this date."
                    return@DatePickerDialog
                }

                val prev = sortedDateList.lastOrNull { it.first < nearest.first }
                val prevVal = prev?.second ?: 0
                val diff = (nearest.second - prevVal).coerceAtLeast(0)

                val nearestDate = outputFormat.format(Date(nearest.first))
                tvResult?.text = "Closest result ($nearestDate): $diff new cases"
                zoomToDateMillis(nearest.first, halfWindowDays = 7)
            },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        )

        if (sortedDateList.isNotEmpty()) {
            dlg.datePicker.minDate = sortedDateList.first().first
            dlg.datePicker.maxDate = sortedDateList.last().first
        }
        dlg.show()
    }

    private fun fetchCovidData() {
        val call = RetrofitInstance.api.getVietnamTrends()
        currentCall = call

        call.enqueue(object : Callback<HistoricalResponse> {
            override fun onResponse(call: Call<HistoricalResponse>, response: Response<HistoricalResponse>) {
                if (!isAdded || lineChart == null || tvResult == null) return

                if (!response.isSuccessful || response.body()?.timeline?.cases.isNullOrEmpty()) {
                    // fallback to cache if server error/empty
                    loadFromCache()
                    return
                }

                val body = response.body()!!
                // save cache
                lifecycleScope.launch(Dispatchers.IO) {
                    FileCache.writeText(requireContext(), "vn_trends.json", gson.toJson(body))
                }

                allCases = body.timeline?.cases
                buildSortedDateList()
                showChart()
            }

            override fun onFailure(call: Call<HistoricalResponse>, t: Throwable) {
                if (!isAdded || tvResult == null) return
                Log.e(TAG, "API Error", t)
                loadFromCache()
            }
        })
    }

    private fun loadFromCache() {
        val cached = FileCache.readText(requireContext(), "vn_trends.json")
        if (cached == null) {
            tvResult?.text = "Offline & no cached trends"
            Toast.makeText(requireContext(), "Offline & no cached trends", Toast.LENGTH_SHORT).show()
            return
        }
        val body = gson.fromJson(cached, HistoricalResponse::class.java)
        val cases = body.timeline?.cases
        if (cases.isNullOrEmpty()) {
            tvResult?.text = "Cached trends missing."
            return
        }
        allCases = cases
        buildSortedDateList()
        showChart()
    }

    private fun buildSortedDateList() {
        val tmp = mutableListOf<Pair<Long, Int>>()
        allCases?.forEach { (k, v) ->
            val parsedDate = inputFormats.firstNotNullOfOrNull { fmt -> runCatching { fmt.parse(k) }.getOrNull() }
            if (parsedDate != null) {
                val cal = Calendar.getInstance().apply {
                    time = parsedDate
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                tmp.add(cal.timeInMillis to v)
            } else {
                Log.e(TAG, "Parse fail for key=$k")
            }
        }
        tmp.sortBy { it.first }
        sortedDateList = tmp
    }

    private fun showChart() {
        if (sortedDateList.size < 2) {
            tvResult?.text = "Not enough data to display chart."
            lineChart?.clear(); return
        }

        val dailyCases = mutableListOf<Pair<Long, Int>>()
        for (i in 1 until sortedDateList.size) {
            val today = sortedDateList[i]
            val yesterday = sortedDateList[i - 1]
            val diff = (today.second - yesterday.second).coerceAtLeast(0)
            dailyCases.add(today.first to diff)
        }

        dailyData = dailyCases
        labels = dailyCases.map { outputFormat.format(Date(it.first)) }

        val entries = dailyCases.mapIndexed { index, pair -> Entry(index.toFloat(), pair.second.toFloat()) }

        lineChart?.apply {
            data = LineData(LineDataSet(entries, "New Cases").apply {
                color = Color.BLUE; lineWidth = 2f; setDrawCircles(false); setDrawValues(false)
            })
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(true); setLabelCount(6, true); setDrawGridLines(false)
            }
            axisLeft.apply { axisMinimum = 0f; setDrawGridLines(true) }
            axisRight.isEnabled = false
            description.isEnabled = false; legend.isEnabled = false
            invalidate()
        }

        tvResult?.text = "Loaded ${dailyCases.size} days of new case data."
    }

    private fun zoomToDateMillis(targetMillis: Long, halfWindowDays: Int = 7) {
        val chart = lineChart ?: return
        if (dailyData.isEmpty()) return

        var idx = dailyData.indexOfFirst { it.first == targetMillis }
        if (idx == -1) {
            idx = dailyData.indices.minByOrNull { i -> kotlin.math.abs(dailyData[i].first - targetMillis) } ?: return
        }
        val start = (idx - halfWindowDays).coerceAtLeast(0)
        val end = (idx + halfWindowDays).coerceAtMost(dailyData.lastIndex)
        val visibleCount = (end - start + 1).coerceAtLeast(1)

        chart.setVisibleXRangeMinimum(visibleCount.toFloat())
        chart.setVisibleXRangeMaximum(visibleCount.toFloat())
        chart.moveViewToX(start.toFloat())
        val y = dailyData[idx].second.toFloat()
        chart.centerViewToAnimated(idx.toFloat(), y, chart.axisLeft.axisDependency, 400)
        chart.highlightValue(idx.toFloat(), 0, false)
    }
}
