package com.example.covid19app.data

import com.google.gson.annotations.SerializedName

data class CovidStatsData(
    @SerializedName("updated") val updated: Long,
    @SerializedName("country") val country: String?,
    @SerializedName("cases") val cases: Int,
    @SerializedName("todayCases") val todayCases: Int,
    @SerializedName("deaths") val deaths: Int,
    @SerializedName("todayDeaths") val todayDeaths: Int,
    @SerializedName("recovered") val recovered: Int,
    @SerializedName("todayRecovered") val todayRecovered: Int,
    @SerializedName("active") val active: Int,
    @SerializedName("critical") val critical: Int,
    @SerializedName("tests") val tests: Int,
    @SerializedName("population") val population: Int
)
