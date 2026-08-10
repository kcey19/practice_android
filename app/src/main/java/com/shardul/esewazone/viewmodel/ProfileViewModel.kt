package com.shardul.esewazone.viewmodel

import androidx.lifecycle.ViewModel
import com.shardul.esewazone.repository.AuthRepository

class ProfileViewModel(
    private val repository: AuthRepository
): ViewModel(){
    fun logout(){
        repository.logout()
    }
}




