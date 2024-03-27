package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.view.screens.FailureScreen
import com.ashim_bari.tildesu.view.screens.SuccessScreen
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageQuizContent(
    navController: NavController,
    level: String,
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableStateOf(-1) }

    LaunchedEffect(key1 = level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.IMAGE_QUIZZES)
    }

    fun showConfirmationDialog() {
        showDialog = true
    }

    val exercises by exerciseViewModel.exercises.observeAsState(initial = emptyList())
    val currentQuestionIndex by exerciseViewModel.currentExercisesIndex.observeAsState(0)
    val exerciseCompleted by exerciseViewModel.exerciseCompleted.observeAsState(false)
    val quizPassed by exerciseViewModel.quizPassed.observeAsState()

    BackHandler {
        showConfirmationDialog()
    }

    if (exerciseCompleted) {
        quizPassed?.let { passed ->
            if (passed) {
                SuccessScreen(navController, exerciseViewModel.quizScore.value ?: 0)
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (exercises.isNotEmpty() && currentQuestionIndex < exercises.size) {
                        ImageQuizCard(
                            quiz = exercises[currentQuestionIndex],
                            selectedOption = selectedOption,
                            onSelectOption = { selectedOption = it }
                        )

                        Button(
                            onClick = {
                                if (selectedOption != -1) {
                                    exerciseViewModel.submitQuizAnswer(selectedOption)
                                    selectedOption = -1
                                }
                            },
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            enabled = selectedOption != -1
                        ) {
                            Text(stringResource(id = R.string.button_next))
                        }
                    }
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
                        navController.popBackStack()
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
fun ImageQuizCard(
    quiz: Exercise,
    selectedOption: Int,
    onSelectOption: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberImagePainter(quiz.imageUrl),
                contentDescription = "Quiz Image",
                modifier = Modifier.height(180.dp)
            )
            Spacer(Modifier.height(8.dp))
            quiz.imageOptions?.let { options ->
                options.forEachIndexed { index, option ->
                    OptionCard(
                        option = option,
                        isSelected = selectedOption == index,
                        onSelect = { onSelectOption(index) }
                    )
                }
            }
        }
    }
}

