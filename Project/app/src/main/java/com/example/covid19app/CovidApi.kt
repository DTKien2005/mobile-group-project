package com.example.covid19app

import retrofit2.Call
import retrofit2.http.GET

interface CovidApi {
    @GET("v3/covid-19/countries")
    fun getCountries(): Call<List<Country>>
}