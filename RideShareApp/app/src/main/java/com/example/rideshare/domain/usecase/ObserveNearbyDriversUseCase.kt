package com.example.rideshare.domain.usecase

import com.example.rideshare.data.model.Driver
import com.example.rideshare.data.model.Vehicle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class ObserveNearbyDriversUseCase {

    operator fun invoke(): Flow<List<Driver>> = flow {
        while (true) {
            emit(mockDrivers())
            delay(5000)
        }
    }

    private fun mockDrivers(): List<Driver> {
        val baseLatitude = 37.7749
        val baseLongitude = -122.4194
        return List(3) { index ->
            Driver(
                id = "driver-$index",
                name = "Driver $index",
                latitude = baseLatitude + Random.nextDouble(-0.01, 0.01),
                longitude = baseLongitude + Random.nextDouble(-0.01, 0.01),
                vehicle = Vehicle(
                    plateNumber = "ABC123$index",
                    model = "Sedan",
                    color = "Black"
                ),
                rating = 4.5 + Random.nextDouble(-0.3, 0.3)
            )
        }
    }
}
