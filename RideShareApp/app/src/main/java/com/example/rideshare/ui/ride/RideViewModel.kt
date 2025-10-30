package com.example.rideshare.ui.ride

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideshare.data.repository.RideRepository
import kotlinx.coroutines.launch

class RideViewModel(
    private val rideRepository: RideRepository = RideRepository()
) : ViewModel() {

    private val _tripState = MutableLiveData<TripState>(TripState.Idle)
    val tripState: LiveData<TripState> = _tripState

    fun submitRideRequest() {
        viewModelScope.launch {
            _tripState.value = TripState.SearchingDriver
            val trip = rideRepository.requestRide()
            _tripState.value = TripState.InProgress(tripId = trip.id)
        }
    }

    fun loadTripSummary() {
        viewModelScope.launch {
            val summary = rideRepository.getLatestTripSummary()
            _tripState.value = TripState.Completed(summary?.id ?: "")
        }
    }
}
