package com.ashim_bari.tildesu.viewmodel.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ashim_bari.tildesu.model.user.UserRepository

class AuthenticationViewModel : ViewModel() {
    private val userRepository = UserRepository()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = userRepository.loginUser(email, password)
            if (result) {
                Log.d("AuthenticationViewModel", "User logged in successfully")
            } else {
                Log.e("AuthenticationViewModel", "Login failed")
            }
            result
        } catch (e: Exception) {
            Log.e("AuthenticationViewModel", "Login failed", e)
            false
        }
    }
    suspend fun isEmailRegistered(email: String): Boolean {
        return userRepository.isEmailRegistered(email)
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            val result = userRepository.registerUser(email, password)
            if (result) {
                Log.d("AuthenticationViewModel", "User registered successfully")
            } else {
                Log.e("AuthenticationViewModel", "Registration failed")
            }
            result
        } catch (e: Exception) {
            Log.e("AuthenticationViewModel", "Registration failed", e)
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            val result = userRepository.resetPassword(email)
            if (result) {
                Log.d("AuthenticationViewModel", "Password reset email sent successfully")
            } else {
                Log.e("AuthenticationViewModel", "Password reset failed")
            }
            result
        } catch (e: Exception) {
            Log.e("AuthenticationViewModel", "Password reset failed", e)
            false
        }
    }
}

