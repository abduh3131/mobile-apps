package com.example.rideshare.ui.ride

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rideshare.R
import com.google.android.material.button.MaterialButton

class RideRequestFragment : Fragment(R.layout.fragment_ride_request) {

    private val viewModel: RideViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.submitRequestButton)?.setOnClickListener {
            viewModel.submitRideRequest()
            findNavController().navigate(R.id.action_request_to_tracking)
        }
    }
}
