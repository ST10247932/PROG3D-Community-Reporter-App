package com.example.spottr

data class Incident(
    val location: String = "",
    val dateTime: String = "",
    val description: String = "",
    val picture: String? = null, // optional
    val lat: Double,
    val lng: Double
)
