package com.shardul.esewazone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.database.CartEntity
import com.shardul.esewazone.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
): ViewModel(){
    val cartItems : Flow<List<CartEntity>> =
        repository.cartItems
    fun addToCart(product: Product){
        viewModelScope.launch {
            repository.addToCart(product)
        }
    }
    fun decreaseQuantity(item: CartEntity){
        viewModelScope.launch {
            repository.decreaseQuantity(item)
        }
    }

    fun increaseQuantity(item: CartEntity){
        viewModelScope.launch {
            repository.increaseQuantity(item)
        }
    }

    fun removeItem(item: CartEntity){
        viewModelScope.launch {
            repository.removeItem(item)
        }
    }
    fun clearCart(){
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}

/*
class cartViewModel(
private val repository: CartRepository
): ViewModel(){

}
 */