package com.ashim_bari.tildesu.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashim_bari.tildesu.model.userChat.ChatRepository
import com.ashim_bari.tildesu.model.userChat.userChat
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val chatRepository = ChatRepository()

    private val _chats = MutableLiveData<List<userChat>>()
    val chats: LiveData<List<userChat>> get() = _chats

    private var chatListenerRegistration: ListenerRegistration? = null

    fun sendMessage(chat: userChat) {
        viewModelScope.launch {
            chatRepository.sendMessage(chat)
        }
    }

    fun loadChats(userId: String) {
        chatListenerRegistration?.remove() // Remove previous listener if any
        chatListenerRegistration = chatRepository.getChatsForUser(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle exception
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val chatList = snapshot.toObjects(userChat::class.java)
                    _chats.value = chatList
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        chatListenerRegistration?.remove() // Remove listener when ViewModel is cleared
    }
}
