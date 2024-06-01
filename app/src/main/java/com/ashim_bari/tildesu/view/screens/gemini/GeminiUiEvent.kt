package com.ashim_bari.tildesu.view.screens.gemini

import android.graphics.Bitmap


sealed class GeminiUiEvent {
    data class UpdatePrompt(val newPrompt: String) : GeminiUiEvent()
    data class SendPrompt(
        val prompt: String,
        val bitmap: Bitmap?
    ) : GeminiUiEvent()
}
