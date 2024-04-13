package com.ashim_bari.tildesu.view.screens.lessons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.lessons.LessonsViewModel

@Composable
fun LessonsScreen(navController: NavHostController, level: String) {
    val lessonsViewModel: LessonsViewModel = hiltViewModel()
    lessonsViewModel.fetchLessonsForLevel(level)

    val lessons = lessonsViewModel.lessons.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Select an Option for $level", style = MaterialTheme.typography.headlineMedium)

        // Button for navigating to the exercise selection screen
        LessonOptionCard("Exercise Selection", onClick = {
            navController.navigate("exerciseTypeSelection/$level")
        })

        Spacer(modifier = Modifier.height(16.dp)) // Add space between the options

        // Cards for specific lessons
        lessons.value.forEach { lesson ->
            LessonOptionCard(lesson.title, onClick = {
                navController.navigate("levelLessons/$level/${lesson.id}")
            })
        }
    }
}

@Composable
fun LessonOptionCard(optionName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(optionName, style = MaterialTheme.typography.titleMedium)
        }
    }
}
