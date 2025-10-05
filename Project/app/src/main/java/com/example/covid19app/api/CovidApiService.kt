package com.example.covid19app.api

import com.example.covid19app.data.CovidStatsData
import com.example.covid19app.data.VaccineResponseData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
    fun getVietnamStats(): Call<CovidStatsData>

    // Historical data for Vietnam (ALL days)
    @GET("v3/covid-19/historical/Vietnam?lastdays=all")
    fun getVietnamTrends(): Call<HistoricalResponse>

    // Vietnam vaccine coverage (moved from CallAPI)
    // https://disease.sh/v3/covid-19/vaccine/coverage/countries/Vietnam?lastdays=30&fullData=false
    @GET("v3/covid-19/vaccine/coverage/countries/Vietnam")
    fun getVaccineCoverage(
        @Query("lastdays") lastDays: String = "30",
        @Query("fullData") fullData: Boolean = false
    ): Call<VaccineResponseData>

    // World vaccine coverage (moved from CallAPI)
    // https://disease.sh/v3/covid-19/vaccine/coverage?lastdays=30&fullData=false
    @GET("v3/covid-19/vaccine/coverage")
    fun getWorldData(
        @Query("lastdays") lastDays: String = "30",
        @Query("fullData") fullData: Boolean = false
    ): Call<Map<String, Long>>

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
