package com.example.covid19app.data

// If you want a type, use this wrapper, but the interface above already returns Map<String, Long>.
data class WorldData(
    val timeline: Map<String, Long>
)