package com.example.rideshare.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rideshare.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.startRideRequestButton)?.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_request)
        }

        viewModel.driverState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NearbyDriverState.DriversAvailable -> {
                    Snackbar.make(view, "Drivers nearby: ${state.drivers.size}", Snackbar.LENGTH_SHORT).show()
                }
                NearbyDriverState.Loading -> Unit
                NearbyDriverState.None -> Snackbar.make(view, "No drivers nearby", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
