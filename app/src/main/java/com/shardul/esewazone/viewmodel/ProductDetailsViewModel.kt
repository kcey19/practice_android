package com.shardul.esewazone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchProduct(id: Int) {

        viewModelScope.launch {

            _loading.value = true

            try {

                val result = repository.getProductById(id)

                _product.value = result

            } catch (e: Exception) {

                _error.value = e.message

            } finally {

                _loading.value = false

            }
        }
    }
}