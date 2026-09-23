package com.shardul.esewazone.repository

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    suspend fun getProductsByCategory(category: String): List<Product> {
        return apiService.getProductsByCategory(category)
    }

    fun deleteReview(productId: String, reviewId: String, onResult: (Boolean, String?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("products")
            .document(productId)
            .collection("reviews")
            .document(reviewId)
            .delete()
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage)
            }
    }
}


