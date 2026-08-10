package com.shardul.esewazone.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val firebaseAuth : FirebaseAuth){
    fun logout(){
        firebaseAuth.signOut()
    }
    fun getCurrentUser() = firebaseAuth.currentUser
}
