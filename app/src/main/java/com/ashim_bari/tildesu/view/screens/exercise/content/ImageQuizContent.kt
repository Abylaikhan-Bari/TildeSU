package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.view.screens.FailureScreen
import com.ashim_bari.tildesu.view.screens.SuccessScreen
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel

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
    val imageQuizPassed by exerciseViewModel.imageQuizPassed.observeAsState()
    BackHandler {
        showConfirmationDialog()
    }
    if (exerciseCompleted) {
        // Handle quiz completion
        imageQuizPassed?.let { passed ->
            if (passed) {
                val score = exerciseViewModel.imageQuizScore.value ?: 0
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (exercises.isEmpty()) {
                        // Display message if no image quizzes found
                        Text(
                            text = "No Image quizzes for this level found.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (currentQuestionIndex < exercises.size) {
                        val exercise = exercises[currentQuestionIndex]
                        LinearProgressIndicator(
                            progress = { currentQuestionIndex.toFloat() / exercises.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        )
                        Text(
                            text = exercise.imageQuestion ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Exercise Image
                        Image(
                            painter = rememberImagePainter(exercise.imageUrl),
                            contentDescription = "Quiz Image",
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth()
                        )
                        Spacer(Modifier.height(24.dp))
                        OptionsGrid(
                            options = exercise.imageOptions ?: emptyList(),
                            selectedOption = selectedOption,
                            onSelectOption = { selectedOption = it }
                        )
                        Spacer(Modifier.height(32.dp))
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
}

@Composable
fun OptionsGrid(
    options: List<String>,
    selectedOption: Int,
    onSelectOption: (Int) -> Unit
) {
    // Create a grid layout using a Column and Rows.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // We create rows for each pair of options.
        options.chunked(2).forEachIndexed { rowIndex, pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                pair.forEachIndexed { index, option ->
                    // Calculate the actual index based on row and column
                    val actualIndex = rowIndex * 2 + index
                    ImageOptionCard(
                        option = option,
                        isSelected = selectedOption == actualIndex,
                        onSelect = { onSelectOption(actualIndex) },
                        modifier = Modifier.weight(1f) // Divide space equally
                    )
                }
            }
        }
    }
}

@Composable
fun ImageOptionCard(
    option: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp) // Adjust padding if needed
            .height(56.dp) // Suggested height for visibility
            // Remove aspectRatio if setting height, or adjust aspectRatio accordingly
            .fillMaxWidth(), // Remove if you want to set a specific width
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onSelect
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp) // Adjust padding if needed
            )
        }
    }
}