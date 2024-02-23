import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
            val levels = listOf("A1 Level", "A2 Level", "B1 Level", "B2 Level", "C1 Level", "C2 Level")
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CardComponent(level: String, route: String, navController: NavHostController, index: Int) {
    // Manage the visibility state to trigger the animation
    var visible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        // Add a start delay based on the card index
        delay(100L * index)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center), // Define your enter animation here
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center), // Define your exit animation here
    ) {
        Card(
            onClick = { navController.navigate(route) },
            modifier = Modifier
                .size(width = 160.dp, height = 100.dp) // Set the size as needed
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
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
