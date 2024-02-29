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
        // Convert the ExerciseType to the correct collection name, ensuring the case matches.
        val collectionName = when (type) {
            ExerciseType.QUIZ -> "quizzes"
            ExerciseType.PUZZLES -> "puzzles"
            ExerciseType.TRUE_FALSE -> "trueOrFalse"
        }

        // Fetch the exercises from the correct sub-collection under the level document.
        val exercises = db.collection("levels")
            .document(level) // Make sure this matches the document ID in Firestore exactly.
            .collection(collectionName) // Use the correct case for collection names.
            .get()
            .await()
            .toObjects(Exercise::class.java)

        Log.d("ExerciseRepository", "Fetched ${exercises.size} exercises for level $level and type ${type.name}")
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


