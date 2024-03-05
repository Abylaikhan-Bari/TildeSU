package com.ashim_bari.tildesu.model.exercise

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.firebase.Firebase
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


