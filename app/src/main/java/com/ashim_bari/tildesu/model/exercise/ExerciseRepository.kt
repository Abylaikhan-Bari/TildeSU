package com.ashim_bari.tildesu.model.exercise

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExerciseRepository {
    private val db = FirebaseFirestore.getInstance()

    @OptIn(UnstableApi::class)
    suspend fun getExercisesByLevel(level: String): List<Exercise> {
        val exercises = db.collection("levels").document(level).collection("exercises")
            .get()
            .await()
            .toObjects(Exercise::class.java)
        Log.d("ExerciseRepository", "Fetched ${exercises.size} exercises for level $level")
        return exercises
    }

    @OptIn(UnstableApi::class)
    suspend fun updateUserProgress(userId: String, level: String, score: Int, email: String, totalCorrectAnswers: Int) {
        val userProgress = mapOf(
            "score" to score,
            "email" to email,
            "totalCorrectAnswers" to totalCorrectAnswers,
            "completedOn" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(userId).collection("progress").document(level)
            .set(userProgress)
            .await()
        Log.d("ExerciseRepository", "User progress updated for user $userId, level $level")
    }

}




