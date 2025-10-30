package com.example.rideshare.data.repository

import com.example.rideshare.data.model.Trip
import kotlinx.coroutines.delay
import java.util.UUID

class RideRepository {

    private var lastTrip: Trip? = null

    suspend fun requestRide(): Trip {
        delay(500)
        val trip = Trip(id = UUID.randomUUID().toString(), status = "in_progress")
        lastTrip = trip
        return trip
    }

    suspend fun getLatestTripSummary(): Trip? {
        delay(300)
        return lastTrip?.copy(status = "completed")
    }
}
