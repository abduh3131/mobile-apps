package com.example.rideshare.ui.ride

sealed class TripState {
    data object Idle : TripState()
    data object SearchingDriver : TripState()
    data class InProgress(val tripId: String) : TripState()
    data class Completed(val tripId: String) : TripState()
}
