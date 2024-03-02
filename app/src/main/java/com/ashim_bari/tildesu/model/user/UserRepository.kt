package com.ashim_bari.tildesu.model.user

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw IllegalStateException("User ID cannot be null")
            createUserProfile(userId, email)
            Log.d("UserRepository", "User registered successfully")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Registration failed", e)
            false
        }
    }
    suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            val result = firebaseAuth.fetchSignInMethodsForEmail(email).await()
            result.signInMethods?.isNotEmpty() ?: false
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    // Handle invalid email format
                    Log.e("Auth", "Invalid email format", e)
                }
                is FirebaseAuthInvalidUserException -> {
                    // Handle user not found
                    Log.e("Auth", "User not found", e)
                }
                is FirebaseNetworkException -> {
                    // Handle network errors
                    Log.e("Auth", "Network error", e)
                }
                else -> {
                    // Handle other errors
                    Log.e("Auth", "Unknown error", e)
                }
            }
            false
        }
    }



    private suspend fun createUserProfile(userId: String, email: String) {
        val user = mapOf(
            "email" to email,
            "name" to "",
            "surname" to "",
            "city" to "",
            "age" to "",
            "gender" to 0,
            "specialty" to ""
        )
        firestore.collection("users").document(userId).set(user).await()
        Log.d("UserRepository", "User profile created successfully")
    }
    suspend fun updateUserProfile(userId: String, userProfile: UserProfile) {
        firestore.collection("users").document(userId).set(userProfile, SetOptions.merge()).await()
        Log.d("UserRepository", "User profile updated successfully")
    }

    fun getUserProfile(onComplete: (UserProfile?) -> Unit) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
                val userProfile = document.toObject(UserProfile::class.java)
                onComplete(userProfile)
            }.addOnFailureListener { exception ->
                Log.e("UserRepository", "Error getting user profile", exception)
                onComplete(null)
            }
        } else {
            onComplete(null)
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.d("UserRepository", "User logged in successfully")
            true // Return true for successful login
        } catch (e: Exception) {
            Log.e("UserRepository", "Login failed", e)
            false // Return false if login fails
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d("UserRepository", "Password reset email sent successfully")
            true // Return true for successful password reset
        } catch (e: Exception) {
            Log.e("UserRepository", "Password reset failed", e)
            false // Return false if password reset fails
        }
    }

    suspend fun logout(): Boolean {
        return try {
            firebaseAuth.signOut()
            Log.d("UserRepository", "User logged out successfully")
            true // Return true for successful log out
        } catch (e: Exception) {
            Log.e("UserRepository", "Logout failed", e)
            false // Return false if log out fails
        }
    }



    suspend fun updatePassword(newPassword: String, currentPassword: String? = null): Boolean {
        return try {
            firebaseAuth.currentUser?.let { user ->
                // If currentPassword is provided, attempt re-authentication
                if (currentPassword != null) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                    user.reauthenticate(credential).await()
                }
                // Attempt to update the password after re-authentication
                user.updatePassword(newPassword).await()
                Log.d("UserRepository", "Password updated successfully")
                true
            } ?: false // Return false if currentUser is null
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Log.e("UserRepository", "Re-authentication required", e)
            false
        } catch (e: Exception) {
            Log.e("UserRepository", "Password update failed", e)
            false
        }
    }


    private val storageReference = Firebase.storage.reference

    suspend fun uploadUserImage(uri: Uri): String? {
        val userId = firebaseAuth.currentUser?.uid ?: return null
        val imageRef = storageReference.child("profileImages/$userId/profilePic.jpg")

        try {
            imageRef.putFile(uri).await()
            val imageUrl = imageRef.downloadUrl.await().toString()
            Log.d("UserRepository", "User image uploaded successfully")
            return imageUrl
        } catch (e: Exception) {
            Log.e("UserRepository", "User image upload failed", e)
            e.printStackTrace()
            return null
        }
    }

    suspend fun getUserImage(): String? {
        val userId = firebaseAuth.currentUser?.uid ?: return null
        val imageRef = storageReference.child("profileImages/$userId/profilePic.jpg")

        return try {
            val imageUrl = imageRef.downloadUrl.await().toString()
            Log.d("UserRepository", "User image fetched successfully")
            imageUrl
        } catch (e: Exception) {
            Log.e("UserRepository", "User image fetch failed", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserProgress(userId: String): Map<String, UserProgress> {
        val userProgressData = mutableMapOf<String, UserProgress>()
        val userProgressCollection = firestore.collection("users").document(userId).collection("progress")
        val querySnapshot = userProgressCollection.get().await()

        for (document in querySnapshot.documents) {
            val levelId = document.id
            val scores = document.data?.get("scores") as? Map<String, Map<String, Any>> ?: continue
            val overallScore = document.data?.get("overallScore") as? Map<String, Any> ?: continue
            val overallCorrectAnswers = (overallScore["correctAnswers"] as? Number)?.toFloat() ?: continue
            val overallTotalQuestions = (overallScore["totalQuestions"] as? Number)?.toFloat() ?: continue
            val overallProgress = if (overallTotalQuestions > 0) overallCorrectAnswers / overallTotalQuestions else 0f

            val exerciseTypeProgress = scores.mapNotNull {
                val typeName = it.key
                val details = it.value
                val correctAnswers = (details["correctAnswers"] as? Number)?.toFloat()
                val totalQuestions = (details["totalQuestions"] as? Number)?.toFloat()
                if (correctAnswers != null && totalQuestions != null && totalQuestions > 0) {
                    typeName to correctAnswers / totalQuestions
                } else {
                    null
                }
            }.toMap()

            userProgressData[levelId] = UserProgress(overallProgress, exerciseTypeProgress)
        }
        //Log.d("UserRepository", "User progress fetched successfully for $userId")
        return userProgressData
    }


    data class UserProgress(
        val overallProgress: Float,
        val exerciseTypeProgress: Map<String, Float>
    )

}

