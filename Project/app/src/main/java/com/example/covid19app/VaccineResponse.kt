package com.example.covid19app

data class VaccineResponse(
    val country: String,
    val timeline: Map<String, Long>
)
