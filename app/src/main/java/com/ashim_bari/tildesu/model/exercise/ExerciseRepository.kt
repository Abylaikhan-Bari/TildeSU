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
        val exercisesSnapshot = db.collection("levels")
            .document(level) // Make sure this matches the document ID in Firestore exactly.
            .collection(collectionName) // Use the correct case for collection names.
            .get()
            .await()

        // Map each Exercise document to an Exercise object and convert correctOrder if necessary
        val exercises = exercisesSnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Exercise::class.java)?.let { exercise ->
                if (type == ExerciseType.PUZZLES) {
                    // Assuming correctOrder in Firestore is stored as a comma-separated string
                    val correctOrderList = documentSnapshot.getString("correctOrder")
                        ?.split(",")
                        ?.mapNotNull { it.trim().toIntOrNull() }
                    exercise.copy(correctOrder = correctOrderList)
                } else {
                    exercise
                }
            }
        }

        Log.d("ExerciseRepository", "Fetched ${exercises.size} exercises for level $level and type ${type.name}")
        return exercises
    }






    @OptIn(UnstableApi::class)
    suspend fun updateUserProgress(userId: String, levelId: String, exerciseType: ExerciseType, correctAnswers: Int, totalQuestions: Int) {
        val progressRef = db.collection("users")
            .document(userId)
            .collection("progress")
            .document(levelId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(progressRef)
            val scoresMap = snapshot.get("scores") as? Map<String, Map<String, Any>> ?: emptyMap()

            // Extract the current scores for the exercise type
            val currentTypeScores = scoresMap[exerciseType.name.toLowerCase()] ?: mapOf("correctAnswers" to 0L, "totalQuestions" to 0L)
            val updatedTypeCorrectAnswers = (currentTypeScores["correctAnswers"] as? Number ?: 0L).toLong() + correctAnswers
            val updatedTypeTotalQuestions = (currentTypeScores["totalQuestions"] as? Number ?: 0L).toLong() + totalQuestions

            // Update the specific type scores
            val updatedScoresMap = scoresMap.toMutableMap()
            updatedScoresMap[exerciseType.name.toLowerCase()] = mapOf(
                "correctAnswers" to updatedTypeCorrectAnswers,
                "totalQuestions" to updatedTypeTotalQuestions
            )

            // Update overall score
            val overallScore = snapshot.get("overallScore") as? Map<String, Any> ?: mapOf("correctAnswers" to 0L, "totalQuestions" to 0L)
            val updatedOverallCorrectAnswers = (overallScore["correctAnswers"] as? Number ?: 0L).toLong() + correctAnswers
            val updatedOverallTotalQuestions = (overallScore["totalQuestions"] as? Number ?: 0L).toLong() + totalQuestions

            transaction.update(progressRef, mapOf(
                "scores" to updatedScoresMap,
                "overallScore" to mapOf(
                    "correctAnswers" to updatedOverallCorrectAnswers,
                    "totalQuestions" to updatedOverallTotalQuestions
                ),
                "completedOn" to FieldValue.serverTimestamp()
            ))
        }.await()
        Log.d("ExerciseRepository", "User progress updated for user $userId, level $levelId, type ${exerciseType.name}")
    }


}


