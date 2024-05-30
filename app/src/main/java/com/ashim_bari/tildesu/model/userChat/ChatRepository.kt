package com.ashim_bari.tildesu.model.userChat

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val chatCollection = db.collection("chats")

    suspend fun sendMessage(chat: userChat) {
        try {
            chatCollection.add(chat).await()
        } catch (e: Exception) {
            // Handle exception
        }
    }

    fun getChatsForUser(userId: String): com.google.firebase.firestore.CollectionReference {
        return chatCollection
            .whereEqualTo("senderId", userId)
            .orderBy("timestamp") as CollectionReference
    }
}
