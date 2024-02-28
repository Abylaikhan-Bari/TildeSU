package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import java.util.Locale

@Composable
fun ExerciseTypeSelectionScreen(navController: NavController, level: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select an Exercise Type", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        ExerciseType.values().forEach { exerciseType ->
            ExerciseTypeCard(exerciseType) {
                // Ensure your navigation route here matches the NavGraph definition
                navController.navigate("specificExercise/$level/${exerciseType.name.lowercase(Locale.getDefault())}")
            }
        }
    }
}

@Composable
fun ExerciseTypeCard(exerciseType: ExerciseType, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = exerciseType.name.split('_').joinToString(" ") { it.lowercase(Locale.getDefault()).replaceFirstChar { char -> char.uppercase() } },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}
