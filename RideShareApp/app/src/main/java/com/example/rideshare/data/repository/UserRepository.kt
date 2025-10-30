package com.example.rideshare.data.repository

import kotlinx.coroutines.delay

class UserRepository {

    suspend fun fetchProfile() {
        delay(250)
    }
}
