package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.exercise.ExerciseType
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
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.TRUE_FALSE)
    }

    val exercises by exerciseViewModel.exercises.observeAsState(initial = emptyList())
    val quizCompleted by exerciseViewModel.quizCompleted.observeAsState(false)
    val quizPassed by exerciseViewModel.quizPassed.observeAsState()

    // Handle quiz completion and navigate or show appropriate screen
    if (quizCompleted) {
        if (quizPassed == true) {
            // Show success screen
            TrueFalseSuccessScreen(navController, exerciseViewModel.score.value ?: 0)
        } else {
            // Show failure screen
            TrueFalseFailureScreen(navController) {
                exerciseViewModel.resetExercise()
            }
        }
    } else if (exercises.isNotEmpty()) {
        // Show question and options
        TrueFalseQuestion(
            statement = exercises.first().statement ?: "",
            isTrue = exercises.first().isTrue ?: false,
            exerciseViewModel = exerciseViewModel,
            navController = navController
        )
    } else {
        Text("Loading true/false exercises...")
    }
}

@Composable
fun TrueFalseQuestion(
    statement: String,
    isTrue: Boolean,
    exerciseViewModel: ExerciseViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(statement, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 24.dp))

        // True option
        TrueFalseOptionCard("True") {
            exerciseViewModel.submitAnswer(isTrue)
        }

        // False option
        TrueFalseOptionCard("False") {
            exerciseViewModel.submitAnswer(!isTrue)
        }
    }
}

@Composable
fun TrueFalseOptionCard(optionText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Text(
            optionText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
fun TrueFalseSuccessScreen(navController: NavController, score: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.congratulations),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
        )
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = "Trophy",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(id = R.string.you_scored_points, score),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
        )

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

    }
}


@Composable
fun TrueFalseFailureScreen(navController: NavController, restartQuiz: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.oops_sorry),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
        )
        Icon(
            imageVector = Icons.Filled.SentimentDissatisfied,
            contentDescription = "Sad face",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(id = R.string.dont_worry_try_again),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
        )
        Card(
            onClick = restartQuiz,
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
                .width(200.dp) // Set the width to a specific value or use Modifier.fillMaxWidth() for full width
                .height(100.dp), // Set the height to a specific value
            shape = RoundedCornerShape(16.dp), // Use a larger value for more rounded corners
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(id = R.string.try_again), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
        }


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

    }
}


