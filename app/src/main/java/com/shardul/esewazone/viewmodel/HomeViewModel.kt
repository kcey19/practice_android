package com.shardul.esewazone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.api.RetrofitInstance
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.data.repository.ProductRepository
import com.shardul.esewazone.database.CartEntity
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.repository.FavouriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val cartrepository: CartRepository,
    private val favouriterepository: FavouriteRepository
) : ViewModel() {

    private val repository = ProductRepository(RetrofitInstance.api)

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _featuredProducts = MutableLiveData<List<Product>>()
    val featuredProducts: LiveData<List<Product>> get() = _featuredProducts

    private val _hotDeals = MutableLiveData<List<Product>>()
    val hotDeals: LiveData<List<Product>> get() = _hotDeals

    private val _popularBrands = MutableLiveData<List<Product>>()
    val popularBrands: LiveData<List<Product>> get() = _popularBrands

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _favouriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favouriteIds: StateFlow<Set<Int>> = _favouriteIds.asStateFlow()

    val cartItems: StateFlow<List<CartEntity>> = cartrepository
        .getActiveUserCart()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadAllHomeData()
    }

    fun loadAllHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val allProducts = repository.getProducts()
                _products.value = allProducts

                if (allProducts.isNotEmpty()) {
                    _featuredProducts.value = allProducts

                    _hotDeals.value = if (allProducts.size >= 14) {
                        allProducts.subList(10, 14)
                    } else {
                        allProducts.drop(10).take(4)
                    }

                    val maxStartIndex = (allProducts.size - 14).coerceAtLeast(0)
                    val randomStartIndex = if (maxStartIndex > 0) (0..maxStartIndex).random() else 0

                    _popularBrands.value = allProducts.drop(randomStartIndex).take(14)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to fetch products"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartrepository.addToCart(product)
        }
    }

    fun increaseQuantity(product: Product) {
        viewModelScope.launch {
            val item = cartItems.value.firstOrNull { it.productId == product.id }
            if (item != null) {
                cartrepository.increaseQuantity(item)
            }
        }
    }

    fun decreaseQuantity(product: Product) {
        viewModelScope.launch {
            val item = cartItems.value.firstOrNull { it.productId == product.id }
            if (item != null) {
                cartrepository.decreaseQuantity(item)
            }
        }
    }


    fun toggleFavourite(product: Product) {
        viewModelScope.launch {
            if (product.id in _favouriteIds.value) {
                val result = favouriterepository.removeSelectedFavourites(setOf(product.id))
                result.onSuccess {
                    _favouriteIds.value = _favouriteIds.value - product.id
                }
            } else {
                val result = favouriterepository.addToFavourites(product)
                result.onSuccess {
                    _favouriteIds.value = _favouriteIds.value + product.id
                }
            }
        }
    }
}