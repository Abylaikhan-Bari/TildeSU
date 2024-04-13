package com.ashim_bari.tildesu.view.screens.lessons

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashim_bari.tildesu.viewmodel.lessons.LevelLessonsViewModel

@SuppressLint("RememberReturnType")
@Composable
fun LevelLessons(level: String, lessonId: String) {
    // Get the ViewModel scoped to this screen
    val levelLessonsViewModel: LevelLessonsViewModel = hiltViewModel()
    // Remember is used to avoid calling the fetch function on every recomposition
    remember(level, lessonId) {
        levelLessonsViewModel.fetchLesson(level, lessonId)
    }

    // Observe the lesson LiveData from the ViewModel
    val lesson = levelLessonsViewModel.lesson.observeAsState()

    // Display the lesson's title and description if it exists
    lesson.value?.let { lesson ->
        Text(lesson.title)
        Text(lesson.description) // Assuming 'description' is part of your Lesson model
    }
}
