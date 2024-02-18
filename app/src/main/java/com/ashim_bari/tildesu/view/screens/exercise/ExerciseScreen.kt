package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(navController: NavHostController, exerciseViewModelFactory: ExerciseViewModelFactory, level: String) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    LaunchedEffect(key1 = level) {
        exerciseViewModel.loadExercisesForLevel(level)
    }

    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList()).value
    val currentQuestionIndex = exerciseViewModel.currentQuestionIndex.observeAsState().value ?: 0
    val selectedOption = rememberSaveable { mutableIntStateOf(-1) }
    val quizCompleted = exerciseViewModel.quizCompleted.observeAsState().value ?: false

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Level $level") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (quizCompleted) {
                Text("Quiz Completed!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                Button(onClick = { navController.navigate("main") }) {
                    Text("Go to Home Page")
                }
            } else if (exercises != null) {
                if (exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
                    val currentExercise = exercises[currentQuestionIndex]
                    Text(text = currentExercise.question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                    exercises[currentQuestionIndex].options.forEachIndexed { index, option ->
                        Row(Modifier.padding(vertical = 8.dp)) {
                            RadioButton(
                                selected = selectedOption.intValue == index,
                                onClick = { selectedOption.intValue = index }
                            )
                            Text(text = option, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Button(
                        onClick = {
                            exerciseViewModel.submitAnswer(selectedOption.intValue)
                            exerciseViewModel.moveToNextQuestion()
                            selectedOption.intValue = -1 // Reset for next question
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        enabled = selectedOption.intValue != -1
                    ) {
                        Text("Next")
                    }
                } else {
                    Text("No exercises found for $level", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

