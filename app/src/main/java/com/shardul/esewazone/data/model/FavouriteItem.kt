package com.shardul.esewazone.data.model

data class FavouriteItem(
    val productId:Int = 0,
    val title:String="",
    val price:Double=0.0,
    val image:String="",
    val category:String=""
)