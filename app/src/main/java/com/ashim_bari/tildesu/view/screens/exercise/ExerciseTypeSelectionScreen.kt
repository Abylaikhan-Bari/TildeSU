package com.ashim_bari.tildesu.view.screens.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.exercise.ExerciseType
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseTypeSelectionScreen(navController: NavController, level: String) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            // Adding the top app bar
            SmallTopAppBar(
                title = { Text(text = level,color = Color.White) } ,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(id = R.string.select_exercise_type),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExerciseType.values().forEachIndexed { index, exerciseType ->
                    AnimatedExerciseTypeCard(index, exerciseType) {
                        // Ensure your navigation route here matches the NavGraph definition
                        navController.navigate("specificExercise/$level/${exerciseType.name.lowercase(Locale.getDefault())}")
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedExerciseTypeCard(index: Int, exerciseType: ExerciseType, onClick: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = "init") {
        delay(100L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + expandIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + shrinkOut(animationSpec = tween(300))
    ) {
        ExerciseTypeCard(exerciseType, onClick)
    }
}

//@Composable
//fun ExerciseTypeCard(exerciseType: ExerciseType, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant,
//            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
//        ),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Text(
//            text = exerciseType.name.replace('_', ' ')
//                .lowercase(Locale.getDefault())
//                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
//            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
//            modifier = Modifier.padding(16.dp)
//        )
//    }
//}
@Composable
fun ExerciseTypeCard(exerciseType: ExerciseType, onClick: () -> Unit) {
    val exerciseTypeName = when (exerciseType) {
        ExerciseType.QUIZ -> stringResource(id = R.string.quiz)
        ExerciseType.PUZZLES -> stringResource(id = R.string.puzzle)
        ExerciseType.TRUE_FALSE -> stringResource(id = R.string.true_false)
        ExerciseType.IMAGE_QUIZZES -> stringResource(id = R.string.image_quiz)
        ExerciseType.DICTIONARY_CARDS -> stringResource(id = R.string.dictionary_cards)

    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = exerciseTypeName,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(16.dp)
        )
    }
}
