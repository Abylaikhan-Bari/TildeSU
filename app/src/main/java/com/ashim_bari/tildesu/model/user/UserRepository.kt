package com.ashim_bari.tildesu.model.user

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true // Return true for successful registration
        } catch (e: Exception) {
            false // Return false if registration fails
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            true // Return true for successful login
        } catch (e: Exception) {
            false // Return false if login fails
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            true // Return true for successful password reset
        } catch (e: Exception) {
            false // Return false if password reset fails
        }
    }

    fun getUserEmail(onComplete: (String?) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        onComplete(currentUser?.email)
    }

}
