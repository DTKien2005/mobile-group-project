package com.example.covid19app.data

data class Country(
    val country: String,
    val countryInfo: CountryInfo,
    val cases: Int,
    val deaths: Int,
    val recovered: Int
)

data class CountryInfo(
    val flag: String
)