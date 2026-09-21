package com.shardul.esewazone.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.shardul.esewazone.databinding.FragmentMoreBinding
import com.shardul.esewazone.more.MoreScreen
import com.shardul.esewazone.repository.AuthRepository
import com.shardul.esewazone.viewmodel.ProfileViewModel
import com.shardul.esewazone.viewmodel.ProfileViewModelFactory
import com.shardul.esewazone.ui.activities.LoginActivity

class MoreFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            AuthRepository(FirebaseAuth.getInstance())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("DEBUG_CHECK: ${FirebaseAuth.getInstance().currentUser?.displayName}")
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val currentUser =
                    viewModel.currentUser
                        .collectAsStateWithLifecycle()
                        .value

                MoreScreen(
                    userName = currentUser?.displayName?:"Not Found",
                    userEmail = currentUser?.email?:"abcd1234@gmail.com",
                    onBackClick = {findNavController().popBackStack()},
                    onLogOutClick = {
                        viewModel.logout {
                            performSafeLogout()
                        }
                    },
                    onViewProfileClick = {},
                    onFaqClick = {},
                    onTermsClick = {},
                    onBonusClick = {},
                    onRewardsClick = {},
                    onPrivacyClick = {},
                    onAboutUsClick = {},
                    onMyOrdersClick = {},
                    onAllLegalClick = {},
                    onMyReturnsClick = {},
                    onPromoCodeClick = {},
                    onMyProductsClick = {},
                    onShippingAddressClick = {
                        findNavController().navigate(
                            com.shardul.esewazone.R.id.action_moreFragment_to_shippingAddressFragment
                        )
                    },
                    onMyCancellationsClick = {},
                    onCustomerSupportClick = {}
                )
            }
        }
    }
    private fun performSafeLogout() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
}
