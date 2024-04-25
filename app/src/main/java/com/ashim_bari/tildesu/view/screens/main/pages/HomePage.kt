package com.ashim_bari.tildesu.view.screens.main.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ashim_bari.tildesu.R
import kotlinx.coroutines.delay

@Composable
fun HomePage(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            val levels = listOf(
                "A1", "A2", "B1", "B2", "C1", "C2"
            )
            val routes = listOf(
                "exercise/A1",
                "exercise/A2",
                "exercise/B1",
                "exercise/B2",
                "exercise/C1",
                "exercise/C2"
            )
            levels.zip(routes).forEachIndexed { index, (level, route) ->
                if (index % 2 == 0) {
                    // Align to start
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CardComponent(level = level, navController = navController, index = index)
                    }
                } else {
                    // Align to end
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CardComponent(level = level, navController = navController, index = index)

                    }
                }
            }
        }
    }
}

@Composable
fun CardComponent(level: String, navController: NavHostController, index: Int) {
    var visible by rememberSaveable { mutableStateOf(false) }
    var animatedOnce by rememberSaveable { mutableStateOf(false) }

    // LaunchedEffect to trigger the animation once when the page is first composed
    LaunchedEffect(animatedOnce) {
        if (!animatedOnce) {
            delay(100L * index)
            visible = true
            animatedOnce = true
        }
    }

    // Determine the string resource ID based on the level
    val levelResId = when (level) {
        "A1" -> R.string.level_a1
        "A2" -> R.string.level_a2
        "B1" -> R.string.level_b1
        "B2" -> R.string.level_b2
        "C1" -> R.string.level_c1
        "C2" -> R.string.level_c2
        else -> R.string.level_a1 // Default case or error handling
    }

    // AnimatedVisibility for animating the card entrance
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
    ) {
        Card(
            onClick = {
                // Navigate using the level as a parameter to find the correct route
                navController.navigate("lessons/$level")
            },
            modifier = Modifier
                .size(width = 200.dp, height = 100.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = levelResId),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}