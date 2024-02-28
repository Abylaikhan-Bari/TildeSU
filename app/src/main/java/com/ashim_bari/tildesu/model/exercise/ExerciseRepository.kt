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
    suspend fun getExercisesByLevelAndType(level: String, type: ExerciseType): List<Exercise> {
        val exercises = db.collection("levels")
            .document(level)
            .collection(type.name.toLowerCase())
            .get()
            .await()
            .toObjects(Exercise::class.java)
        Log.d("ExerciseRepository", "Fetched ${exercises.size} exercises for level $level and type $type")
        return exercises
    }

    @OptIn(UnstableApi::class)
    suspend fun updateUserProgress(userId: String, level: String, score: Int, totalCorrectAnswers: Int, totalQuestions: Int) {
        val userProgress = mapOf(
            "score" to score,
            "totalCorrectAnswers" to totalCorrectAnswers,
            "totalQuestions" to totalQuestions, // Store total questions attempted
            "completedOn" to FieldValue.serverTimestamp()
        )
        db.collection("users")
            .document(userId)
            .collection("progress")
            .document(level)
            .set(userProgress)
            .await()
        Log.d("ExerciseRepository", "User progress updated for user $userId, level $level")
    }
}


