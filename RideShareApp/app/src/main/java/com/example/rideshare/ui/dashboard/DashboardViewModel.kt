package com.example.rideshare.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideshare.domain.usecase.ObserveNearbyDriversUseCase
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val observeNearbyDriversUseCase: ObserveNearbyDriversUseCase = ObserveNearbyDriversUseCase()
) : ViewModel() {

    private val _driverState = MutableLiveData<NearbyDriverState>(NearbyDriverState.Loading)
    val driverState: LiveData<NearbyDriverState> = _driverState

    init {
        viewModelScope.launch {
            observeNearbyDriversUseCase().collect { drivers ->
                _driverState.value = if (drivers.isEmpty()) {
                    NearbyDriverState.None
                } else {
                    NearbyDriverState.DriversAvailable(drivers)
                }
            }
        }
    }
}
