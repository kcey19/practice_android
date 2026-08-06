package com.shardul.esewazone.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_cart")
data class CartEntity(
    @PrimaryKey
    val id:Int,
    val title:String,
    val image:String,
    val price:Double,
    val quantity:Int =1
)