package com.ashim_bari.tildesu.model.user

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
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
    suspend fun logout(): Boolean {
        return try {
            firebaseAuth.signOut()
            true // Return true for successful log out
        } catch (e: Exception) {
            false // Return false if log out fails
        }
    }

    fun getUserEmail(onComplete: (String?) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        onComplete(currentUser?.email)
    }

//    suspend fun updateEmail(newEmail: String, onComplete: (Boolean) -> Unit) {
//        try {
//            firebaseAuth.currentUser?.verifyBeforeUpdateEmail(newEmail)?.await()
//            onComplete(true)
//        } catch (e: Exception) {
//            Log.e("UpdateEmail", "Failed to update email", e)
//            onComplete(false)
//        }
//    }


    suspend fun updatePassword(newPassword: String): Boolean {
        return try {
            firebaseAuth.currentUser?.updatePassword(newPassword)?.await()
            true // Return true for successful password update
        } catch (e: Exception) {
            false // Return false if password update fails
        }
    }

    private val storageReference = Firebase.storage.reference

    suspend fun uploadUserImage(uri: Uri): String? {
        val userId = firebaseAuth.currentUser?.uid ?: return null
        // Adjusted the file path to include the user ID for unique storage per user
        val imageRef = storageReference.child("profileImages/$userId/profilePic.jpg")

        try {
            // Upload the file and wait for it to complete
            imageRef.putFile(uri).await()

            // After upload, retrieve and return the download URL
            return imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Log the error or handle it as needed
            e.printStackTrace()
            return null
        }
    }

    suspend fun getUserImage(): String? {
        val userId = firebaseAuth.currentUser?.uid ?: return null
        val imageRef = storageReference.child("profileImages/$userId/profilePic.jpg")

        return try {
            // Directly return the download URL
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            // If there's an error (e.g., file not found), handle accordingly
            e.printStackTrace()
            null
        }
    }
}
