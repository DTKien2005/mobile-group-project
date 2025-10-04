package com.example.covid19app.features.vndashboard.data.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.covid19app.features.vndashboard.data.model.CovidStats
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

object CovidApiService {
    private const val URL = "https://disease.sh/v3/covid-19/countries/VN"

    // Function that call API
    fun fetchCovidStats(context: Context, callback: CovidCallback) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET,
            URL,
            { response ->
                try {
                    //                        // cách 1: parse JSON
                    //                        val obj = JSONObject(response)
                    //                        val country = obj.getString("country")
                    //                        val cases = obj.getInt("cases")
                    //                        val deaths = obj.getInt("deaths")

                    // Cách 2: Use Gson map to CovidStats
                    val stats = Gson().fromJson<CovidStats?>(response, CovidStats::class.java)

                    callback.onSuccess(stats)
                } catch (e: Exception) {
                    callback.onError(e.message)
                }
            },
            { error ->
                callback.onError(error.message)
            })

        queue.add<String?>(stringRequest)
    }

    // Callback interface for Activity take the data
    interface CovidCallback {
        fun onSuccess(stats: CovidStats?)
        fun onError(errorMessage: String?)
    }
}
