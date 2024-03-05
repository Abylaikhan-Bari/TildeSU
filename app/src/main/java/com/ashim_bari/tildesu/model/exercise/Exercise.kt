package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

enum class ExerciseType {
    QUIZ, PUZZLES, TRUE_FALSE
}

data class Exercise(
    @Exclude @set:Exclude @get:Exclude
    var id: String = "", // Exclude id from Firestore serialization/deserialization
    var level: String = "",
    var type: ExerciseType? = null, // Make nullable to handle setting post-instantiation
    var question: String? = null, // Make nullable if not all types have questions
    var options: List<String>? = emptyList(), // Specific to QUIZ, nullable if not applicable
    var correctOptionIndex: Int? = null, // Specific to QUIZ
    @Exclude @set:Exclude @get:Exclude
    var userSelectedOptionIndex: Int? = null, // User's selection, not stored in Firestore
    val statement: String? = null, // Consider merging with question if they serve the same purpose
    @get:PropertyName("isTrue") @set:PropertyName("isTrue")
    var isTrue: Boolean? = null, // Specific to TRUE_FALSE, make sure Firestore property matches
    var sentenceParts: List<String>? = null, // Specific to PUZZLES
    var correctOrder: List<Int>? = null // Specific to PUZZLES
) {
    // Optional: Additional logic or helper methods can be added here
}
