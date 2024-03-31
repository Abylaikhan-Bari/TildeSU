package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel

@Composable
fun DictionaryCardsContent(
    level: String, // Add a parameter to specify the level
    exerciseType: ExerciseType = ExerciseType.DICTIONARY_CARDS, // Defaulting to DICTIONARY_CARDS
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    // Using LaunchedEffect to load dictionary cards for a specific level and type
    LaunchedEffect(key1 = level, key2 = exerciseType) {
        exerciseViewModel.loadExercisesForLevelAndType(level, exerciseType)
    }
    val dictionaryCards by exerciseViewModel.exercises.observeAsState(initial = emptyList())
    // Display a loading indicator while waiting for the data to load
    if (exerciseViewModel.isLoading.observeAsState(initial = true).value) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .padding(16.dp)
        )
    } else if (dictionaryCards.isEmpty()) {
        // Display a message if no dictionary cards are available
        Text(
            text = "No dictionary cards available.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        // Display the dictionary cards in a list
        LazyColumn {
            items(dictionaryCards) { card ->
                var isExpanded by rememberSaveable { mutableStateOf(false) }

                DictionaryCard(
                    card = card,
                    isExpanded = isExpanded,
                    onClick = { isExpanded = !isExpanded }
                )
            }
        }
    }
}

@Composable
fun DictionaryCard(card: Exercise, isExpanded: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = card.wordKazakh.orEmpty(), style = MaterialTheme.typography.headlineSmall)

            // Toggle visibility based on isExpanded state
            if (isExpanded) {
                Text(
                    text = "English: ${card.wordEnglish.orEmpty()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Russian: ${card.wordRussian.orEmpty()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}