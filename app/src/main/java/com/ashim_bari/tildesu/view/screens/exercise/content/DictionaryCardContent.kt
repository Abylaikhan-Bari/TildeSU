package com.ashim_bari.tildesu.view.screens.exercise.content

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import com.ashim_bari.tildesu.viewmodel.exercise.ExerciseViewModelFactory

@Composable
fun DictionaryCardContent(
    navController: NavController,
    level: String,
    exerciseType: ExerciseType,
    exerciseViewModelFactory: ExerciseViewModelFactory
) {
    // Define the UI content for a dictionary card here
    // This might display a word in Kazakh and ask for translations or vice versa
}