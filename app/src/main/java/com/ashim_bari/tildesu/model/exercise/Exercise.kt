package com.ashim_bari.tildesu.model.exercise

data class Exercise(
    var id: String = "",
    val level: String = "",
    val question: String = "",
    val options: List<String> = listOf(),
    val correctOptionIndex: Int = -1, // Ensure this matches Firestore field
    var userSelectedOption: Int = -1 // To track user's selection
)

