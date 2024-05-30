package com.ashim_bari.tildesu.model.userChat

import com.google.firebase.Timestamp

data class userChat(
    val senderId: String = "",
    val senderEmail: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now()
)