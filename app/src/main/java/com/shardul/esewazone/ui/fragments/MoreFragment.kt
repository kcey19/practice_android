package com.shardul.esewazone.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.shardul.esewazone.databinding.FragmentMoreBinding
import com.shardul.esewazone.repository.AuthRepository
import com.shardul.esewazone.viewmodel.ProfileViewModel
import com.shardul.esewazone.viewmodel.ProfileViewModelFactory
import com.shardul.esewazone.ui.activities.LoginActivity

class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMoreBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupLogout()

    }

    private fun setupViewModel() {

        val authRepository = AuthRepository(
            FirebaseAuth.getInstance()
        )

        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(authRepository)
        )[ProfileViewModel::class.java]
    }

    private fun setupLogout() {

        binding.cardLogout.setOnClickListener {
            viewModel.logout()
            navigateToLogin()

        }
    }

    private fun navigateToLogin() {

        val intent = Intent(
            requireContext(),
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}