package com.shardul.esewazone.data.model

import com.google.firebase.Timestamp

data class ReviewModel(
    val reviewId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Timestamp? = null
)