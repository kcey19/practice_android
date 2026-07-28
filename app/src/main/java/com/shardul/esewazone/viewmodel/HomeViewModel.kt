package com.shardul.esewazone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.api.RetrofitInstance
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.data.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository(RetrofitInstance.api)
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    init {
        fetchProducts()
    }
    private fun fetchProducts() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getProducts()
                _products.value = response

            } catch (e: Exception) {

                _error.value = e.message

            } finally {
                _loading.value = false

            }
        }
    }
}