package com.shardul.esewazone.data.model

data class SelectedLocation(
    val name:String,
    val address:String?,
    val latitude:Double,
    val longitude:Double,
    val placeId:String? = null
)