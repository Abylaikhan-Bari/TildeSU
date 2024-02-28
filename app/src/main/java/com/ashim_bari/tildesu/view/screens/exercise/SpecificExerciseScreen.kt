package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.view.screens.exercise.content.PuzzlesContent
import com.ashim_bari.tildesu.view.screens.exercise.content.QuizContent
import com.ashim_bari.tildesu.view.screens.exercise.content.TrueFalseContent
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@Composable
fun SpecificExerciseScreen(
    navController: NavController,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    level: String,
    type: String
) {
    // Convert the type string to ExerciseType enum
    val exerciseType = try {
        ExerciseType.valueOf(type.uppercase())
    } catch (e: IllegalArgumentException) {
        ExerciseType.QUIZ // Default to QUIZ or handle error as appropriate
    }

    // Based on exerciseType, decide which content to show
    when (exerciseType) {
        ExerciseType.QUIZ -> QuizContent(level, exerciseViewModelFactory)
        ExerciseType.PUZZLES -> PuzzlesContent(level, exerciseViewModelFactory)
        ExerciseType.TRUE_FALSE -> TrueFalseContent(level, exerciseViewModelFactory)
    }
}
