package com.ashim_bari.tildesu.viewmodel

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

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> get() = _userEmail

    fun getUserEmail() {
        viewModelScope.launch {
            userRepository.getUserEmail { email ->
                _userEmail.postValue(email)
            }
        }
    }
    fun register(email: String, password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.registerUser(email, password)
            onComplete(result)
        }
    }

    fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.loginUser(email, password)
            onComplete(result)
        }
    }

    fun resetPassword(email: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.resetPassword(email)
            onComplete(result)
        }
    }

    fun logout(navController: NavHostController) {
        viewModelScope.launch {
            // Call the log out function from the UserRepository
            val result = userRepository.logout()
            navController.navigate(Navigation.AUTHENTICATION_ROUTE)
            // Call the onComplete callback to handle further actions, if needed

        }
    }

}
