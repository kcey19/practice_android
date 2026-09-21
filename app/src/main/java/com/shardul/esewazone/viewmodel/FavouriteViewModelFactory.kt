package com.shardul.esewazone.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shardul.esewazone.repository.FavouriteRepository

class FavouriteViewModelFactory(
    private val repository: FavouriteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                FavouriteViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return FavouriteViewModel(
                repository
            ) as T
        }
        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}