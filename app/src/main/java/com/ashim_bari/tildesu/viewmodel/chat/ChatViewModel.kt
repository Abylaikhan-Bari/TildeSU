package com.ashim_bari.tildesu.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.userChat.ChatRepository
import com.ashim_bari.tildesu.model.userChat.userChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val chatRepository = ChatRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _chats = MutableLiveData<List<userChat>>()
    val chats: LiveData<List<userChat>> get() = _chats

    private var chatListenerRegistration: ListenerRegistration? = null

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""
    val currentUserEmail: String
        get() = auth.currentUser?.email ?: ""

    fun sendMessage(chat: userChat) {
        viewModelScope.launch {
            chatRepository.sendMessage(chat)
        }
    }

    fun loadChats() {
        chatListenerRegistration?.remove() // Remove previous listener if any
        chatListenerRegistration = chatRepository.getUserChats(currentUserId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle exception
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val chatList = snapshot.get("messages") as? List<Map<String, Any>>
                    _chats.value = chatList?.map { map ->
                        userChat(
                            senderId = map["senderId"] as? String ?: "",
                            senderEmail = map["senderEmail"] as? String ?: "",
                            receiverId = map["receiverId"] as? String ?: "",
                            message = map["message"] as? String ?: "",
                            timestamp = map["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now()
                        )
                    }?.sortedBy { it.timestamp } ?: emptyList()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        chatListenerRegistration?.remove() // Remove listener when ViewModel is cleared
    }
}
