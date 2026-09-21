package com.shardul.esewazone.data.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckoutItem(
    val name:String,
    val price:Double
): Parcelable
