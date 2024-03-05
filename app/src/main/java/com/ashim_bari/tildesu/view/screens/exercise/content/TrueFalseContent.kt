package com.ashim_bari.tildesu.view.screens.exercise.content

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.view.screens.FailureScreen
import com.ashim_bari.tildesu.view.screens.SuccessScreen
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@Composable
fun TrueFalseContent(
    navController: NavController,
    level: String,
    type: ExerciseType,
    exerciseViewModelFactory: ExerciseViewModelFactory
) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    LaunchedEffect(key1 = level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, type)
    }

    val exercises by exerciseViewModel.exercises.observeAsState(initial = emptyList())
    val currentQuestionIndex by exerciseViewModel.currentExercisesIndex.observeAsState(0)
    val exerciseCompleted by exerciseViewModel.exerciseCompleted.observeAsState(false)
    val totalQuestions = exercises.size
    var correctAnswers by rememberSaveable { mutableStateOf(0) }
    var showFeedback by rememberSaveable { mutableStateOf(false) }
    var isAnswerCorrect by rememberSaveable { mutableStateOf(false) }

    if (exerciseCompleted) {
        // Check if all answers are correct to decide which screen to show
        if (correctAnswers == totalQuestions) {
            SuccessScreen(navController, exerciseViewModel.trueFalseScore.value ?: 0)
        } else {
            FailureScreen(navController) {
                exerciseViewModel.resetExercise()
            }
        }
    } else if (exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
        val currentExercise = exercises[currentQuestionIndex]

        if (!showFeedback) {
            TrueFalseQuestion(
                statement = currentExercise.statement ?: "",
                isTrue = currentExercise.isTrue ?: false,
                onAnswer = { userAnswer ->
                    isAnswerCorrect = userAnswer == currentExercise.isTrue
                    if (isAnswerCorrect) {
                        correctAnswers += 1
                    }
                    showFeedback = true
                    exerciseViewModel.submitTrueFalseAnswer(isAnswerCorrect)
                }
            )
        } else {
            AnswerFeedbackScreen(isAnswerCorrect) {
                showFeedback = false
                exerciseViewModel.moveToNextTrueFalse()
            }
        }
    } else {
        Text("Loading true/false exercises...")
    }
}



@Composable
fun AnswerFeedbackScreen(correct: Boolean, onContinue: () -> Unit) {
    Log.d("AnswerFeedbackScreen", "Correct: $correct")
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (correct) "Correct!" else "Incorrect",
            style = MaterialTheme.typography.headlineLarge,
            color = if (correct) Color.Green else Color.Red,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(onClick = onContinue) {
            Text("Next Question")
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
            statement,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        TrueFalseOptionCard("True") {
            onAnswer(true) // Pass true to the onAnswer callback for the "True" option.
        }

        TrueFalseOptionCard("False") {
            onAnswer(false) // Pass false to the onAnswer callback for the "False" option.
        }
    }
}




@Composable
fun TrueFalseOptionCard(optionText: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .height(56.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(optionText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}





