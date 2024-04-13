package com.ashim_bari.tildesu.model.lesson

import com.google.firebase.firestore.DocumentId

data class Lesson(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val content: String = ""
)
