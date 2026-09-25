package com.shardul.esewazone.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "my_cart")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val userId: String,
    val productId:Int,
    val quantity:Int = 1,
    val price:Double,
    val image:String,
    val title:String
)

