package com.example.rideshare.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rideshare.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.initialize()
            delay(1500)
            val destination = if (viewModel.isAuthenticated()) {
                R.id.action_splash_to_auth // placeholder: should navigate to dashboard when implemented
            } else {
                R.id.action_splash_to_auth
            }
            findNavController().navigate(destination)
        }
    }
}
