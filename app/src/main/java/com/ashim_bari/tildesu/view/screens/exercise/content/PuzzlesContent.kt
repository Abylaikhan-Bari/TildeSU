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

    // React to changes in level and load exercises
    LaunchedEffect(level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.PUZZLES)
    }

    // React to feedback message changes to trigger side effects
    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage == "Correct! Well done.") {
            delay(1500) // Delay to let user read the feedback
            feedbackMessage = null // Clear feedback
            exerciseViewModel.moveToNextQuestion() // Move to the next question
        }
        // Handle incorrect feedback if needed
    }

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
                }
            )
        } else {
            Text("Loading puzzles...")
        }
        if (feedbackMessage == "Correct! Well done.") {
            LaunchedEffect(key1 = currentQuestionIndex) {
                delay(1500) // Allow time for the user to see the message
                exerciseViewModel.moveToNextQuestion()
                feedbackMessage = null
            }
        }
    }
}



@Composable
fun DraggableWordPuzzle(
    puzzle: Exercise,
    onPuzzleSolved: (Boolean) -> Unit
) {
    // Ensure the words are not shuffled every recomposition
    var words by remember(puzzle.sentenceParts) {
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
                    targetIndex = (index + change).coerceIn(words.indices)
                }
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