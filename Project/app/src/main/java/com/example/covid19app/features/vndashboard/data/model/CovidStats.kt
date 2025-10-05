package com.example.covid19app.features.vndashboard.data.model

import com.google.gson.annotations.SerializedName

class CovidStats // Constructor
    (// Getters and Setters
    @field:SerializedName("updated") var updated: Long,
    @field:SerializedName(
        "country"
    ) var country: String?,
    @field:SerializedName("cases") var cases: Int,
    @field:SerializedName(
        "todayCases"
    ) var todayCases: Int,
    @field:SerializedName("deaths") var deaths: Int,
    @field:SerializedName("todayDeaths") var todayDeaths: Int,
    @field:SerializedName(
        "recovered"
    ) var recovered: Int,
    @field:SerializedName("todayRecovered") var todayRecovered: Int,
    @field:SerializedName(
        "active"
    ) var active: Int,
    @field:SerializedName("critical") var critical: Int,
    @field:SerializedName(
        "tests"
    ) var tests: Int,
    @field:SerializedName("population") var population: Int
)
