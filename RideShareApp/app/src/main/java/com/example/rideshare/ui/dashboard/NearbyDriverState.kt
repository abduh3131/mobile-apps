package com.example.rideshare.ui.dashboard

import com.example.rideshare.data.model.Driver

sealed class NearbyDriverState {
    data object Loading : NearbyDriverState()
    data object None : NearbyDriverState()
    data class DriversAvailable(val drivers: List<Driver>) : NearbyDriverState()
}
