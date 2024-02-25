package com.ashim_bari.tildesu.view.screens.exercise

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    navController: NavController,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    level: String,
    navBackStackEntry: NavBackStackEntry? = null
) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    val currentLevel by rememberSaveable { mutableStateOf(level) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = level) {
        Log.d("ExerciseScreen", "LaunchedEffect triggered for level: $level")
        exerciseViewModel.loadExercisesForLevel(level)
    }

    val exercises = exerciseViewModel.exercises.observeAsState(initial = emptyList()).value
    val currentQuestionIndex = exerciseViewModel.currentQuestionIndex.observeAsState().value ?: 0
    var selectedOption by rememberSaveable { mutableIntStateOf(-1) }
    val quizCompleted = exerciseViewModel.quizCompleted.observeAsState().value ?: false
    val quizPassed = exerciseViewModel.quizPassed.observeAsState()
    fun showConfirmationDialog() {
        showDialog = true
    }

    Log.d("ExerciseScreen", "Composing ExerciseScreen, Current Level: $currentLevel, Current Question Index: $currentQuestionIndex, Quiz Completed: $quizCompleted")
    if (quizCompleted) {
        quizPassed.value?.let { passed ->
            if (passed) {
                SuccessScreen(navController, exerciseViewModel.score.value ?: 0)
                return@ExerciseScreen
            } else {
                FailureScreen(navController) {
                    // Implement what should happen when retrying the quiz, e.g., resetting quiz state
                    exerciseViewModel.resetQuiz()
                    // Navigate as needed or reset UI state
                }
                return@ExerciseScreen
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(
                        R.string.exercise_level,
                        currentLevel
                    ))  },
                navigationIcon = {
                    if (!quizCompleted) {
                        IconButton(
                            onClick = { showConfirmationDialog() }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        BackHandler {
            showConfirmationDialog()
            Log.d("ExerciseScreen", "BackHandler triggered")
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            //.verticalScroll(rememberScrollState()) // Add vertical scroll modifier here
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center // Center the content vertically
            ) {
                // Log action: Checking quiz completion
                Log.d("ExerciseScreen", "Checking quiz completion")

                if (quizCompleted) {
                    // Log action: Quiz completed
                    Log.d("ExerciseScreen", "Quiz completed")

                    Text(stringResource(id = R.string.exercise_completed), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
                    Card(
                        onClick = { navController.navigate("main") },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                            .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
                            .height(100.dp), // Set the height to a specific value
                        shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(id = R.string.go_home_card), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
                    }
                    Text(
                        text = stringResource(
                            R.string.your_score,
                            exerciseViewModel.score.observeAsState().value ?: 0
                        ), modifier = Modifier.align(
                        Alignment.CenterHorizontally))
                } else if (exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
                    val currentExercise = exercises[currentQuestionIndex]
                    Text(text = currentExercise.question, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))

                    // Use a LazyVerticalGrid to create a grid layout for the option cards
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // We want 2 columns
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentExercise.options.size) { index ->
                            OptionCard(
                                option = currentExercise.options[index],
                                isSelected = selectedOption == index,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                selectedOption = index
                            }
                        }
                    }


                    Button(
                        onClick = {
                            exerciseViewModel.submitAnswer(selectedOption)
                            exerciseViewModel.moveToNextQuestion()
                            selectedOption = -1 // Reset for next question
                        },
                        modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally), // Align the button to the center horizontally
                        enabled = selectedOption != -1
                    ) {
                        Text(stringResource(id = R.string.next_button))
                    }
                } else {
                    Text(
                        text = stringResource(
                            R.string.no_exercises_found,
                            currentLevel
                        ), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(
                        Alignment.CenterHorizontally)) // Use currentLevel here
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(id = R.string.exit_exercise_dialog_title)) },
            text = { Text(stringResource(id = R.string.exit_exercise_dialog_content)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("main")
                    }
                ) {
                    Text(stringResource(id = R.string.exit_dialog_yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text(stringResource(id = R.string.exit_dialog_no))
                }
            }
        )
    }
}
@Composable
fun SuccessScreen(navController: NavController, score: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Congratulations!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Icon(
            imageVector = Icons.Filled.EmojiEvents, // This is a built-in trophy-like icon
            contentDescription = "Trophy",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )

        Text(
            text = "You scored $score points!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = { /* TODO: Implement share functionality */ },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Share")
        }
        Button(
            onClick = { navController.navigate("main") }
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
fun FailureScreen(navController: NavController, restartQuiz: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Oops! Sorry",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Icon(
            imageVector = Icons.Filled.SentimentDissatisfied,
            contentDescription = "Sad Face",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )

        Text(
            text = "Don't worry, you can try again!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = { restartQuiz() },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Try Again")
        }
        Button(
            onClick = { navController.navigate("main") }
        ) {
            Text("Back to Home")
        }
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
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
        )
    }
}
