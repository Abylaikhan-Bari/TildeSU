package com.ashim_bari.tildesu.model.exercise


enum class ExerciseType {
    QUIZ, PUZZLES, TRUE_FALSE
}
data class Exercise(
    var id: String = "",
    val level: String = "",
    val type: ExerciseType = ExerciseType.QUIZ,
    val question: String? = null,
    val options: List<String>? = listOf(),
    val correctOptionIndex: Int? = null,
    var userSelectedOption: Int? = null,
    val statement: String? = null,
    val isTrue: Boolean? = null,
    val sentenceParts: List<String>? = null, // Only for puzzles
    val correctOrder: List<Int>? = null // Only for puzzles, but consider changing to List<String>
)

