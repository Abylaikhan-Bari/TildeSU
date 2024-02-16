package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class ExerciseRepository {

    private val db = FirebaseFirestore.getInstance()

    // Function to get exercises by level
    suspend fun getExercisesByLevel(level: String): List<Exercise> {
        // Use string template to construct the collection name dynamically
        val exercisesRef = db.collection("${level}_exercises")
        return try {
            exercisesRef.get().await().documents.mapNotNull { it.toObject<Exercise>()?.apply { id = it.id } }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Other functions can be similarly adjusted to include the level parameter when necessary.
}


