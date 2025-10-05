package com.example.covid19app.api

import com.example.covid19app.features.vndashboard.data.model.CovidStats
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// --- data models for historical endpoint ---
data class HistoricalResponse(
    val timeline: Timeline?
)
data class Timeline(
    val cases: Map<String, Int>?,
    val deaths: Map<String, Int>?,
    val recovered: Map<String, Int>?
)

// --- unified Retrofit service ---
interface CovidApiService {

    // Current stats for Vietnam
    @GET("v3/covid-19/countries/VN")
    fun getVietnamStats(): Call<CovidStats>

    // Historical data for Vietnam (ALL days)
    @GET("v3/covid-19/historical/Vietnam?lastdays=all")
    fun getVietnamTrends(): Call<HistoricalResponse>

    companion object {
        fun create(): CovidApiService {
            val client = OkHttpClient.Builder().build()

            return Retrofit.Builder()
                .baseUrl("https://disease.sh/") // must end with /
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CovidApiService::class.java)
        }
    }
}
