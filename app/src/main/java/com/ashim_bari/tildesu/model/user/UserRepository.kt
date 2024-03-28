package com.ashim_bari.tildesu.model.user

import android.net.Uri
import android.util.Log
import com.ashim_bari.tildesu.db.dao.UserDao
import com.ashim_bari.tildesu.utils.Mapper.Companion.toUserEntity
import com.ashim_bari.tildesu.utils.Mapper.Companion.toUserProfile
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) {
//    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw IllegalStateException("User ID cannot be null")
            val newUserProfile = UserProfile(email = email) // Initialize with minimum required data
            createUserProfile(userId, newUserProfile)
            // Insert into the Room database
            val newUserEntity = newUserProfile.toUserEntity(userId)
            userDao.insertUser(newUserEntity)
            Log.d("UserRepository", "User registered and saved locally successfully")
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



    private suspend fun createUserProfile(userId: String, userProfile: UserProfile) {
        val userMap = userProfile.let {
            mapOf(
                "email" to it.email,
                "name" to (it.name ?: ""), // Default to an empty string if null
                "surname" to (it.surname ?: ""),
                "city" to (it.city ?: ""),
                "age" to (it.age ?: ""),
                "gender" to (it.gender ?: 0), // Default to '0' if null
                "specialty" to (it.specialty ?: "")
            )
        }
        firestore.collection("users").document(userId).set(userMap).await()
        Log.d("UserRepository", "User profile created successfully in Firestore")
    }
    suspend fun updateUserProfile(userId: String, userProfile: UserProfile) {
        // Update Firebase
        firestore.collection("users").document(userId).set(userProfile, SetOptions.merge()).await()

        // Update local DB
        val userEntity = userProfile.toUserEntity(userId)
        userDao.updateUserProfile(userEntity)

        // Log for debugging
        Log.d("UserRepository", "User profile updated successfully")
    }


    fun getUserProfile(onComplete: (UserProfile?) -> Unit) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            onComplete(null)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // First, try to get the user profile from the local database.
                val localUserProfile = userDao.getUserProfile(userId)
                if (localUserProfile != null) {
                    // If found, map it to UserProfile and complete.
                    withContext(Dispatchers.Main) {
                        onComplete(localUserProfile.toUserProfile())
                    }
                    return@launch
                }

                // If not found in the local DB, fetch from Firestore.
                val documentSnapshot = firestore.collection("users").document(userId).get().await()
                val firestoreUserProfile = documentSnapshot.toObject(UserProfile::class.java)
                if (firestoreUserProfile != null) {
                    // Insert the fetched profile into the local database.
                    userDao.insertUser(firestoreUserProfile.toUserEntity(userId))
                    withContext(Dispatchers.Main) {
                        onComplete(firestoreUserProfile)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onComplete(null)
                    }
                }
            } catch (exception: Exception) {
                Log.e("UserRepository", "Error getting user profile", exception)
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
            }
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
            // Log based on the specific error code
            when (e.errorCode) {
                StorageException.ERROR_OBJECT_NOT_FOUND -> {
                    Log.i("UserRepository", "User image does not exist at location: profileImages/$userId/profilePic.jpg")
                }
                else -> Log.e("UserRepository", "User image fetch failed due to a storage exception", e)
            }
            null // Return null for any StorageException
        } catch (e: Exception) {
            // Handle any other unexpected exceptions
            Log.e("UserRepository", "Unexpected error fetching user image", e)
            null // Return null for any other exceptions
        }
    }




    data class UserProgress(
        val overallProgress: Float,
        val puzzleProgress: Float,
        val quizProgress: Float,
        val trueFalseProgress: Float,
        val imageQuizProgress: Float

    )

    suspend fun getUserProgress(userId: String): Map<String, UserProgress> {
        val userProgressData = mutableMapOf<String, UserProgress>()
        val userProgressCollection = firestore.collection("users").document(userId).collection("progress")
        val querySnapshot = userProgressCollection.get().await()

        for (document in querySnapshot.documents) {
            val levelId = document.id

            // Firestore stores numbers as Long or Double, so you need to cast them accordingly
            val overallCorrect = (document.getLong("overallCorrect") ?: 0).toFloat()
            val overallTotal = (document.getLong("overallTotal") ?: 0).toFloat()
            val puzzleCorrect = (document.getLong("puzzleCorrect") ?: 0).toFloat()
            val puzzleTotal = (document.getLong("puzzleTotal") ?: 0).toFloat()
            val quizCorrect = (document.getLong("quizCorrect") ?: 0).toFloat()
            val quizTotal = (document.getLong("quizTotal") ?: 0).toFloat()
            val trueFalseCorrect = (document.getLong("trueFalseCorrect") ?: 0).toFloat()
            val trueFalseTotal = (document.getLong("trueFalseTotal") ?: 0).toFloat()
            val imageQuizCorrect = (document.getLong("imageQuizCorrect") ?: 0).toFloat()
            val imageQuizTotal = (document.getLong("imageQuizTotal") ?: 0).toFloat()

            // Calculate progress as a float ratio
            val overallProgress = if (overallTotal > 0) overallCorrect / overallTotal else 0f
            val puzzleProgress = if (puzzleTotal > 0) puzzleCorrect / puzzleTotal else 0f
            val quizProgress = if (quizTotal > 0) quizCorrect / quizTotal else 0f
            val trueFalseProgress = if (trueFalseTotal > 0) trueFalseCorrect / trueFalseTotal else 0f
            val imageQuizProgress = if (imageQuizTotal > 0) imageQuizCorrect / imageQuizTotal else 0f
            // Create UserProgress instance without completedOn
            userProgressData[levelId] = UserProgress(overallProgress, puzzleProgress, quizProgress, trueFalseProgress, imageQuizProgress)
        }

        return userProgressData
    }




}

