package com.example.rideshare.ui.ride

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rideshare.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: RideViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.emergencyButton)?.setOnClickListener {
            Snackbar.make(view, "Emergency contacted", Snackbar.LENGTH_LONG).show()
        }
        viewModel.tripState.observe(viewLifecycleOwner) { state ->
            if (state is TripState.Completed) {
                findNavController().navigate(R.id.action_tracking_to_summary)
            }
        }
    }
}
