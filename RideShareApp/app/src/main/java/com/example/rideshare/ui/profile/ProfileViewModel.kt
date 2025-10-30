package com.example.rideshare.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideshare.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    fun refreshProfile() {
        viewModelScope.launch {
            userRepository.fetchProfile()
        }
    }
}
