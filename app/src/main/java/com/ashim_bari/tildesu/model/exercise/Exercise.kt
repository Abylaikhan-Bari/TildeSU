package com.ashim_bari.tildesu.model.exercise

data class Exercise(
    var id: String = "", // Unique identifier for the exercise
    val level: String = "", // The level of the exercise, e.g., "A1", "A2", etc.
    val question: String = "", // The text of the question
    val options: List<String> = listOf(), // The list of answer options
    val correctOption: Int = -1 // The index of the correct answer in the options list
)
