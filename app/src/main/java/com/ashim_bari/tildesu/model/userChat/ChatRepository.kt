package com.ashim_bari.tildesu.model.userChat

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val chatCollection = db.collection("chats")

    suspend fun sendMessage(chat: userChat) {
        try {
            // Add the message to the user's document
            val documentReference = chatCollection.document(chat.senderId)
            documentReference.update("messages", com.google.firebase.firestore.FieldValue.arrayUnion(chat.toMap())).await()
        } catch (e: Exception) {
            // If the document doesn't exist, create it
            try {
                val documentReference = chatCollection.document(chat.senderId)
                documentReference.set(mapOf("messages" to listOf(chat.toMap())), SetOptions.merge()).await()
            } catch (innerException: Exception) {
                // Handle exception
            }
        }
    }

    fun getUserChats(userId: String) = chatCollection.document(userId)

    private fun userChat.toMap(): Map<String, Any> {
        return mapOf(
            "senderId" to senderId,
            "senderEmail" to senderEmail,
            "receiverId" to receiverId,
            "message" to message,
            "timestamp" to timestamp
        )
    }
}
