package com.ashim_bari.tildesu.view.screens.lessons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.viewmodel.lessons.LessonsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(navController: NavHostController, level: String) {
    val lessonsViewModel: LessonsViewModel = hiltViewModel()
    lessonsViewModel.fetchLessonsForLevel(level)
    val lessons = lessonsViewModel.lessons.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = level, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
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
            // The rest of your code goes here
            Spacer(modifier = Modifier.height(16.dp)) // Optional: You might not need this spacer

            // Button for navigating to the exercise selection screen
            LessonOptionCard("Exercise Selection", onClick = {
                navController.navigate("exerciseTypeSelection/$level")
            })

            // Cards for specific lessons
            lessons.value.forEach { lesson ->
                LessonOptionCard(lesson.title, onClick = {
                    navController.navigate("levelLessons/$level/${lesson.id}")
                })
            }
        }
    }
}

@Composable
fun LessonOptionCard(optionName: String, onClick: () -> Unit) {
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
