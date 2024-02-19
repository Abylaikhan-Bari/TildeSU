package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class ExerciseRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getExercisesByLevel(level: String): List<Exercise> {
        return db.collection("levels").document(level).collection("exercises")
            .get()
            .await()
            .toObjects(Exercise::class.java)
    }

    suspend fun updateUserProgress(userId: String, level: String, score: Int) {
        val userProgress = mapOf(
            "score" to score,
            "completedOn" to System.currentTimeMillis()
        )
        db.collection("users").document(userId).collection("progress").document(level)
            .set(userProgress)
            .await()
    }
}




