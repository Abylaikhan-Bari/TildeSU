package com.ashim_bari.tildesu.viewmodel.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.model.user.UserProfile
import com.ashim_bari.tildesu.model.user.UserRepository
import com.ashim_bari.tildesu.view.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel: ViewModel() {

    private val userRepository = UserRepository()

    private val _userEmail = MutableLiveData<String?>()
    val userEmail: LiveData<String?> get() = _userEmail

    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _progressData = MutableLiveData<Map<String, Pair<Float, Int>>>()
    val progressData: LiveData<Map<String, Pair<Float, Int>>> = _progressData
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile
    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        _isLoggedIn.value = userRepository.isLoggedIn()
        // Log user login status
        if (_isLoggedIn.value == true) {
            println("User is logged in.")
        } else {
            println("User is not logged in.")
        }
    }


    fun fetchUserProfile() {
        userRepository.getUserProfile { userProfile ->
            _userProfile.postValue(userProfile)
        }
    }
    fun updateUserProfile(userProfile: UserProfile) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.updateUserProfile(userId, userProfile)
            _userProfile.postValue(userProfile) // Update LiveData to reflect changes immediately
        }
    }

//    fun getUserEmail() {
//        viewModelScope.launch {
//            userRepository.getUserEmail { email ->
//                _userEmail.postValue(email)
//                // Log user email retrieval
//                println("User email retrieved: $email")
//            }
//        }
//    }

    fun logout(navController: NavHostController) {
        viewModelScope.launch {
            // Call the log out function from the UserRepository
            val result = userRepository.logout()
            if (result) {
                // Log successful logout
                println("User logged out successfully")
            } else {
                // Log logout failure
                println("Logout failed")
            }
            // Navigate to authentication route
            navController.navigate(Navigation.AUTHENTICATION_ROUTE)
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
                // Log user progress retrieval
                println("User progress loaded: $userProgress")
            }
        }
    }

    init {
        fetchProfileImageUrl()
    }

    private fun fetchProfileImageUrl() {
        viewModelScope.launch {
            val imageUrl = userRepository.getUserImage()
            _profileImageUrl.value = imageUrl
            // Log profile image URL retrieval
            println("Profile image URL fetched: $imageUrl")
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            val imageUrl = userRepository.uploadUserImage(uri)
            _profileImageUrl.value = imageUrl
            // Log profile image upload result
            if (imageUrl != null) {
                println("Profile image uploaded successfully. Image URL: $imageUrl")
            } else {
                println("Failed to upload profile image")
            }
            // Attempt to fetch profile image URL again after upload
            fetchProfileImageUrl()
        }
    }
    fun reAuthenticate(currentPassword: String, onComplete: (Boolean) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email ?: return onComplete(false)

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        viewModelScope.launch {
            try {
                user.reauthenticate(credential).await()
                println("Re-authentication successful")
                onComplete(true)
            } catch (e: Exception) {
                println("Re-authentication failed: ${e.message}")
                onComplete(false)
            }
        }
    }

    fun updatePassword(newPassword: String, currentPassword: String? = null, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.updatePassword(newPassword, currentPassword)
            onComplete(success)
            // Log password update result
            if (success) {
                println("Password updated successfully")
            } else {
                println("Password update failed")
            }
        }
    }
}