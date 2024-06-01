package com.ashim_bari.tildesu.model.userChat

import android.graphics.Bitmap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val chatCollection = db.collection("chats")
    private val storage = FirebaseStorage.getInstance()

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

    suspend fun uploadBitmap(bitmap: Bitmap, senderId: String): String {
        val storageRef = storage.reference.child("images/${senderId}_${System.currentTimeMillis()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageRef.putBytes(data).await()
        return storageRef.downloadUrl.await().toString()
    }

    fun getUserChats(userId: String) = chatCollection.document(userId)

    private fun userChat.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "senderId" to senderId,
            "senderEmail" to senderEmail,
            "receiverId" to receiverId,
            "message" to message,
            "timestamp" to timestamp
        )
        imageUrl?.let { map["imageUrl"] = it }
        return map
    }
}
