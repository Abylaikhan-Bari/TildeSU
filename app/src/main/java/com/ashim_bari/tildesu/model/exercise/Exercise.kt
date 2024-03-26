package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

enum class ExerciseType {
    QUIZ, PUZZLES, TRUE_FALSE, IMAGE_QUIZ, DICTIONARY_CARD
}

data class Exercise(
    @Exclude @set:Exclude @get:Exclude
    var id: String = "", // Exclude id from Firestore serialization/deserialization
    var level: String = "",
    var type: ExerciseType? = null, // Nullable to handle setting post-instantiation
    var question: String? = null, // Nullable, used for text-based questions
    var options: List<String>? = emptyList(), // Specific to QUIZ and IMAGE_QUIZ, nullable if not applicable
    var correctOptionIndex: Int? = null, // Specific to QUIZ and IMAGE_QUIZ
    @Exclude @set:Exclude @get:Exclude
    var userSelectedOptionIndex: Int? = null, // User's selection, not stored in Firestore
    val statement: String? = null, // Consider merging with question if they serve the same purpose
    @get:PropertyName("isTrue") @set:PropertyName("isTrue")
    var isTrue: Boolean? = null, // Specific to TRUE_FALSE, Firestore property name must match
    var sentenceParts: List<String>? = null, // Specific to PUZZLES
    var correctOrder: List<Int>? = null, // Specific to PUZZLES
    var imageUrl: String? = null, // URL for the image in IMAGE_QUIZ
    // New fields for DICTIONARY_CARD
    var wordKazakh: String? = null, // The word in Kazakh
    var wordRussian: String? = null, // Translation of the word in Russian
    var wordEnglish: String? = null  // Translation of the word in English
) {
    // Optional: Additional logic or helper methods can be added here
}
