package com.ashim_bari.tildesu.model.usefultips

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UsefulTipsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getUsefulTips(): Flow<List<UsefulTip>> = callbackFlow {
        val listenerRegistration = firestore.collection("usefulTips")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val tips = snapshots?.documents?.mapNotNull { it.toObject(UsefulTip::class.java) } ?: emptyList()
                trySend(tips).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }
}
