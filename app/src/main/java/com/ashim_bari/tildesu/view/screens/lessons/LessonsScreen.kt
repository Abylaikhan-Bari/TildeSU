package com.ashim_bari.tildesu.view.screens.lessons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import com.ashim_bari.tildesu.model.lesson.Lesson
import com.ashim_bari.tildesu.view.screens.exercise.ExerciseTypeSelectionScreen
import com.ashim_bari.tildesu.viewmodel.lessons.LessonsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun LessonsScreen(navController: NavHostController, level: String) {
    val lessonsViewModel: LessonsViewModel = hiltViewModel()
    lessonsViewModel.fetchLessonsForLevel(level)
    val lessons = lessonsViewModel.lessons.observeAsState(initial = emptyList())
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()  // Coroutine scope for launching suspend functions
    val stringResource = LocalContext.current.resources
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
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    val indicatorWidth = tabPositions[pagerState.currentPage].width
                    TabRowDefaults.Indicator(
                        modifier = Modifier.fillMaxWidth(fraction = 1f)
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .width(indicatorWidth),
                        height = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    text = { Text(stringResource(id = R.string.lesson),) },
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(0)
                        }
                    }
                )
                Tab(
                    text = { Text(stringResource(id = R.string.exercise_selection),) },
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(1)
                        }
                    }
                )
            }

            HorizontalPager(
                count = 2,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> LessonsContent(lessons = lessons.value, level = level, navController = navController)
                    1 -> ExerciseTypeSelectionScreen(navController = navController, level = level)
                }
            }
        }
    }
}

@Composable
fun LessonsContent(lessons: List<Lesson>, level: String, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        lessons.forEachIndexed { index, lesson ->
            AnimatedLessonOptionCard(index, lesson.title) {
                navController.navigate("levelLessons/$level/${lesson.id}")
            }
        }
    }
}

@Composable
fun AnimatedLessonOptionCard(index: Int, optionName: String, onClick: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = "animation_$optionName") { // Unique key for each card
        delay(100L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + expandIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + shrinkOut(animationSpec = tween(300))
    ) {
        LessonOptionCard(optionName, onClick)
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

