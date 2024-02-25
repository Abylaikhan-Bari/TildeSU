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
                stringResource(id = R.string.level_a1),
                stringResource(id = R.string.level_a2),
                stringResource(id = R.string.level_b1),
                stringResource(id = R.string.level_b2),
                stringResource(id = R.string.level_c1),
                stringResource(id = R.string.level_c2)
            )
            val routes = listOf("exercise/A1", "exercise/A2", "exercise/B1", "exercise/B2", "exercise/C1", "exercise/C2")

            levels.zip(routes).forEachIndexed { index, (level, route) ->
                if (index % 2 == 0) {
                    // Align to start
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CardComponent(level, route, navController, index)
                    }
                } else {
                    // Align to end
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CardComponent(level, route, navController, index)
                    }
                }
            }
        }
    }
}


@Composable
fun CardComponent(level: String, route: String, navController: NavHostController, index: Int) {
    // Manage the visibility state to trigger the animation
    var visible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = "init") { // Use a descriptive key or comment to clarify intent
        delay(100L * index) // Add delay based on index to stagger animations
        visible = true // Trigger animation by setting visible to true
    }


    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center), // Define your enter animation here
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center), // Define your exit animation here
    ) {
        Card(
            onClick = { navController.navigate(route) },
            modifier = Modifier
                .size(width = 200.dp, height = 100.dp) // Set the size as needed
                .padding(horizontal = 8.dp, vertical = 4.dp), // Adjust padding as needed
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(16.dp) // Add padding inside the card for the text
            ) {
                Text(
                    text = level,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
