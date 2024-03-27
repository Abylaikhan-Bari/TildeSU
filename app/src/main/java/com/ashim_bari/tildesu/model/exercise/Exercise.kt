package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

enum class ExerciseType {
    QUIZ, PUZZLES, TRUE_FALSE, DICTIONARY_CARDS, IMAGE_QUIZZES
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
    var correctOrder: List<Int>? = null, // Specific to PUZZLES

    // Dictionary Card specific properties
    var wordEnglish: String? = null,
    var wordKazakh: String? = null,
    var wordRussian: String? = null,
    // Image Quiz specific properties
    var imageUrl: String? = null,
    var imageOptions: List<String>? = null,
    var imageQuestion: String? = null,
    var correctImageOptionIndex: Int? = null

)


