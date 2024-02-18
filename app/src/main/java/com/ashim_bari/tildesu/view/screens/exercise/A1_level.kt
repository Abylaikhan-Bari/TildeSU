package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun A1_Level(navController: NavHostController, exerciseViewModelFactory: ExerciseViewModelFactory) {
    // Obtain the ExerciseViewModel from the factory
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Observe the current question index from the ViewModel
    val currentQuestionIndex = exerciseViewModel.currentQuestionIndex.observeAsState()
    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList())
    val selectedOption = remember { mutableStateOf(-1) }

    Scaffold(
        topBar = {
            // Here, the TopAppBar is implemented to display "A1 Level"
            TopAppBar(
                title = { Text("A1 Level") }
            )
        }
    ) { innerPadding ->
        exercises.value?.let { exerciseList ->
            if (exerciseList.isNotEmpty() && currentQuestionIndex.value != null && currentQuestionIndex.value!! < exerciseList.size) {
                val exercise = exerciseList[currentQuestionIndex.value!!]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = exercise.question,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Radio buttons for options
                    exercise.options.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedOption.value = index },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOption.value == index,
                                onClick = { selectedOption.value = index }
                            )
                            Text(
                                text = option,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            exerciseViewModel.submitAnswer(selectedOption.value)
                            exerciseViewModel.moveToNextQuestion()
                            selectedOption.value = -1 // Reset selection for the next question
                        },
                        enabled = selectedOption.value != -1, // Enable the button only if an option is selected
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Next")
                    }
                }
            } else {
                // Show quiz completion or a loading state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Quiz Completed!", fontWeight = FontWeight.Bold)
                    // Optionally, navigate to a results screen or back to the main menu
                }
            }
        }
    }
}
