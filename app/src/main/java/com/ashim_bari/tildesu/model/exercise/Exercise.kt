package com.ashim_bari.tildesu.model.exercise

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

enum class ExerciseType {
    QUIZ, PUZZLES, TRUE_FALSE, DICTIONARY_CARDS, IMAGE_QUIZZES
}
data class Exercise(
    @Exclude @set:Exclude @get:Exclude
    var id: String = "",
    var level: String = "",
    var type: ExerciseType? = null,
    var question: String? = null,
    var options: List<String>? = emptyList(),
    var correctOptionIndex: Int? = null,
    @Exclude @set:Exclude @get:Exclude
    var userSelectedOptionIndex: Int? = null,
    val statement: String? = null,
    @get:PropertyName("isTrue") @set:PropertyName("isTrue")
    var isTrue: Boolean? = null,
    var sentenceParts: List<String>? = null,
    var correctOrder: List<Int>? = null,
    var wordEnglish: String? = null,
    var wordKazakh: String? = null,
    var wordRussian: String? = null,
    var imageUrl: String? = null,
    var imageOptions: List<String>? = null,
    var imageQuestion: String? = null,
    var correctImageOptionIndex: Int? = null
)