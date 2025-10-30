package com.example.rideshare.ui.splash

import androidx.lifecycle.ViewModel
import com.example.rideshare.data.repository.AuthRepository

class SplashViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    suspend fun initialize() {
        authRepository.prefetchUser()
    }

    fun isAuthenticated(): Boolean = authRepository.isLoggedIn()
}
