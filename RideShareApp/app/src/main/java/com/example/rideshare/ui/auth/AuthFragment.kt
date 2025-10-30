package com.example.rideshare.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rideshare.R
import com.google.android.material.snackbar.Snackbar

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Authenticated -> findNavController().navigate(R.id.action_auth_to_dashboard)
                is AuthState.Error -> Snackbar.make(view, state.message, Snackbar.LENGTH_LONG).show()
                AuthState.Loading -> Unit
                AuthState.Unauthenticated -> Unit
            }
        }
    }
}
