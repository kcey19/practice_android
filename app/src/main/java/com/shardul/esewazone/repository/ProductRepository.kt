package com.shardul.esewazone.data.repository

import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.api.ApiService

class ProductRepository(
    private val apiService: ApiService
) {

    suspend fun getProducts(): List<Product> {
        return apiService.getProducts()
    }
    suspend fun getProductById(id: Int): Product {
        return apiService.getProductById(id)
    }

}