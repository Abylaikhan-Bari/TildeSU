package com.ashim_bari.tildesu.view.screens.lessons

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.lessons.LevelLessonsViewModel

@SuppressLint("RememberReturnType")
@Composable
fun LevelLessons(navController: NavHostController, level: String, lessonId: String) {
    val levelLessonsViewModel: LevelLessonsViewModel = hiltViewModel()
    LaunchedEffect(level, lessonId) {
        levelLessonsViewModel.fetchLesson(level, lessonId)
    }

    val lesson by levelLessonsViewModel.lesson.observeAsState()

    lesson?.let { lessonContent ->
        Column {
            Text(lessonContent.title)
            Text(lessonContent.description)
            Text(lessonContent.content)
        }
    } ?: Text("Loading lesson content...")
}