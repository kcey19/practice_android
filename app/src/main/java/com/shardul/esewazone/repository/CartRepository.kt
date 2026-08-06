package com.shardul.esewazone.repository

import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.database.CartDao
import com.shardul.esewazone.database.CartEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(
    private val cartDao: CartDao
) {
    val cartItems: Flow<List<CartEntity>> =
        cartDao.getAllItems()
    suspend fun addToCart(product: Product) {
        val existingItem = cartDao.getItemById(product.id)
        if (existingItem == null) {
            val cartItem = CartEntity(
                id = product.id,
                title = product.title,
                image = product.image,
                price = product.price,
                quantity = 1
            )
            cartDao.insertItem(cartItem)
        }
        else {
            cartDao.updateItem(
                existingItem.copy(
                    quantity = existingItem.quantity + 1
                )
            )
        }
    }

    suspend fun decreaseQuantity(item: CartEntity){
        if(item.quantity > 1){
            cartDao.updateItem(
                item.copy(
                    quantity = item.quantity - 1
                )
            )
        }
        else{
            cartDao.deleteItem(item)
        }
    }

    suspend fun increaseQuantity(item: CartEntity){
        cartDao.updateItem(
            item.copy(
                quantity = item.quantity + 1
            )
        )
    }

    suspend fun removeItem(item: CartEntity) {
        cartDao.deleteItem(item)
    }
    suspend fun clearCart() {
        cartDao.clearCart()
    }
}