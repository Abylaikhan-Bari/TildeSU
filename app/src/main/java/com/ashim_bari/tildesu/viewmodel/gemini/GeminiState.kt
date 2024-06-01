package com.ashim_bari.tildesu.viewmodel.gemini

import android.graphics.Bitmap
import com.ashim_bari.tildesu.model.chat.Chat

data class GeminiState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)