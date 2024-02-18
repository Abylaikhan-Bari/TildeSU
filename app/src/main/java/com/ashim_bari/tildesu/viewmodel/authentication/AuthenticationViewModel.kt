package com.ashim_bari.tildesu.viewmodel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.model.user.UserRepository
import com.ashim_bari.tildesu.view.navigation.Navigation
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {
    private val userRepository = UserRepository()





    suspend fun login(email: String, password: String): Boolean {
        return userRepository.loginUser(email, password)
    }
    suspend fun register(email: String, password: String): Boolean {
        return userRepository.registerUser(email, password)
    }
    suspend fun resetPassword(email: String): Boolean {
        return userRepository.resetPassword(email)
    }





}
