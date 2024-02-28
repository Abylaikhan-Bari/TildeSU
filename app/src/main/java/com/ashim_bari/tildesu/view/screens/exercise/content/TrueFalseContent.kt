package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@Composable
fun TrueFalseContent(level: String, exerciseViewModelFactory: ExerciseViewModelFactory) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Assuming your ViewModel has a function to load true/false exercises for the given level
    LaunchedEffect(level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.TRUE_FALSE)
    }

    val exercises by exerciseViewModel.exercises.observeAsState(initial = emptyList())

    // Display logic for true/false exercises
    if (exercises.isEmpty()) {
        Text("Loading true/false exercises...")
    } else {
        // Displaying the first true/false question as an example
        // Implement navigation between questions and user interactions as required
        val firstQuestion = exercises.first()
        firstQuestion.question?.let { Text(it) }
        Column {
            // Option for True
            Text("True", modifier = Modifier.clickable {
                // Handle true selection
                exerciseViewModel.submitAnswer(true)
            })
            // Option for False
            Text("False", modifier = Modifier.clickable {
                // Handle false selection
                exerciseViewModel.submitAnswer(false)
            })
        }
    }
}
