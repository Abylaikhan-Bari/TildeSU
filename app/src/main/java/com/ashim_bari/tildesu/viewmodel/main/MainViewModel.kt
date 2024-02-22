package com.ashim_bari.tildesu.viewmodel.main

import android.net.Uri
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.model.user.UserRepository
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel:ViewModel() {

    private val userRepository = UserRepository()

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> get() = _userEmail
    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    // Inside MainViewModel
    private val _progressData = MutableLiveData<Map<String, Pair<Float, Int>>>()
    val progressData: LiveData<Map<String, Pair<Float, Int>>> = _progressData



    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        _isLoggedIn.value = userRepository.isLoggedIn()
    }
    fun getUserEmail() {
        viewModelScope.launch {
            userRepository.getUserEmail { email ->
                _userEmail.postValue(email)
            }
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
    init {
        loadUserProgress()
    }

    fun loadUserProgress() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                // Fetch progress data from repository and update LiveData
                val userProgress = userRepository.getUserProgress(userId)
                _progressData.value = userProgress
            }
        }
    }
    init {
        fetchProfileImageUrl()
    }

    // Function to upload a profile image and update the LiveData
    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            val imageUrl = userRepository.uploadUserImage(uri)
            _profileImageUrl.value = imageUrl
            val success = userRepository.uploadUserImage(uri)
            if (success != null) {
                fetchProfileImageUrl()
            }
        }
    }

    // Function to fetch and display the profile image URL
    private fun fetchProfileImageUrl() {
        viewModelScope.launch {
            val imageUrl = userRepository.getUserImage()
            _profileImageUrl.value = imageUrl
        }
    }
    fun updatePassword(newPassword: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.updatePassword(newPassword)
            onComplete(success)
        }
    }
}