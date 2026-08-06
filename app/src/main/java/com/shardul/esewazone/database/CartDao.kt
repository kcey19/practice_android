package com.shardul.esewazone.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao{
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartEntity)
    @Update
    suspend fun updateItem(item: CartEntity)
    @Delete
    suspend fun deleteItem(item: CartEntity)
    @Query("SELECT * FROM my_cart")
    fun getAllItems(): Flow<List<CartEntity>>
    @Query("SELECT * FROM my_cart WHERE id=:id LIMIT 1")
    suspend fun getItemById(id: Int): CartEntity?
    @Query("DELETE FROM my_cart")
    suspend fun clearCart()
}