// LessonsScreen.kt
package com.ashim_bari.tildesu.view.screens.lessons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun LessonsScreen(navController: NavHostController, level: String) {
    // Dummy data - replace with real data from your ViewModel
    val lessons = listOf("Lesson 1", "Lesson 2", "Lesson 3")

    Column(modifier = Modifier.fillMaxSize()) {
        for (lesson in lessons) {
            LessonCard(lesson, onClick = {
                // Navigate to LevelLessons screen with the lesson information
                navController.navigate("levelLessons/$level/$lesson")
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
