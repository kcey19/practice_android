package com.shardul.esewazone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.data.model.FavouriteItem
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.repository.FavouriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val repository: FavouriteRepository
) : ViewModel() {

    private val _favourites =
        MutableStateFlow<List<FavouriteItem>>(emptyList())

    val favourites: StateFlow<List<FavouriteItem>> =
        _favourites

    private val _isLoading =
        MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> =
        _isLoading
    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error
    private val _isFavourite =
        MutableStateFlow(false)
    val isFavourite: StateFlow<Boolean> =
        _isFavourite
    fun addToFavourites(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result =
                repository.addToFavourites(product)
            result
                .onSuccess {
                    _isFavourite.value = true
                }
                .onFailure { exception ->
                    _error.value =
                        exception.message
                }
            _isLoading.value = false
        }
    }
    fun getFavourites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result =
                repository.getFavourites()
            result
                .onSuccess { items ->
                    _favourites.value = items
                }
                .onFailure { exception ->
                    _error.value =
                        exception.message
                }
            _isLoading.value = false
        }
    }
}