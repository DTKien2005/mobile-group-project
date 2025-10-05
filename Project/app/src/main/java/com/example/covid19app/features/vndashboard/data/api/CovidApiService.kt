package com.example.covid19app.features.vndashboard.data.api

import com.example.covid19app.features.vndashboard.data.model.CovidStats
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// --- data models for historical endpoint (moved here so everything is in one place) ---
data class HistoricalResponse(
    val timeline: Timeline
)
data class Timeline(
    val cases: Map<String, Int>,
    val deaths: Map<String, Int>,
    val recovered: Map<String, Int>
)

// --- unified Retrofit service ---
interface CovidApiService {

    // Current stats for Vietnam (was Volley before)
    @GET("v3/covid-19/countries/VN")
    fun getVietnamStats(): Call<CovidStats>

    // Historical data for Vietnam (last 30 days)
    @GET("v3/covid-19/historical/Vietnam?lastdays=30")
    fun getVietnamTrends(): Call<HistoricalResponse>

    companion object {
        fun create(): CovidApiService {
            return Retrofit.Builder()
                .baseUrl("https://disease.sh/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CovidApiService::class.java)
        }
    }
}
