package com.shardul.esewazone.checkout


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shardul.esewazone.repository.CartRepository

class CheckoutViewModelFactory(
    private val cartRepository: CartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                CheckoutViewModel::class.java
            )
        ) {

            return CheckoutViewModel(
                cartRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}