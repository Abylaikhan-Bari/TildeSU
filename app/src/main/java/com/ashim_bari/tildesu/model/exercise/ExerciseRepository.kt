package com.ashim_bari.tildesu.model.exercise

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ExerciseRepository {
    //private val db = FirebaseFirestore.getInstance()

    @OptIn(UnstableApi::class)
    private val db = Firebase.firestore

    @OptIn(UnstableApi::class)
    suspend fun getExercisesByLevelAndType(level: String, type: ExerciseType): List<Exercise> {
        val collectionName = when (type) {
            ExerciseType.QUIZ -> "quizzes"
            ExerciseType.PUZZLES -> "puzzles"
            ExerciseType.TRUE_FALSE -> "trueOrFalse"
        }

        val exercisesSnapshot = db.collection("levels")
            .document(level)
            .collection(collectionName)
            .get()
            .await()

        return exercisesSnapshot.documents.mapNotNull { documentSnapshot ->
            val exercise = documentSnapshot.toObject<Exercise>() ?: return@mapNotNull null
            when (type) {
                ExerciseType.PUZZLES -> {
                    // Convert the correctOrder from List<Long> to List<Int>
                    val correctOrderLongs = documentSnapshot.get("correctOrder") as? List<Long>
                    val correctOrderInts = correctOrderLongs?.map { it.toInt() }
                    exercise.copy(correctOrder = correctOrderInts)
                }
                else -> exercise
            }
        }.also {
            Log.d("ExerciseRepository", "Fetched ${it.size} exercises for level $level and type ${type.name}")
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun updateUserProgress(userId: String, levelId: String, exerciseType: ExerciseType, correctAnswers: Int, totalQuestions: Int) {
        val progressRef = db.collection("users")
            .document(userId)
            .collection("progress")
            .document(levelId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(progressRef)
            if (!snapshot.exists()) {
                // Initialize scores map with all exercise types to ensure structure consistency
                val initialScoresMap = ExerciseType.values().associate { exerciseType ->
                    exerciseType.name.toLowerCase() to mapOf("correctAnswers" to 0L, "totalQuestions" to 0L)
                }

                // Document does not exist, create it with initial values
                transaction.set(progressRef, mapOf(
                    "scores" to initialScoresMap,
                    "overallScore" to mapOf("correctAnswers" to 0L, "totalQuestions" to 0L),
                    "completedOn" to FieldValue.serverTimestamp() // Consider if initial creation needs this
                ))
            } else {
                // Document exists, proceed with updating
                val scoresMap = snapshot.get("scores") as? Map<String, Map<String, Any>> ?: emptyMap()

                val currentTypeScores = scoresMap[exerciseType.name.toLowerCase()] ?: mapOf("correctAnswers" to 0L, "totalQuestions" to 0L)
                val updatedTypeCorrectAnswers = (currentTypeScores["correctAnswers"] as? Number ?: 0L).toLong() + correctAnswers
                val updatedTypeTotalQuestions = (currentTypeScores["totalQuestions"] as? Number ?: 0L).toLong() + totalQuestions

                val updatedScoresMap = scoresMap.toMutableMap()
                updatedScoresMap[exerciseType.name.toLowerCase()] = mapOf(
                    "correctAnswers" to updatedTypeCorrectAnswers,
                    "totalQuestions" to updatedTypeTotalQuestions
                )

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
            }
        }.await()
        Log.d("ExerciseRepository", "User progress updated for user: $userId, level: $levelId, type: ${exerciseType.name}")
    }



}


