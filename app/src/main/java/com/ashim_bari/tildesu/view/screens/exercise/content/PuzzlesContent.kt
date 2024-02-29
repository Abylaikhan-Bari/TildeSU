package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

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

    if (puzzles.isEmpty()) {
        Text("Loading puzzles...")
    } else {
        val firstPuzzle = puzzles.first()
        DraggableWordPuzzle(puzzle = firstPuzzle)
    }
}

@Composable
fun DraggableWordPuzzle(puzzle: Exercise) {
    val initialWords = puzzle.sentenceParts ?: listOf()
    var words by remember { mutableStateOf(initialWords.shuffled()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Puzzle: Arrange the words into a sentence", modifier = Modifier.padding(bottom = 8.dp))
        words.forEach { word ->
            Text(word, modifier = Modifier.padding(2.dp))
        }
        Button(onClick = { words = initialWords.shuffled() }) {
            Text("Shuffle Words")
        }
    }
}


