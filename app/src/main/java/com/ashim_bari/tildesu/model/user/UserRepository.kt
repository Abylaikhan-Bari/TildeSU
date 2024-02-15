package com.ashim_bari.tildesu.model.user

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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

    suspend fun uploadUserImage(uri: Uri): String? {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return null
            val uid = currentUser.uid
            val imageRef = storageRef.child("images/$uid/profile.jpg")
            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserImage(): String? {
        return try {
            val currentUser = firebaseAuth.currentUser ?: return null
            val uid = currentUser.uid
            val imageRef = storageRef.child("images/$uid/profile.jpg")
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}
