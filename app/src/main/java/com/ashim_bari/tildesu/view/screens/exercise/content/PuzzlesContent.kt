package com.ashim_bari.tildesu.view.screens.exercise.content

import android.util.Log
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
    val currentExerciseIndex by exerciseViewModel.currentExercisesIndex.observeAsState()
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val exerciseCompleted by exerciseViewModel.exerciseCompleted.observeAsState(false)
    val puzzleScore by exerciseViewModel.puzzleScore.observeAsState(0)
    val TAG = "PuzzlesContent"
    LaunchedEffect(level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.PUZZLES)
        Log.d(TAG, "Exercises loaded for level: $level")
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == "Correct! Well done.") {
            delay(1500)
            feedbackMessage = null
            exerciseViewModel.moveToNextPuzzle()
        }
    }

    if (exerciseCompleted) {
        PuzzleSuccessScreen(navController, puzzleScore) // Directly use puzzleScore
    }

    else {
        Column(modifier = Modifier.padding(16.dp)) {
            feedbackMessage?.let {
                Text(text = it, modifier = Modifier.padding(bottom = 8.dp))
            }

            if (puzzles.isNotEmpty() && currentExerciseIndex != null) {
                val currentPuzzle = puzzles[currentExerciseIndex!!]
                DraggableWordPuzzle(
                    puzzle = currentPuzzle,
                    onPuzzleSolved = { correct ->
                        feedbackMessage = if (correct) "Correct! Well done." else "Incorrect. Please try again."
                        // This might now be redundant or need adjustment since the logic has moved.
                    },
                    currentPuzzleIndex = currentExerciseIndex!!,
                    exerciseViewModel = exerciseViewModel // Pass the ViewModel instance here
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
    currentPuzzleIndex: Int,
    exerciseViewModel: ExerciseViewModel
) {
    // The words list will be re-initialized and shuffled when currentPuzzleIndex changes
    var words by remember(currentPuzzleIndex) {
        mutableStateOf(puzzle.sentenceParts?.shuffled() ?: listOf())
    }
    var draggedIndex by remember { mutableStateOf(-1) }
    var targetIndex by remember { mutableStateOf(-1) }
    val TAG = "DraggableWordPuzzle"
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
            val userOrderIndices = words.mapNotNull { puzzle.sentenceParts?.indexOf(it) }

            // Log user's chosen order of words
            Log.d("Puzzles", "User order: $userOrderIndices")

            val isCorrect = userOrderIndices == puzzle.correctOrder
            onPuzzleSolved(isCorrect)
            if (isCorrect) {
                // Log before submitting the answer
                Log.d("Puzzles", "Submitting correct answer")
                exerciseViewModel.submitPuzzleAnswer(userOrderIndices, puzzle)
            } else {
                // Log incorrect attempt message
                Log.d("Puzzles", "User answer is incorrect")
            }
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
    // Unique key for each card based on puzzle index and card index to ensure proper state reset
    var offset by remember(currentPuzzleIndex, index) { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .offset(y = offset.dp)
            .pointerInput(currentPuzzleIndex, index) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        offset += dragAmount
                        onDragChange(dragAmount.sign.toInt())
                    },
                    onDragEnd = {
                        offset = 0f // Reset the offset on drag end
                        onDragEnd()
                    }
                )
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Text(text = word)
        }
    }
}




@Composable
fun PuzzleSuccessScreen(navController: NavController, puzzleScore: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.congratulations),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
        )
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = "Trophy",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )

        // Displaying the score passed to the success screen
        Text(
            text = stringResource(id = R.string.you_scored_points, puzzleScore),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
        )

        // Optionally, display more detailed score information if available
        // For example, correctAnswers and totalQuestions could be additional parameters
        // Text(text = "You answered $correctAnswers out of $totalQuestions correctly!")

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



