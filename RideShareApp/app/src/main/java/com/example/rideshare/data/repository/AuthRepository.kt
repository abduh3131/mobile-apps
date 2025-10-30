package com.example.rideshare.data.repository

import com.example.rideshare.ui.auth.AuthState
import kotlinx.coroutines.delay

class AuthRepository {

    private var cachedUserId: String? = null

    suspend fun prefetchUser() {
        delay(250)
    }

    fun isLoggedIn(): Boolean = cachedUserId != null

    suspend fun login(email: String, password: String): AuthState {
        delay(500)
        cachedUserId = "demo-user"
        return AuthState.Authenticated(cachedUserId!!)
    }

    suspend fun signup(email: String, password: String): AuthState {
        delay(500)
        cachedUserId = "demo-user"
        return AuthState.Authenticated(cachedUserId!!)
    }
}
