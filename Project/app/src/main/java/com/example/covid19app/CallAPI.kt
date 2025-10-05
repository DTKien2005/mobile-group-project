package com.example.covid19app

import retrofit2.http.GET
import retrofit2.Call

interface CallAPI {

    @GET("/v3/covid-19/vaccine/coverage/countries/Vietnam?lastdays=30&fullData=false")
    fun getVaccineCoverage(): Call<VaccineResponse>

    @GET("/v3/covid-19/all")
    fun getWorldData(): Call<WorldData>
}