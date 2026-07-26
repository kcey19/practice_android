package com.shardul.esewazone.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shardul.esewazone.data.repository.ProductRepository

class ProductDetailsViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return ProductDetailsViewModel(repository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }

}