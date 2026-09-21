package com.shardul.esewazone.repository

import androidx.privacysandbox.ads.adservices.adid.AdId
import com.google.firebase.auth.FirebaseAuth
import com.shardul.esewazone.data.model.Product
import com.shardul.esewazone.database.CartDao
import com.shardul.esewazone.database.CartEntity
import kotlinx.coroutines.flow.Flow
import java.security.PrivateKey

//class CartRepository(
//    private val cartDao: CartDao
//) {
//    val cartItems: Flow<List<CartEntity>> =
//        cartDao.getAllItems()
//    suspend fun addToCart(product: Product) {
//        val existingItem = cartDao.getItemById(product.id)
//        if (existingItem == null) {
//            val cartItem = CartEntity(
//                id = product.id,
//                title = product.title,
//                image = product.image,
//                price = product.price,
//                quantity = 1
//            )
//            cartDao.insertItem(cartItem)
//        }
//        else {
//            cartDao.updateItem(
//                existingItem.copy(
//                    quantity = existingItem.quantity + 1
//                )
//            )
//        }
//    }
//
//    suspend fun decreaseQuantity(item: CartEntity){
//        if(item.quantity > 1){
//            cartDao.updateItem(
//                item.copy(
//                    quantity = item.quantity - 1
//                )
//            )
//        }
//        else{
//            cartDao.deleteItem(item)
//        }
//    }
//
//    suspend fun increaseQuantity(item: CartEntity){
//        cartDao.updateItem(
//            item.copy(
//                quantity = item.quantity + 1
//            )
//        )
//    }
//
//    suspend fun removeItem(item: CartEntity) {
//        cartDao.deleteItem(item)
//    }
//    suspend fun clearCart() {
//        cartDao.clearCart()
//    }
//}

class CartRepository(
        private val cartdao:CartDao,
        private val auth: FirebaseAuth
){
    private val currentUserID: String
        get() = auth.currentUser?.uid ?: ""

    fun getActiveUserCart():Flow<List<CartEntity>>{
        return cartdao.getCartItemByUser(currentUserID)
    }
    suspend fun addToCart(product: Product){
        val userId = currentUserID
        val existingItem = cartdao.getItemById(product.id,userId)
        if (existingItem == null){
            cartdao.insertItem(
                CartEntity(
                    userId = userId,
                    productId = product.id,
                    title = product.title,
                    image = product.image,
                    quantity = 1,
                    price = product.price
                )
            )

        } else{
            cartdao.updateItem(
                existingItem.copy(
                    quantity = existingItem.quantity + 1
                )
            )
        }
    }
    suspend fun increaseQuantity(item: CartEntity){
        cartdao.updateItem(
            item.copy(
                quantity = item.quantity + 1
            )
        )
    }
    suspend fun decreaseQuantity(item: CartEntity){
        if(item.quantity>1){
            cartdao.updateItem(
                item.copy(
                    quantity = item.quantity -1
                )
            )
        } else{
            removeItem(item)
        }
    }

    suspend fun removeItem(item: CartEntity){
        cartdao.deleteCartItem(item.id,currentUserID)
    }

    suspend fun clearUserCart(){
        cartdao.clearCart(currentUserID)
    }
}

