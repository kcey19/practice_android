package com.shardul.esewazone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.repository.FavouriteRepository

class HomeViewModelFactory(
    private val cartRepository: CartRepository,
    private val favouriteRepository: FavouriteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(
                cartRepository,
                favouriteRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}