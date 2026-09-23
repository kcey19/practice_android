package com.shardul.esewazone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.database.CartEntity
import com.shardul.esewazone.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
): ViewModel() {
    val cartItems: StateFlow<List<CartEntity>> = repository.getActiveUserCart()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    public fun addToCart(product:Product){
        viewModelScope.launch {
            repository.addToCart(product)
        }
    }
     fun increaseQuantity(item: CartEntity){
        viewModelScope.launch {
            repository.increaseQuantity(item)
        }
    }
    fun increaseQuantity(product: Product){
        viewModelScope.launch{
            val currentCart = cartItems.value
            val item = currentCart.find { it.productId == product.id }
            if (item != null) {
                repository.increaseQuantity(item)
            }else{
                repository.addToCart(product)
            }
        }
    }
     fun decreaseQuantity(item: CartEntity){
        viewModelScope.launch {
            repository.decreaseQuantity(item)
        }
    }

    fun decreaseQuantity(product: Product) {
        viewModelScope.launch {
            val currentCart = cartItems.value
            val item = currentCart.find { it.productId == product.id }
            if (item != null) {
                repository.decreaseQuantity(item)
            }
        }
    }
     fun removeItem(item: CartEntity){
        viewModelScope.launch {
            repository.removeItem(item)
        }
    }
     fun clearUserCart(){
        viewModelScope.launch{
            repository.clearUserCart()
        }
    }

}
