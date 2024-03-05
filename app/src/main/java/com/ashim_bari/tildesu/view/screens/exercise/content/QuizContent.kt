package com.ashim_bari.tildesu.view.screens.exercise.content

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.view.screens.FailureScreen
import com.ashim_bari.tildesu.view.screens.SuccessScreen
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizContent(navController: NavController, level: String, type: ExerciseType, exerciseViewModelFactory: ExerciseViewModelFactory) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)
    val currentLevel by rememberSaveable { mutableStateOf(level) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.QUIZ)
    }

    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList()).value
    val currentQuestionIndex = exerciseViewModel.currentExercisesIndex.observeAsState().value ?: 0
    var selectedOption by rememberSaveable { mutableIntStateOf(-1) }
    val exerciseCompleted = exerciseViewModel.exerciseCompleted.observeAsState().value ?: false
    val quizPassed = exerciseViewModel.quizPassed.observeAsState()

    if (exerciseCompleted) {
        quizPassed.value?.let { passed ->
            if (passed) {
                val score = exerciseViewModel.quizScore.value ?: 0
                if (score > 0) {
                    SuccessScreen(navController, score)
                } else {
                    FailureScreen(navController) {
                        exerciseViewModel.resetExercise()
                    }
                }
            } else {
                FailureScreen(navController) {
                    exerciseViewModel.resetExercise()
                }
            }
        }
    } else {
        Scaffold { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
                        val currentExercise = exercises[currentQuestionIndex]
                        Text(text = currentExercise.question ?: "No question available", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            currentExercise.options?.let {
                                items(it.size) { index ->
                                    OptionCard(
                                        option = currentExercise.options[index] ?: "No option",
                                        isSelected = selectedOption == index,
                                        onSelect = {
                                            selectedOption = index
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedOption >= 0) {
                                    val currentExercise = exercises[currentQuestionIndex]
                                    val correctOptionIndex = currentExercise.correctOptionIndex ?: -1
                                    val isCorrect = selectedOption == correctOptionIndex
                                    exerciseViewModel.submitQuizAnswer(selectedOption)
                                    selectedOption = -1
                                    Log.d("QuizContent", "Answer submitted. Correct: $isCorrect")
                                }
                            },
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            enabled = selectedOption != -1
                        ) {
                            Text("Next")
                        }


                    } else {
                        Text("No questions found for this quiz.")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Exit Quiz?") },
            text = { Text("Are you sure you want to exit the quiz? Your progress will not be saved.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("main") // Adjust as needed for your navigation setup
                }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}






@Composable
fun OptionCard(option: String, isSelected: Boolean, modifier: Modifier = Modifier, onSelect: () -> Unit) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onSelect)
            .aspectRatio(1f), // Optional: Makes the card square-shaped
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = option,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

