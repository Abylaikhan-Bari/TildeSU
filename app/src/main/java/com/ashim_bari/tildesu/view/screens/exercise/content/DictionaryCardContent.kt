package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModel
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@Composable
fun DictionaryCardContent(
    navController: NavController,
    level: String,
    exerciseType: ExerciseType,
    exerciseViewModelFactory: ExerciseViewModelFactory
) {
    // Assume you have a ViewModel instance, which provides the list of dictionary cards
    // and a function to update the current card index
    val exerciseViewModel = exerciseViewModelFactory.create(ExerciseViewModel::class.java)

    // Observe the exercises LiveData from the ViewModel
    val dictionaryCards by exerciseViewModel.exercises.observeAsState(initial = emptyList())

    // Card display state to show/hide translations
    var showTranslations by rememberSaveable { mutableStateOf(false) }

    // Assuming the dictionaryCards list holds all the cards for the current level
    LazyColumn {
        items(dictionaryCards) { card ->
            DictionaryCard(
                word = card.wordKazakh ?: "",
                onClick = { showTranslations = !showTranslations }
            )
            if (showTranslations) {
                Translations(
                    russianTranslation = card.wordRussian ?: "",
                    englishTranslation = card.wordEnglish ?: ""
                )
            }
        }
    }
}

@Composable
fun DictionaryCard(word: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },

    ) {
        Text(
            text = word,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun Translations(russianTranslation: String, englishTranslation: String) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Russian: $russianTranslation",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "English: $englishTranslation",
            style = MaterialTheme.typography.bodySmall
        )
    }
}