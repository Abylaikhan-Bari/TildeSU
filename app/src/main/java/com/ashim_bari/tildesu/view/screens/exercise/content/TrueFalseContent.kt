package com.ashim_bari.tildesu.view.screens.exercise.content

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.view.screens.TrueFalseFailureScreen
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel

@Composable
fun TrueFalseContent(
    navController: NavController,
    level: String,
    type: ExerciseType,

) {
    val exerciseViewModel: ExerciseViewModel = hiltViewModel()
    LaunchedEffect(key1 = level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.TRUE_FALSE)
    }
    val exercises by exerciseViewModel.exercises.observeAsState(emptyList())
    var exerciseCompleted by rememberSaveable { mutableStateOf(false) }
    var trueFalseScore by rememberSaveable { mutableStateOf(0) }
    var showFeedback by rememberSaveable { mutableStateOf(false) }
    var isAnswerCorrect by rememberSaveable { mutableStateOf(false) }
    val currentQuestionIndex = exerciseViewModel.currentExercisesIndex.observeAsState(0).value
    // Observe exercise completion state
    val progress = (currentQuestionIndex.toFloat()) / (exercises.size.toFloat())
    val lifecycleOwner = LocalLifecycleOwner.current
    val restartTrueFalseExercise: () -> Unit = {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.TRUE_FALSE)
    }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    fun showConfirmationDialog() {
        showDialog = true
    }
    DisposableEffect(key1 = exerciseViewModel.exerciseCompleted) {
        val observer = Observer<Boolean> { completed ->
            exerciseCompleted = completed
        }
        exerciseViewModel.exerciseCompleted.observe(lifecycleOwner, observer)
        onDispose {
            exerciseViewModel.exerciseCompleted.removeObserver(observer)
        }
    }
    BackHandler {
        showConfirmationDialog()
        Log.d("ExerciseScreen", "BackHandler triggered")
    }
    if (exerciseCompleted) {
        // Check if at least one answer is correct
        val atLeastOneCorrect = trueFalseScore > 0
        if (atLeastOneCorrect) {
            navController.navigate("trueFalseSuccess/$trueFalseScore")
        } else {
            // Show the TrueFalseFailureScreen only when no answer is correct
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TrueFalseFailureScreen(navController = navController, restartTrueFalseExercise = restartTrueFalseExercise)
                Spacer(modifier = Modifier.height(16.dp)) // Add some space between the failure screen and the content above it
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
    Column {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
            val currentExercise = exercises[currentQuestionIndex]


            if (showFeedback) {
                AnswerFeedbackScreen(isAnswerCorrect) {
                    showFeedback = false
                    if (currentQuestionIndex + 1 < exercises.size) {
                        exerciseViewModel.moveToNextTrueFalse()
                    }
                }
            } else {
                TrueFalseQuestion(
                    statement = currentExercise.statement ?: "",
                    isTrue = currentExercise.isTrue ?: false,
                    onAnswer = { userAnswer ->
                        val correct = userAnswer == currentExercise.isTrue
                        if (correct) {
                            trueFalseScore++
                        }
                        exerciseViewModel.submitTrueFalseAnswer(userAnswer)

                        if (currentQuestionIndex == exercises.size - 1) {
                            // This is the last question
                            exerciseCompleted = true
                            // Directly navigate based on the score without setting showFeedback
                            if (trueFalseScore > 0) {
                                navController.navigate("trueFalseSuccess/$trueFalseScore")
                            } else {
                                navController.navigate("trueFalseFailure")
                            }
                        } else {
                            // Not the last question, show feedback as usual
                            isAnswerCorrect = correct
                            showFeedback = true
                        }
                    }
                )
            }
        } else {
            // Text("No true/false exercises found for this level ...")
            Text(stringResource(id = R.string.no_true_false_exercises_found))

        }
    }
}
@Composable
fun AnswerFeedbackScreen(correct: Boolean, onContinue: () -> Unit) {
    Log.d("AnswerFeedbackScreen", "Correct: $correct")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (correct) stringResource(id = R.string.feedback_correct) else stringResource(id = R.string.feedback_incorrect),
            style = MaterialTheme.typography.headlineLarge,
            color = if (correct) Color.Green else Color.Red,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(onClick = onContinue) {
            Text(stringResource(id = R.string.feedback_next_question))
        }
    }
}
@Composable
fun TrueFalseQuestion(
    statement: String,
    isTrue: Boolean,
    onAnswer: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = statement,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrueFalseOptionCard("True", Color(0xFF4CAF50), Modifier.weight(1f), onClick = { onAnswer(true) })
            Spacer(Modifier.width(16.dp)) // Space between buttons
            TrueFalseOptionCard("False", Color(0xFFF44336), Modifier.weight(1f), onClick = { onAnswer(false) })
        }
    }
}
@Composable
fun TrueFalseOptionCard(
    optionText: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier, // Added a parameter for the modifier
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp)
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor)
    ) {
        Text(optionText, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
    }
}