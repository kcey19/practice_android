package com.shardul.esewazone.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.shardul.esewazone.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(
    private val repository: AuthRepository
): ViewModel(){
    private val _currentUser = MutableStateFlow<FirebaseUser?>(repository.getCurrentUser())
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    fun logout(onSuccess:() -> Unit){
        repository.logout()
        _currentUser.value = null
        onSuccess()
    }
}




