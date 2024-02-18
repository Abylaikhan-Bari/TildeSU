package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@Composable
fun ExerciseScreen(navController: NavHostController, exerciseViewModelFactory: ExerciseViewModelFactory, level: String) {
    // Instantiate ViewModel
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Load exercises for the given level
    exerciseViewModel.loadExercisesForLevel(level)

    // Observe the exercises LiveData from ViewModel
    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList()).value

    Column(modifier = Modifier.padding(16.dp)) {
        // Display the exercises
        if (exercises.isNullOrEmpty()) {
            Text(text = "No exercises found for $level")
        } else {
            exercises.forEach { exercise ->
                // Display each exercise
                Text(text = "Question: ${exercise.question}")
                // Implement options display and selection logic
            }
        }

        // Add navigation or action buttons as needed
    }
}
