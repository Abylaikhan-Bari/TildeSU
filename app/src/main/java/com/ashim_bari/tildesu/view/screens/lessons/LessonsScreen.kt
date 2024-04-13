package com.ashim_bari.tildesu.view.screens.lessons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.lessons.LessonsViewModel

@Composable
fun LessonsScreen(navController: NavHostController, level: String) {
    // Get the ViewModel scoped to this screen
    val lessonsViewModel: LessonsViewModel = hiltViewModel()
    lessonsViewModel.fetchLessonsForLevel(level)

    // Observe the lessons LiveData from the ViewModel
    val lessons = lessonsViewModel.lessons.observeAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        // Loop through the lessons and create a card for each one
        lessons.value.forEach { lesson ->
            LessonCard(lesson.title, onClick = {
                // Navigate to LevelLessons screen with the lesson information
                navController.navigate("levelLessons/$level/${lesson.id}")
            })
        }
    }
}

@Composable
fun LessonCard(lessonName: String, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Text(lessonName)
    }
}
