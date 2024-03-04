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
import com.google.firebase.storage.StorageException
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
        // Get the current user ID, return null if not signed in
        val userId = firebaseAuth.currentUser?.uid ?: return null

        // Construct the reference to the image in Firebase Storage
        val imageRef = storageReference.child("profileImages/$userId/profilePic.jpg")

        // Attempt to get the download URL of the image
        return try {
            val imageUrl = imageRef.downloadUrl.await().toString()
            Log.d("UserRepository", "User image fetched successfully: $imageUrl")
            imageUrl // Return the image URL if successful
        } catch (e: StorageException) {
            if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                // If the image does not exist, log the information and return null
                Log.i("UserRepository", "User image does not exist at location: profileImages/$userId/profilePic.jpg")
                null
            } else {
                // If another error occurred, log the exception and return null
                Log.e("UserRepository", "User image fetch failed", e)
                null
            }
        } catch (e: Exception) {
            // Catch any other exceptions that might occur and return null
            Log.e("UserRepository", "Unexpected error fetching user image", e)
            null
        }
    }



    suspend fun getUserProgress(userId: String): Map<String, UserProgress> {
        val userProgressData = mutableMapOf<String, UserProgress>()
        val userProgressCollection = firestore.collection("users").document(userId).collection("progress")
        val querySnapshot = userProgressCollection.get().await()

        for (document in querySnapshot.documents) {
            val levelId = document.id
            val scores = document["scores"] as? Map<String, Map<String, Number>> ?: continue
            val overallScore = document["overallScore"] as? Map<String, Number> ?: continue

            // Extract values and calculate overall progress
            val overallCorrectAnswers = overallScore["correctAnswers"]?.toFloat() ?: 0f
            val overallTotalQuestions = overallScore["totalQuestions"]?.toFloat() ?: 0f
            val overallProgress = if (overallTotalQuestions > 0) {
                overallCorrectAnswers / overallTotalQuestions
            } else {
                0f
            }

            // Calculate progress for each exercise type
            val exerciseTypeProgress = scores.mapValues { (_, typeScores) ->
                val correct = typeScores["correctAnswers"]?.toFloat() ?: 0f
                val total = typeScores["totalQuestions"]?.toFloat() ?: 0f
                if (total > 0) correct / total else 0f
            }

            // Create UserProgress instance
            userProgressData[levelId] = UserProgress(overallProgress, exerciseTypeProgress)
        }

        return userProgressData
    }







    data class UserProgress(
        val overallProgress: Float,
        val exerciseTypeProgress: Map<String, Float>
    )

}

