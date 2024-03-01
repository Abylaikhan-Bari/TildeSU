package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
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
    LaunchedEffect(level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.PUZZLES)
    }

    val puzzles by exerciseViewModel.exercises.observeAsState(initial = emptyList())
    val currentQuestionIndex by exerciseViewModel.currentQuestionIndex.observeAsState()
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    // Display feedback message if any
    feedbackMessage?.let {
        // Using Snackbar for immediate feedback; adjust as needed for your UI
        Snackbar { Text(text = it) }
        // Automatically clear the feedback message after some delay
        LaunchedEffect(it) {
            delay(2000)
            feedbackMessage = null
        }
    }

    if (puzzles.isNotEmpty() && currentQuestionIndex != null) {
        val currentPuzzle = puzzles[currentQuestionIndex!!]
        DraggableWordPuzzle(
            puzzle = currentPuzzle,
            onPuzzleSolved = { correct ->
                feedbackMessage = if (correct) "Correct! Well done." else "Incorrect. Please try again."
                if (correct) {
                    // Proceed only if the answer is correct
                    if (currentQuestionIndex!! < puzzles.size - 1) {
                        exerciseViewModel.moveToNextQuestion()
                    } else {
                        exerciseViewModel.completeExercise()
                        // Optionally navigate to a completion screen
                    }
                }
            }
        )
    } else {
        Text("Loading puzzles...")
    }
}


// Implement DraggableWordPuzzle and DraggableCard as before


// ... rest of your code remains the same

@Composable
fun DraggableWordPuzzle(
    puzzle: Exercise,
    onPuzzleSolved: (Boolean) -> Unit
) {
    var words = remember { mutableStateListOf<String>().also { it.addAll(puzzle.sentenceParts?.shuffled() ?: listOf()) } }
    var draggedIndex by remember { mutableStateOf(-1) }
    var targetIndex by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Arrange the words into a sentence:")

        words.forEachIndexed { index, word ->
            DraggableCard(
                word = word,
                onDragEnd = {
                    if (draggedIndex != -1 && targetIndex != -1 && draggedIndex != targetIndex) {
                        words.removeAt(draggedIndex).also {
                            words.add(targetIndex, it)
                        }
                    }
                    draggedIndex = -1
                    targetIndex = -1
                },
                onDragChange = { change ->
                    if (draggedIndex == -1) draggedIndex = index
                    val newIndex = (index + change).coerceIn(words.indices)
                    targetIndex = newIndex
                }
            )
        }

        Button(onClick = {
            // Check if both sentenceParts and correctOrder are not null
            if (puzzle.sentenceParts != null && puzzle.correctOrder != null) {
                // Create a list of indices based on the current order of words in comparison to the original sentence parts
                val userOrderIndices = words.map { word -> puzzle.sentenceParts!!.indexOf(word) }
                // Now compare the user's ordered indices with the puzzle's correct order
                val isCorrect = userOrderIndices == puzzle.correctOrder
                onPuzzleSolved(isCorrect)
            } else {
                // Handle the case where sentenceParts or correctOrder is null
                onPuzzleSolved(false)
            }
        }) {
            Text("Submit")
        }

    }
}

// ... rest of your code remains the same


@Composable
fun DraggableCard(
    word: String,
    onDragEnd: () -> Unit,
    onDragChange: (Int) -> Unit
) {
    var offset by remember { mutableStateOf(0f) }
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .offset(y = offset.dp)
            .pointerInput(Unit) {
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