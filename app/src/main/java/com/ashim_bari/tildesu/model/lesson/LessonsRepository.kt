package com.ashim_bari.tildesu.model.lesson

import com.google.firebase.firestore.FirebaseFirestore
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
                    close(error)
                    return@addSnapshotListener
                }
                val lessons = value?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Lesson::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                lessons?.let { trySend(it).isSuccess }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    fun getLessonById(level: String, lessonId: String): Flow<Lesson> {
        return callbackFlow {
            val docRef = firestore.collection("levels").document(level).collection("lessons").document(lessonId)
            val subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                } else if (snapshot != null && snapshot.exists()) {
                    val lesson = snapshot.toObject(Lesson::class.java)
                    lesson?.let { trySend(it).isSuccess }
                } else {
                    close(Throwable("No such lesson found"))
                }
            }
            awaitClose { subscription.remove() }
        }
    }

}
