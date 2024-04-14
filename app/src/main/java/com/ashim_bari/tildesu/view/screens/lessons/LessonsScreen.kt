package com.ashim_bari.tildesu.view.screens.lessons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.lesson.Lesson
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseTypeSelectionScreen
import com.ashim_bari.tildesu.viewmodel.lessons.LessonsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(navController: NavHostController, level: String) {
    val lessonsViewModel: LessonsViewModel = hiltViewModel()
    lessonsViewModel.fetchLessonsForLevel(level)
    val lessons = lessonsViewModel.lessons.observeAsState(initial = emptyList())
    val stringResource = LocalContext.current.resources
    val (selectedTabIndex, setSelectedTabIndex) = remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource.getString(R.string.lessons_for_level, level), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]))
                }
            ) {
                Tab(
                    text = { Text("Lessons") },
                    selected = selectedTabIndex == 0,
                    onClick = { setSelectedTabIndex(0) }
                )
                Tab(
                    text = { Text("Exercise Type Selection") },
                    selected = selectedTabIndex == 1,
                    onClick = { setSelectedTabIndex(1) }
                )
            }

            if (selectedTabIndex == 0) {
                LessonsContent(lessons = lessons.value, level = level, navController = navController)
            } else if (selectedTabIndex == 1) {
                ExerciseTypeSelectionScreen(navController = navController, level = level)
            }
        }
    }
}

@Composable
fun LessonsContent(lessons: List<Lesson>, level: String, navController: NavHostController) {
    lessons.forEach { lesson ->
        LessonOptionCard(lesson.title, onClick = {
            navController.navigate("levelLessons/$level/${lesson.id}")
        })
    }
}
@Composable
fun ExerciseSelectionCard(navController: NavHostController, level: String) {
    val stringResource = LocalContext.current.resources
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("exerciseTypeSelection/$level") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = stringResource.getString(R.string.exercise_selection),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )
    }
}
@Composable
fun LessonOptionCard(optionName: String, onClick: () -> Unit) {
    val stringResource = LocalContext.current.resources
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
            text = optionName,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(16.dp)
        )
    }
}
