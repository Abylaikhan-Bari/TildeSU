package com.ashim_bari.tildesu.model.exercise


enum class ExerciseType {
    QUIZ, PUZZLES, TRUE_FALSE
}
data class Exercise(
    var id: String = "",
    val level: String = "",
    val type: ExerciseType = ExerciseType.QUIZ,
    val question: String? = null, // Common for quizzes and true/false
    val options: List<String>? = null, // Only for quizzes
    val correctOptionIndex: Int? = null, // Only for quizzes
    var userSelectedOption: Int? = null, // User's selection for quizzes
    val statement: String? = null, // Only for true/false
    val isTrue: Boolean? = null, // Only for true/false
    val sentenceParts: List<String>? = null, // Only for puzzles
    val correctOrder: List<Int>? = null // Only for puzzles
)
