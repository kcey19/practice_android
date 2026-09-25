package com.shardul.esewazone.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.database.CartEntity
import com.shardul.esewazone.repository.CartRepository
import com.shardul.esewazone.repository.FavouriteRepository
import com.shardul.esewazone.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle(emptyList(), emptyList()))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val cartItems: StateFlow<List<CartEntity>> = cartRepository.getActiveUserCart()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _favouriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favouriteIds: StateFlow<Set<Int>> = _favouriteIds.asStateFlow()

    private var allProductsCache: List<Product> = emptyList()

    init {
        fetchAllProductsForSearch()
        loadFavourites()
    }

    private fun fetchAllProductsForSearch() {
        viewModelScope.launch {
            try {
                allProductsCache = productRepository.getProducts()
            } catch (e: Exception) {
                allProductsCache = emptyList()
            }
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            val result = favouriteRepository.getFavourites()
            result.onSuccess { list ->
                _favouriteIds.value = list.map { it.productId }.toSet()
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle(emptyList(), emptyList())
        }
    }

    fun performSearch(searchQuery: String) {
        viewModelScope.launch {
            if (searchQuery.isBlank()) {
                _uiState.value = SearchUiState.Idle(emptyList(), emptyList())
                return@launch
            }

            val filtered = allProductsCache.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }

            if (filtered.isEmpty()) {
                _uiState.value = SearchUiState.Empty
            } else {
                _uiState.value = SearchUiState.Results(
                    products = filtered,
                    totalCount = filtered.size
                )
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(product)
        }
    }

    fun increaseQuantity(product: Product) {
        viewModelScope.launch {
            val matchingCartEntity = cartItems.value.find { it.productId == product.id }
            if (matchingCartEntity != null) {
                cartRepository.increaseQuantity(matchingCartEntity)
            } else {
                cartRepository.addToCart(product)
            }
        }
    }

    fun decreaseQuantity(product: Product) {
        viewModelScope.launch {
            val matchingCartEntity = cartItems.value.find { it.productId == product.id }
            if (matchingCartEntity != null) {
                cartRepository.decreaseQuantity(matchingCartEntity)
            }
        }
    }

    fun toggleFavourite(product: Product) {
        viewModelScope.launch {
            val current = _favouriteIds.value
            if (current.contains(product.id)) {
                favouriteRepository.removeSelectedFavourites(setOf(product.id))
                _favouriteIds.value = current - product.id
            } else {
                favouriteRepository.addToFavourites(product)
                _favouriteIds.value = current + product.id
            }
        }
    }

    fun clearHistory() {
    }
}