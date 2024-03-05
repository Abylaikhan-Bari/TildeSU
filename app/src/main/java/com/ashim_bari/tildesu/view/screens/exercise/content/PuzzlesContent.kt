package com.ashim_bari.tildesu.view.screens.exercise.content

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.view.navigation.Navigation
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

    // Observe the puzzle score here
    val puzzleScore by exerciseViewModel.puzzleScore.observeAsState(0)
    Log.d(TAG, "Current score observed: $puzzleScore")
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
    LaunchedEffect(exerciseCompleted, puzzleScore) {
        if (exerciseCompleted) {
            delay(100) // Short delay to ensure LiveData updates are observed
            Log.d(TAG, "Navigating to success screen with score: $puzzleScore")
            navController.navigate("success/$puzzleScore") {
                popUpTo(Navigation.MAIN_ROUTE) { inclusive = true }
            }
        }
    }



    Column(modifier = Modifier.padding(16.dp)) {

        LinearProgressIndicator(
            progress = {
                // Safely calculate the progress ensuring non-null and floating point division
                (currentExerciseIndex?.toFloat() ?: 0f) / (puzzles.size.toFloat().takeIf { it > 0 } ?: 1f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )


        Spacer(modifier = Modifier.height(16.dp)) // Add space between progress bar and instruction text

        feedbackMessage?.let {
            Text(text = it, modifier = Modifier.padding(bottom = 8.dp))
        }
        if (puzzles.isNotEmpty() && currentExerciseIndex != null) {
            val currentPuzzle = puzzles[currentExerciseIndex!!]
            DraggableWordPuzzle(
                puzzle = currentPuzzle,
                onPuzzleSolved = { correct ->
                    feedbackMessage = if (correct) "Correct! Well done." else "Incorrect. Please try again."
                },
                currentPuzzleIndex = currentExerciseIndex!!,
                exerciseViewModel = exerciseViewModel
            )
        } else {
            Text("Loading puzzles...")
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
    // The words list will be re-initialized and shuffled when currentPuzzleIndex changes.
    // This ensures the puzzle is reset correctly when the current puzzle index changes.
    var words by remember(currentPuzzleIndex) { mutableStateOf(puzzle.sentenceParts!!.shuffled()) }
    var draggedIndex by remember { mutableStateOf(-1) }
    var targetIndex by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "Arrange the words into a sentence!",
            style = MaterialTheme.typography.headlineMedium, // Adjust typography to match the design
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        words.forEachIndexed { index, word ->
            DraggableCard(
                word = word,
                onDragEnd = {
                    if (draggedIndex != -1 && targetIndex != -1 && draggedIndex != targetIndex) {
                        words = words.toMutableList().apply {
                            add(targetIndex, removeAt(draggedIndex))
                        }.also {
                            // Reset dragged and target indices after the list is modified
                            draggedIndex = -1
                            targetIndex = -1
                        }
                    }
                },
                onDragChange = { change ->
                    if (draggedIndex == -1) draggedIndex = index
                    targetIndex = (index + change.sign).coerceIn(words.indices)
                },
                currentPuzzleIndex = currentPuzzleIndex,
                index = index
            )
        }

        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
            // This maps the current order of words back to their original indices
            // to verify if the user's arrangement matches the correct order.
            val userOrderIndices = words.mapNotNull { puzzle.sentenceParts?.indexOf(it) }
            val isCorrect = userOrderIndices == puzzle.correctOrder
            onPuzzleSolved(isCorrect)
            if (isCorrect) {
                // Update the score in the ViewModel
                exerciseViewModel.submitPuzzleAnswer(userOrderIndices, puzzle)
            }
        }) {
            Text("Submit")
        }
    }
}

//@Composable
//fun SubmitButton() {
//    Button(
//        onClick = { /* Handle submit action */ },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .height(56.dp), // Match the height in the design
//        // Match the colors, elevation, and shape in the design
//        colors = ButtonDefaults.buttonColors(
//            containerColor = Color(0xFF000000), // Use the color value that matches the design
//            contentColor = Color.White
//        ),
//        elevation = ButtonDefaults.buttonElevation(4.dp)
//    ) {
//        Text(
//            text = "Submit",
//            style = MaterialTheme.typography.button // Use a style that matches the design
//        )
//    }
//}


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




