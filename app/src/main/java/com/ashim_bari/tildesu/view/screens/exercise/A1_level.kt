package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.ExerciseViewModelFactory

@Composable
fun A1_Level(navController: NavHostController, exerciseViewModelFactory: ExerciseViewModelFactory) {
    // Obtain the ExerciseViewModel from the factory
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Call loadExercises once when the composable enters the composition
    LaunchedEffect(key1 = "loadExercises") {
        exerciseViewModel.loadExercisesForLevel("A1")
    }

    // Observe the exercises LiveData from the ViewModel
    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            // TopAppBar code
            // ...
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Iterate over each exercise and display its content
            exercises.value.forEach { exercise ->
                Text(
                    text = exercise.question,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                exercise.options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {

                        Text(
                            text = "Option ${index + 1}: $option",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Divider(Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
