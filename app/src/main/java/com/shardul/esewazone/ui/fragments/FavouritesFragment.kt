package com.shardul.esewazone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shardul.esewazone.data.model.FavouriteItem
import com.shardul.esewazone.databinding.FragmentFavouritesBinding

import com.shardul.esewazone.repository.FavouriteRepository
import com.shardul.esewazone.viewmodel.FavouriteViewModel
import com.shardul.esewazone.viewmodel.FavouriteViewModelFactory
import com.shardul.esewazone.ui.favourites.FavouritesScreen


class FavouritesFragment : Fragment() {
    private lateinit var favouriteViewModel: FavouriteViewModel
    private var _binding: FragmentFavouritesBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                FavouritesScreen(
                    viewModel = favouriteViewModel,
                    {findNavController().popBackStack()},
                    { Unit },
                    {favouriteViewModel.deleteAllFavourites()},
                    {selectedIds->
                        favouriteViewModel.deleteSelectedFavourites(selectedIds)}
                )
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        favouriteViewModel.getFavourites()

    }

    private fun setupViewModel() {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val repository = FavouriteRepository(
            auth = auth,
            firestore = firestore
        )

        val factory = FavouriteViewModelFactory(repository)
        favouriteViewModel = ViewModelProvider(
            this,
            factory
        )[FavouriteViewModel::class.java]
    }
}