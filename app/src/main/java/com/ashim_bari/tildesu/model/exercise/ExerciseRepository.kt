package com.ashim_bari.tildesu.model.exercise

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    //private val db = FirebaseFirestore.getInstance()

    @OptIn(UnstableApi::class)
    private val db = Firebase.firestore

    @OptIn(UnstableApi::class)
    suspend fun getExercisesByLevelAndType(level: String, type: ExerciseType): List<Exercise> {
        val collectionName = when (type) {
            ExerciseType.QUIZ -> "quizzes"
            ExerciseType.PUZZLES -> "puzzles"
            ExerciseType.TRUE_FALSE -> "trueOrFalse"
            ExerciseType.DICTIONARY_CARDS -> "dictionaryCards"
            ExerciseType.IMAGE_QUIZZES -> "imageQuizzes"
        }

        return try {
            val exercisesSnapshot = db.collection("levels")
                .document(level)
                .collection(collectionName)
                .get()
                .await()

            exercisesSnapshot.documents.mapNotNull { documentSnapshot ->
                val exercise = documentSnapshot.toObject<Exercise>()?.also {
                    it.type = type // Explicitly set the type from the method parameter if missing
                }

                when (type) {
                    ExerciseType.PUZZLES -> {
                        val correctOrderLongs = documentSnapshot.get("correctOrder") as? List<Long>
                        val correctOrderInts = correctOrderLongs?.map { it.toInt() }
                        exercise?.copy(correctOrder = correctOrderInts)
                    }
                    else -> exercise
                }.also {
                    if (it == null) Log.d("ExerciseRepository", "Failed to convert document to Exercise object: ${documentSnapshot.id}")
                }
            }.also {
                Log.d("ExerciseRepository", "Fetched ${it.size} exercises for level $level and type ${type.name}")
            }
        } catch (e: Exception) {
            Log.e("ExerciseRepository", "Error fetching exercises for level $level and type ${type.name}", e)
            emptyList()
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun fetchUserProgress(userId: String, levelId: String): Map<String, Any> {
        // Placeholder implementation. Adjust according to your Firestore structure.
        return try {
            val document = db.collection("users")
                .document(userId)
                .collection("progress")
                .document(levelId)
                .get()
                .await()

            document.data ?: emptyMap()
        } catch (e: Exception) {
            Log.e("ExerciseRepository", "Error fetching user progress", e)
            emptyMap()
        }
    }


    @OptIn(UnstableApi::class)
    suspend fun updateUserProgress(userId: String, levelId: String, updateData: Map<String, Any>) {
        val progressRef = db.collection("users").document(userId).collection("progress").document(levelId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(progressRef)
            if (!snapshot.exists()) {
                transaction.set(progressRef, updateData)
            } else {
                transaction.update(progressRef, updateData)
            }
        }.await()
    }

}


