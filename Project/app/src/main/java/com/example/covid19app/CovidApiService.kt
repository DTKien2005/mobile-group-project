package com.example.covid19app

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

data class HistoricalResponse(
    val timeline: Timeline
)

data class Timeline(
    val cases: Map<String, Int>,
    val deaths: Map<String, Int>,
    val recovered: Map<String, Int>
)

interface CovidApiService {
    @GET("v3/covid-19/historical/Vietnam?lastdays=30")
    fun getVietnamTrends(): Call<HistoricalResponse>
}
