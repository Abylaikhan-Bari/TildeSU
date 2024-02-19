package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExerciseRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getExercisesByLevel(level: String): List<Exercise> {
        return db.collection("levels").document(level).collection("exercises")
            .get()
            .await()
            .toObjects(Exercise::class.java)
    }

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
    }

}




