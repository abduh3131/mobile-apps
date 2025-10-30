package com.example.rideshare.data.model

data class Trip(
    val id: String,
    val status: String,
    val driver: Driver? = null,
    val riderId: String? = null,
    val pickupAddress: String? = null,
    val dropoffAddress: String? = null,
    val fare: Double? = null,
    val durationMinutes: Int? = null
)
