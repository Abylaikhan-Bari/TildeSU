package com.ashim_bari.tildesu.model.lesson

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LessonsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getLessonsForLevel(level: String): Flow<List<Lesson>> = callbackFlow {
        val listenerRegistration = firestore.collection("levels")
            .document(level)
            .collection("lessons")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("LessonsRepository", "Error fetching lessons for level $level", error)
                    close(error)
                    return@addSnapshotListener
                }
                val lessons = value?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject<Lesson>().also { lesson ->
                            Log.d("LessonsRepository", "Fetched lesson: $lesson")
                        }
                    } catch (e: Exception) {
                        Log.e("LessonsRepository", "Error converting document to Lesson", e)
                        null
                    }
                }
                lessons?.let {
                    Log.d("LessonsRepository", "Emitting ${lessons.size} lessons")
                    trySend(it).isSuccess
                }
            }

        awaitClose {
            Log.d("LessonsRepository", "Closing the lessons listener for level $level")
            listenerRegistration.remove()
        }
    }

    fun getLessonById(level: String, lessonId: String): Flow<Lesson> = callbackFlow {
        val docRef = firestore.collection("levels").document(level).collection("lessons").document(lessonId)
        val subscription = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("LessonsRepository", "Error fetching lesson $lessonId", e)
                close(e)
            } else if (snapshot != null && snapshot.exists()) {
                try {
                    val lesson = snapshot.toObject<Lesson>().also { lesson ->
                        Log.d("LessonsRepository", "Fetched specific lesson: $lesson")
                    }
                    lesson?.let { trySend(it).isSuccess }
                } catch (e: Exception) {
                    Log.e("LessonsRepository", "Error converting document to specific Lesson", e)
                    close(e)
                }
            } else {
                Log.w("LessonsRepository", "No such lesson found: $lessonId")
                close(Throwable("No such lesson found"))
            }
        }
        awaitClose {
            Log.d("LessonsRepository", "Closing the specific lesson listener for lesson $lessonId")
            subscription.remove()
        }
    }
}