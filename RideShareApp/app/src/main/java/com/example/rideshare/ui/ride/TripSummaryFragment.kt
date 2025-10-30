package com.example.rideshare.ui.ride

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.rideshare.R

class TripSummaryFragment : Fragment(R.layout.fragment_trip_summary) {

    private val viewModel: RideViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadTripSummary()
    }
}
