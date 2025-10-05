package com.example.covid19app.api

import com.example.covid19app.data.Country
import com.example.covid19app.data.CovidStatsData
import com.example.covid19app.data.VaccineResponseData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ---------- Models for /historical ----------
data class HistoricalResponse(
    val timeline: Timeline?
)

data class Timeline(
    val cases: Map<String, Int>?,
    val deaths: Map<String, Int>?,
    val recovered: Map<String, Int>?
)

// ---------- API ----------
interface CovidApiService {

    // Vietnam stats today
    // https://disease.sh/v3/covid-19/countries/vietnam?strict=true
    @GET("v3/covid-19/countries/vietnam?strict=true")
    fun getVietnamStats(): Call<CovidStatsData>

    // Vietnam historical timeseries (cases, deaths, recovered)
    // https://disease.sh/v3/covid-19/historical/vietnam?lastdays=all
    @GET("v3/covid-19/historical/vietnam")
    fun getVietnamTrends(
        @Query("lastdays") lastDays: String = "all"
    ): Call<HistoricalResponse>

    // Vietnam vaccine coverage: returns { country, timeline: { "1/1/21": 123, ... } }
    // https://disease.sh/v3/covid-19/vaccine/coverage/countries/vietnam?lastdays=30&fullData=false
    @GET("v3/covid-19/vaccine/coverage/countries/vietnam")
    fun getVaccineCoverage(
        @Query("lastdays") lastDays: String,
        @Query("fullData") fullData: Boolean = false
    ): Call<VaccineResponseData>

    // World vaccine coverage: returns a flat map { "1/1/21": 123, ... }
    // https://disease.sh/v3/covid-19/vaccine/coverage?lastdays=30&fullData=false
    @GET("v3/covid-19/vaccine/coverage")
    fun getWorldVaccineCoverage(
        @Query("lastdays") lastDays: String,
        @Query("fullData") fullData: Boolean = false
    ): Call<Map<String, Long>>

    // All countries (you already render these)
    // https://disease.sh/v3/covid-19/countries
    @GET("v3/covid-19/countries")
    fun getCountries(): Call<List<Country>>
}

// ---------- Retrofit singleton ----------
object RetrofitInstance {
    private const val BASE_URL = "https://disease.sh/"

    val api: CovidApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC   // or BODY for deeper debugging
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL) // must end with /
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CovidApiService::class.java)
    }
}
