package com.ashim_bari.tildesu.model.exercise

data class Exercise(
    var id: String = "", // Unique identifier for the exercise
    val questionText: String = "", // The text of the question
    val options: List<String> = emptyList(), // The list of answer options
    val correctOptionIndex: Int = -1 // Index of the correct option in the options list
)
