package com.example.rideshare.data.model

data class Driver(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val vehicle: Vehicle,
    val rating: Double
)
