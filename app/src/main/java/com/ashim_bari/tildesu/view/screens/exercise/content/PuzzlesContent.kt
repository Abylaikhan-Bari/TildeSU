package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@Composable
fun PuzzlesContent(level: String, exerciseViewModelFactory: ExerciseViewModelFactory) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Assuming your ViewModel can load puzzles for the given level
    LaunchedEffect(level) {
        exerciseViewModel.loadExercisesForLevelAndType(level, ExerciseType.PUZZLES)
    }

    val puzzles by exerciseViewModel.exercises.observeAsState(initial = emptyList())

    if (puzzles.isEmpty()) {
        Text("Loading puzzles...")
    } else {
        // Example of displaying a simple word puzzle
        // This could be the first puzzle as an example
        val firstPuzzle = puzzles.first()

        // Assuming the puzzle is about arranging words into the correct order
        // This could be a draggable list or a set of selectable items that users can reorder
        DraggableWordPuzzle(puzzle = firstPuzzle)
    }
}

@Composable
fun DraggableWordPuzzle(puzzle: Exercise) {
    // Placeholder for a draggable list of words
    // This could use libraries like accompanist's draggable LazyColumn or a custom implementation
    Text("Puzzle: Arrange the words into a sentence")

    // Display the words in a reorderable list
    // You'll need to manage the state of the list, allowing the user to drag and reorder items
    // After reordering, you can provide a button for the user to submit their answer
    // For simplicity, this example just displays the puzzle's question
    puzzle.question?.let { Text(it) }
}
