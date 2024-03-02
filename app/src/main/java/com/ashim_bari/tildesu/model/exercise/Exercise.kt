package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.PropertyName

enum class ExerciseType {
    QUIZ, PUZZLES, TRUE_FALSE
}

data class Exercise(
    var id: String = "",
    val level: String = "",
    val type: ExerciseType = ExerciseType.QUIZ,
    val question: String = "", // Assuming question is common and required for QUIZ and TRUE_FALSE
    val options: List<String> = emptyList(), // Specific to QUIZ
    val correctOptionIndex: Int? = null, // Specific to QUIZ
    var userSelectedOptionIndex: Int? = null, // User's selection, applicable to QUIZ
    val statement: String? = null, // Consider merging with question if they serve the same purpose
    @get:PropertyName("isTrue")
    @set:PropertyName("isTrue")
    var isTrue: Boolean? = null, // Specific to TRUE_FALSE, nullable to accommodate other types
    val sentenceParts: List<String>? = null, // Specific to PUZZLES
    val correctOrder: List<Int>? = null // Specific to PUZZLES
)
