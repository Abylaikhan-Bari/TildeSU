package com.ashim_bari.tildesu.viewmodel.chat

import android.graphics.Bitmap
import com.ashim_bari.tildesu.model.chat.Chat

data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)