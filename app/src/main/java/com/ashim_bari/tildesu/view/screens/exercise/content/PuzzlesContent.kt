package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory
import kotlinx.coroutines.delay
import kotlin.math.sign

@Composable
fun PuzzlesContent(
    navController: NavController,
    level: String,
    exerciseViewModelFactory: ExerciseViewModelFactory
) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)
    val puzzles by exerciseViewModel.exercises.observeAsState(initial = emptyList())
    val currentQuestionIndex by exerciseViewModel.currentQuestionIndex.observeAsState()
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val exerciseCompleted by exerciseViewModel.exerciseCompleted.observeAsState(false)
    val score by exerciseViewModel.score.observeAsState(0)
    val quizPassed by exerciseViewModel.quizPassed.observeAsState()

    LaunchedEffect(level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.PUZZLES)
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == "Correct! Well done.") {
            delay(1500)
            feedbackMessage = null
            exerciseViewModel.moveToNextQuestion()
        }
    }

    if (exerciseCompleted) {
        if (quizPassed == true) {
            PuzzleSuccessScreen(navController, score)
        } else {
            PuzzleFailureScreen(navController) {
                // Reset the exercise and possibly navigate back to the puzzle screen
                // or simply allow the user to restart the puzzles
                exerciseViewModel.resetExercise()
                navController.popBackStack()
            }
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            feedbackMessage?.let {
                Text(text = it, modifier = Modifier.padding(bottom = 8.dp))
            }

            if (puzzles.isNotEmpty() && currentQuestionIndex != null) {
                val currentPuzzle = puzzles[currentQuestionIndex!!]
                DraggableWordPuzzle(
                    puzzle = currentPuzzle,
                    onPuzzleSolved = { correct ->
                        feedbackMessage = if (correct) "Correct! Well done." else "Incorrect. Please try again."
                        if (correct) {
                            exerciseViewModel.updatePuzzleAnswer(correct)
                        }
                    },
                    currentPuzzleIndex = currentQuestionIndex!!
                )
            } else {
                Text("Loading puzzles...")
            }
        }
    }
}




@Composable
fun DraggableWordPuzzle(
    puzzle: Exercise,
    onPuzzleSolved: (Boolean) -> Unit,
    currentPuzzleIndex: Int
) {
    // The words list will be re-initialized and shuffled when currentPuzzleIndex changes
    var words by remember(currentPuzzleIndex) {
        mutableStateOf(puzzle.sentenceParts?.shuffled() ?: listOf())
    }
    var draggedIndex by remember { mutableStateOf(-1) }
    var targetIndex by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Arrange the words into a sentence:")

        words.forEachIndexed { index, word ->
            DraggableCard(
                word = word,
                onDragEnd = {
                    if (draggedIndex != -1 && targetIndex != -1 && draggedIndex != targetIndex) {
                        words = words.toMutableList().apply {
                            add(targetIndex, removeAt(draggedIndex))
                        }
                    }
                    draggedIndex = -1
                    targetIndex = -1
                },
                onDragChange = { change ->
                    if (draggedIndex == -1) draggedIndex = index
                    val newIndex = (index + change).coerceIn(words.indices)
                    targetIndex = newIndex
                },
                currentPuzzleIndex = currentPuzzleIndex,
                index = index
            )
        }

        Button(onClick = {
            val userOrderIndices = words.mapNotNull { word ->
                puzzle.sentenceParts?.indexOf(word)
            }
            val isCorrect = userOrderIndices == puzzle.correctOrder
            onPuzzleSolved(isCorrect)
        }) {
            Text("Submit")
        }
    }
}



@Composable
fun DraggableCard(
    word: String,
    onDragEnd: () -> Unit,
    onDragChange: (Int) -> Unit,
    currentPuzzleIndex: Int,
    index: Int
) {
    // Reset offset when currentPuzzleIndex or index changes
    var offset by remember(currentPuzzleIndex, index) { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .offset(y = offset.dp)
            .pointerInput(key1 = currentPuzzleIndex, key2 = index) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        offset += dragAmount
                        onDragChange(dragAmount.sign.toInt())
                    },
                    onDragEnd = {
                        offset = 0f
                        onDragEnd()
                    }
                )
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Text(word)
        }
    }
}



@Composable
fun PuzzleSuccessScreen(navController: NavController, score: Int) {
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
fun PuzzleFailureScreen(navController: NavController, restartExercise: () -> Unit) {
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
            onClick = restartExercise,
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