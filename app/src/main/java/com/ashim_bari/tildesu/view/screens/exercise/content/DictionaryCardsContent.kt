package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashim_bari.tildesu.model.exercise.Exercise
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel

@Composable
fun DictionaryCardsContent(
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    val dictionaryCards by exerciseViewModel.exercises.observeAsState(initial = emptyList())

    LazyColumn {
        items(dictionaryCards) { card ->
            DictionaryCard(card = card) {
                // This lambda can be triggered when a card is clicked.
                // Perhaps toggle visibility of translations or navigate to a detail view.
            }
        }
    }
}

@Composable
fun DictionaryCard(card: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = card.wordEnglish.orEmpty(), style = MaterialTheme.typography.headlineSmall)
            // Assume there is some state here to show/hide translations
        }
    }
}
