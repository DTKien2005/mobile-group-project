package com.example.covid19app.api

import com.example.covid19app.data.Country
import com.example.covid19app.data.CovidStatsData
import com.example.covid19app.data.VaccineResponseData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- Models for historical endpoint ---
data class HistoricalResponse(
    val timeline: Timeline?
)

data class Timeline(
    val cases: Map<String, Int>?,
    val deaths: Map<String, Int>?,
    val recovered: Map<String, Int>?
)

// --- Unified Retrofit Service Interface ---
interface CovidApiService {

    // Fetch all countries' COVID data
    @GET("v3/covid-19/countries")
    fun getCountries(): Call<List<Country>>

    // Vietnam current stats
    @GET("v3/covid-19/countries/VN")
    fun getVietnamStats(): Call<CovidStatsData>

    // Vietnam historical data (all days)
    @GET("v3/covid-19/historical/Vietnam?lastdays=all")
    fun getVietnamTrends(): Call<HistoricalResponse>

    // Vietnam vaccine coverage
    @GET("v3/covid-19/vaccine/coverage/countries/Vietnam")
    fun getVaccineCoverage(
        @Query("lastdays") lastDays: String = "30",
        @Query("fullData") fullData: Boolean = false
    ): Call<VaccineResponseData>

    // Global vaccine coverage
    @GET("v3/covid-19/vaccine/coverage")
    fun getWorldVaccineCoverage(
        @Query("lastdays") lastDays: String = "30",
        @Query("fullData") fullData: Boolean = false
    ): Call<Map<String, Long>>
}

// --- Retrofit Singleton Instance ---
object RetrofitInstance {
    private const val BASE_URL = "https://disease.sh/"

    val api: CovidApiService by lazy {
        val client = OkHttpClient.Builder().build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CovidApiService::class.java)
    }
}
