package com.example.covid19app.data

data class VaccineResponseData(
    val country: String,
    val timeline: Map<String, Long>
)
