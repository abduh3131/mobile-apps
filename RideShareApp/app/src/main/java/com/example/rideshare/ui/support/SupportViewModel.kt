package com.example.rideshare.ui.support

import androidx.lifecycle.ViewModel
import com.example.rideshare.data.repository.SupportRepository

class SupportViewModel(
    private val supportRepository: SupportRepository = SupportRepository()
) : ViewModel() {

    fun prepareSupportChannels() {
        supportRepository.ensureChannels()
    }
}
