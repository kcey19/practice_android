package com.shardul.esewazone.ui.search

import com.shardul.esewazone.data.model.Product

sealed interface SearchUiState {
    data class Idle(
        val searchHistory: List<String>,
        val popularSearches: List<String>
    ) : SearchUiState

    data class Suggestions(
        val suggestions: List<String>
    ) : SearchUiState

    data class Results(
        val products: List<Product>,
        val totalCount: Int
    ) : SearchUiState

    object Empty : SearchUiState
}

data class FilterState(
    val priceRange: ClosedFloatingPointRange<Float> = 0f..150000f,
    val selectedBrands: Set<String> = emptySet(),
    val cashOnDelivery: Boolean = false,
    val freeShipping: Boolean = false
)

enum class SortOption {
    BEST_SELLER, PRICE_LOW_HIGH, PRICE_HIGH_LOW
}