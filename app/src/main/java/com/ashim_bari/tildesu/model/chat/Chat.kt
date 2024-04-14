package com.ashim_bari.tildesu.model.chat

import android.graphics.Bitmap

data class Chat(
    val prompt: String,
    val bitmap: Bitmap?,
    val isFromUser: Boolean
)