package com.example.covid19app

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TrendsFragment : Fragment() {
    private val TAG = "TrendsFragment"
    
    private var lineChart: LineChart? = null
    private var tvResult: TextView? = null

    private var currentCall: Call<HistoricalResponse>? = null

    private var allCases: Map<String, Int>? = null
    private var sortedDateList: List<Pair<Long, Int>> = emptyList()

    private val inputFormat = SimpleDateFormat("M/d/yy", Locale.ENGLISH)
    private val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.activity_trends, container, false)
        lineChart = root.findViewById(R.id.lineChart)
        tvResult = root.findViewById(R.id.tvResult)

        val btnPickDate: Button = root.findViewById(R.id.btnPickDate)
        btnPickDate.setOnClickListener { showDatePicker() }

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
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val selMillis = selCal.timeInMillis

                if (sortedDateList.isEmpty()) {
                    tvResult?.text = "Chưa có dữ liệu để tra cứu"
                    return@DatePickerDialog
                }

                var nearest = sortedDateList.lastOrNull { it.first <= selMillis }

                if (nearest == null) {
                    nearest = sortedDateList.firstOrNull { it.first >= selMillis }
                }

                if (nearest == null) {
                    tvResult?.text = "Không có dữ liệu nào để hiển thị"
                    return@DatePickerDialog
                }

                val prev = sortedDateList.lastOrNull { it.first < nearest.first }
                val prevVal = prev?.second ?: 0
                val diff = (nearest.second - prevVal).coerceAtLeast(0)

                val nearestDate = outputFormat.format(Date(nearest.first))
                tvResult?.text = "Kết quả gần nhất ($nearestDate): $diff ca nhiễm mới"
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun fetchCovidData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://disease.sh/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CovidApiService::class.java)
        val call = api.getVietnamTrends()
        currentCall = call

        call.enqueue(object : Callback<HistoricalResponse> {
            override fun onResponse(call: Call<HistoricalResponse>, response: Response<HistoricalResponse>) {
                // if fragment not added or views released -> skip UI work
                if (!isAdded || lineChart == null || tvResult == null) {
                    Log.w(TAG, "Fragment not ready -> skipping onResponse UI update")
                    return
                }

                allCases = response.body()?.timeline?.cases
                buildSortedDateList()
                showChart()
            }

            override fun onFailure(call: Call<HistoricalResponse>, t: Throwable) {
                if (!isAdded || tvResult == null) {
                    Log.w(TAG, "Fragment not ready -> skipping onFailure UI update")
                    return
                }
                Log.e(TAG, "API Error", t)
                tvResult?.text = "Lỗi tải dữ liệu"
            }
        })
    }

    private fun buildSortedDateList() {
        val tmp = mutableListOf<Pair<Long, Int>>()
        allCases?.forEach { (k, v) ->
            val d = runCatching { inputFormat.parse(k) }.getOrNull()
            if (d != null) {
                val cal = Calendar.getInstance().apply {
                    time = d
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                tmp.add(Pair(cal.timeInMillis, v))
            } else {
                Log.e(TAG, "Parse fail for key=$k")
            }
        }
        tmp.sortBy { it.first }
        sortedDateList = tmp
    }

    private fun showChart() {
        if (sortedDateList.size < 2) return
        val dailyCases = mutableListOf<Pair<Long, Int>>()
        for (i in 1 until sortedDateList.size) {
            val today = sortedDateList[i]
            val yesterday = sortedDateList[i - 1]
            val diff = (today.second - yesterday.second).coerceAtLeast(0)
            dailyCases.add(Pair(today.first, diff))
        }

        val labels = dailyCases.map { outputFormat.format(Date(it.first)) }
        val entries = dailyCases.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.second.toFloat())
        }

        lineChart?.apply {
            data = LineData(
                LineDataSet(entries, "Ca nhiễm mới").apply {
                    color = Color.BLUE
                    lineWidth = 2f
                    setDrawCircles(false)
                    setDrawValues(false)
                }
            )
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false) // ẩn nhãn X cho gọn
                setDrawGridLines(false)
            }
            axisLeft.apply {
                axisMinimum = 0f
                setDrawLabels(false)
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            invalidate()
        }
    }
}